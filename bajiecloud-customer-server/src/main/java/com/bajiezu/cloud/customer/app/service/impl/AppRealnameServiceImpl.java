package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.customer.app.client.AliyunCloudauthClient;
import com.bajiezu.cloud.customer.app.client.AliyunVerifyMaterialResult;
import com.bajiezu.cloud.customer.app.dto.AppIdentityListReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppRealnameSubmitReqDTO;
import com.bajiezu.cloud.customer.app.vo.AppIdentityDetailRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdentityListItemVO;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdentityListRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameSubmitRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameStatusRespVO;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.entity.CustomerRealnameAuthDO;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.dal.mapper.CustomerRealnameAuthMapper;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import com.bajiezu.cloud.customer.utils.IdCardUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.bajiezu.cloud.common.service.UploadService;
import com.bajiezu.cloud.customer.app.client.OcrClient;
import com.bajiezu.cloud.customer.app.client.OssPrivateFileService;
import com.bajiezu.cloud.customer.app.enums.AppIdCardSideEnum;
import com.bajiezu.cloud.customer.app.service.AppRealnameService;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrBackVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrFrontVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.dal.entity.AppFileUploadDO;
import com.bajiezu.cloud.customer.dal.mapper.AppFileUploadMapper;
import com.bajiezu.cloud.framework.security.po.CustomerInfo;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class AppRealnameServiceImpl implements AppRealnameService {

    @Resource
    private AppFileUploadMapper appFileUploadMapper;
    @Resource
    private OssPrivateFileService ossPrivateFileService;
    @Resource
    private OcrClient ocrClient;

    @Resource
    private UploadService uploadService;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerRealnameAuthMapper customerRealnameAuthMapper;
    @Resource
    private AliyunCloudauthClient aliyunCloudauthClient;
    @Resource
    private Environment environment;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Value("${app.realname.idcard.max-file-size:10485760}")
    private long maxFileSize;
    @Value("${app.realname.oss.prefix:private/realname}")
    private String ossPrefix;
    @Value("${app.realname.oss.preview-expire-seconds:600}")
    private long previewExpireSeconds;
    @Value("${spring.redis.key.prefix:}")
    private String redisPrefix;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_MIME = Set.of("image/jpeg", "image/png");

    @Override
    public AppRealnameStatusRespVO getStatus() {
        LoginUser<?> loginUser = AppSecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        if (!UserTypeEnum.CUSTOMER.getValue().equals(loginUser.getUserType())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户类型非法");
        }
        Long customerId = extractCustomerId(loginUser);
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null || !Integer.valueOf(0).equals(customer.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在");
        }
        if (!Integer.valueOf(1).equals(customer.getAccountStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "账户异常");
        }

        CustomerRealnameAuthDO latestAuth = customerRealnameAuthMapper.selectOne(new LambdaQueryWrapper<CustomerRealnameAuthDO>()
                .eq(CustomerRealnameAuthDO::getCustomerId, customerId)
                .eq(CustomerRealnameAuthDO::getIsDeleted, 0)
                .orderByDesc(CustomerRealnameAuthDO::getSubmitTime)
                .orderByDesc(CustomerRealnameAuthDO::getId)
                .last("limit 1"));

        AppRealnameStatusRespVO respVO = new AppRealnameStatusRespVO();
        Integer faceAuthStatus = customer.getFaceAuthStatus() == null ? 0 : customer.getFaceAuthStatus();
        respVO.setFaceAuthStatus(faceAuthStatus);
        respVO.setFaceAuthUrl(null);
        respVO.setExpireSeconds(null);

        if (latestAuth == null) {
            Integer realnameStatus = customer.getRealnameStatus() == null ? 0 : customer.getRealnameStatus();
            respVO.setRealnameStatus(realnameStatus);
            respVO.setAuthStatus(null);
            fillStatusFields(respVO, realnameStatus);
            return respVO;
        }

        Integer authStatus = latestAuth.getAuthStatus();
        Integer realnameStatus = customer.getRealnameStatus();
        if (realnameStatus == null) {
            realnameStatus = mapAuthStatusToRealnameStatus(authStatus);
        }
        if (realnameStatus == null) {
            realnameStatus = 0;
        }
        respVO.setRealnameStatus(realnameStatus);
        respVO.setAuthStatus(authStatus);
        respVO.setFailReason(latestAuth.getFailReason());
        respVO.setAuthOrderNo(String.valueOf(latestAuth.getId()));
        respVO.setRealName(maskName(decrypt(getFirstNotBlank(customer.getRealName(), latestAuth.getRealName()))));
        respVO.setIdCard(IdCardUtil.desensitize(decrypt(getFirstNotBlank(customer.getIdCard(), latestAuth.getIdCard()))));
        fillStatusFields(respVO, realnameStatus);
        return respVO;
    }

    @Override
    public AppIdCardUploadRespVO uploadIdCard(String side, MultipartFile file) {
        LoginUser<?> loginUser = AppSecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        if (!UserTypeEnum.CUSTOMER.getValue().equals(loginUser.getUserType())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户类型非法");
        }
        Long customerId = extractCustomerId(loginUser);
        AppIdCardSideEnum cardSide = parseSide(side);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件过大");
        }
        String originalFileName = file.getOriginalFilename();
        String ext = parseExt(originalFileName);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件后缀不支持");
        }
        if (!ALLOWED_MIME.contains(StrUtil.blankToDefault(file.getContentType(), "").toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件MIME不支持");
        }

        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "读取文件失败");
        }
        String fileHash = sha256(content);
        String fileKey = buildFileKey(customerId, cardSide, ext);

        AppFileUploadDO uploadDO = new AppFileUploadDO();
        uploadDO.setCustomerId(customerId);
        uploadDO.setBizType("REALNAME");
        uploadDO.setFileType(cardSide == AppIdCardSideEnum.FRONT ? "ID_CARD_FRONT" : "ID_CARD_BACK");
        uploadDO.setFileName(originalFileName);
        uploadDO.setFileKey(originalFileName);
        uploadDO.setFileSize(file.getSize());
        uploadDO.setMimeType(file.getContentType());
        uploadDO.setFileHash(fileHash);
        uploadDO.setCreateTime(new Date());
        uploadDO.setUpdateTime(new Date());
        uploadDO.setIsDeleted(0);

        try {
            ossPrivateFileService.upload(fileKey, content, file.getContentType());
            String url = uploadService.upload(file);

            if (url == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "上传oss失败");
            }

            uploadDO.setUploadStatus(1);
            appFileUploadMapper.insert(uploadDO);
        } catch (Exception ex) {
            uploadDO.setUploadStatus(2);
            appFileUploadMapper.insert(uploadDO);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "上传失败");
        }

        Object ocrResult = null;
        try {
            ocrResult = cardSide == AppIdCardSideEnum.FRONT
                    ? ocrClient.recognizeIdCardFront(fileKey)
                    : ocrClient.recognizeIdCardBack(fileKey);
        } catch (Exception ignored) {
            // OCR失败不影响上传结果
        }

        AppIdCardUploadRespVO respVO = new AppIdCardUploadRespVO();
        respVO.setFileId(uploadDO.getId());
        respVO.setSide(cardSide.name());
        respVO.setPreviewUrl(ossPrivateFileService.generatePreviewUrl(fileKey, previewExpireSeconds));
        if (ocrResult instanceof AppIdCardOcrFrontVO || ocrResult instanceof AppIdCardOcrBackVO) {
            respVO.setOcrResult(ocrResult);
        }
        return respVO;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppRealnameSubmitRespVO submit(AppRealnameSubmitReqDTO reqDTO) {
        LoginUser<?> loginUser = AppSecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        if (!UserTypeEnum.CUSTOMER.getValue().equals(loginUser.getUserType())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户类型非法");
        Long customerId = extractCustomerId(loginUser);
        if (!Boolean.TRUE.equals(reqDTO.getAgreeAuth())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请同意实名认证协议");
        if (StrUtil.isBlank(reqDTO.getRealName()) || StrUtil.isBlank(reqDTO.getIdCard())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "姓名和身份证号不能为空");
        if (IdCardUtil.getBirthDateStr(reqDTO.getIdCard()) == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "身份证号有误");

        Customer customer = customerMapper.selectById(customerId);
        if (customer == null || !Integer.valueOf(0).equals(customer.getIsDeleted())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在");
        if (!Integer.valueOf(1).equals(customer.getAccountStatus())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "账户状态不允许实名认证");
        if (Integer.valueOf(1).equals(customer.getRealnameStatus())) return buildResp(1,0,1,"已实名",null,reqDTO.getRealName(),reqDTO.getIdCard(),null);

        AppFileUploadDO front = appFileUploadMapper.selectById(reqDTO.getIdCardFrontFileId());
        AppFileUploadDO back = appFileUploadMapper.selectById(reqDTO.getIdCardBackFileId());
        validateFile(front, customerId, "ID_CARD_FRONT");
        validateFile(back, customerId, "ID_CARD_BACK");

        String idHash = SecureUtil.sha256(reqDTO.getIdCard().toUpperCase(Locale.ROOT));
        Customer other = customerMapper.selectOne(new LambdaQueryWrapper<Customer>().eq(Customer::getIdCardHash,idHash).eq(Customer::getRealnameStatus,1).ne(Customer::getId,customerId).last("limit 1"));
        if (other != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该身份证已被其他账号实名");

        Date now = new Date();
        String encName = encrypt(reqDTO.getRealName());
        String encId = encrypt(reqDTO.getIdCard());
        String encAddr = encrypt(reqDTO.getAddress());

        CustomerRealnameAuthDO auth = new CustomerRealnameAuthDO();
        auth.setCustomerId(customerId); auth.setRealName(encName); auth.setIdCard(encId); auth.setIdCardHash(idHash);
        auth.setGender(reqDTO.getGender()); auth.setBirthday(parseDate(reqDTO.getBirthday())); auth.setEthnicity(reqDTO.getEthnicity());
        auth.setAddress(encAddr); auth.setIssueAuthority(reqDTO.getIssueAuthority());
        auth.setIdCardValidStart(parseDate(reqDTO.getValidStart())); auth.setIdCardValidEnd(parseDate(reqDTO.getValidEnd()));
        auth.setIdCardFrontFileId(front.getId()); auth.setIdCardBackFileId(back.getId()); auth.setAuthStatus(0); auth.setFaceAuthStatus(0);
        auth.setSubmitTime(now); auth.setCreateTime(now); auth.setUpdateTime(now); auth.setIsDeleted(0);
        customerRealnameAuthMapper.insert(auth);

        String frontUrl = ossPrivateFileService.generatePreviewUrl(front.getFileKey(), 600);
        String backUrl = ossPrivateFileService.generatePreviewUrl(back.getFileKey(), 600);
//        AliyunVerifyMaterialResult verifyResult = aliyunCloudauthClient.verifyMaterial(reqDTO.getRealName(), reqDTO.getIdCard(), frontUrl, backUrl);

//        auth.setFaceAuthResult(verifyResult.getRawResult()); // 本期暂存 VerifyMaterial 原始结果
//        if (verifyResult.isSuccess()) {
            auth.setAuthStatus(1); auth.setPassTime(now); auth.setFailReason(null);
            customer.setRealnameStatus(1); customer.setRealnameTime(now); customer.setFaceAuthStatus(0);
            customer.setRealName(encName); customer.setIdCard(encId); customer.setIdCardHash(idHash);
            customer.setGender(reqDTO.getGender()); customer.setBirthday(parseDate(reqDTO.getBirthday())); customer.setIsAnonymous(false);
            customer.setMobile(encrypt(reqDTO.getMobile()));
            customer.setEmail(encrypt(reqDTO.getEmail()));
            customerMapper.updateById(customer);
            customerRealnameAuthMapper.updateById(auth);
            afterSubmitUpdateRedis(customer, auth, true, reqDTO.getRealName(), reqDTO.getIdCard());
            return buildResp(1,0,1,"已实名",String.valueOf(auth.getId()),reqDTO.getRealName(),reqDTO.getIdCard(),null);
//        }
//        auth.setAuthStatus(2); auth.setFailReason(StrUtil.blankToDefault(verifyResult.getMessage(), "阿里云认证异常或服务暂不可用"));
//        customer.setRealnameStatus(2); customer.setFaceAuthStatus(0);
//        customerMapper.updateById(customer); customerRealnameAuthMapper.updateById(auth);
//        afterSubmitUpdateRedis(customerId, false, reqDTO.getRealName(), reqDTO.getIdCard());
//        return buildResp(2,0,2,"实名失败",String.valueOf(auth.getId()),reqDTO.getRealName(),reqDTO.getIdCard(),auth.getFailReason());
    }


    @Override
    public AppIdentityListRespVO identityList(AppIdentityListReqDTO reqDTO) {
        Long customerId = extractCustomerId(requireLoginUser());
        int pageNo = reqDTO.getPageNo() == null || reqDTO.getPageNo() < 1 ? 1 : reqDTO.getPageNo();
        int pageSize = reqDTO.getPageSize() == null || reqDTO.getPageSize() < 1 ? 10 : Math.min(reqDTO.getPageSize(), 50);
        LambdaQueryWrapper<CustomerRealnameAuthDO> qw = new LambdaQueryWrapper<CustomerRealnameAuthDO>()
                .eq(CustomerRealnameAuthDO::getCustomerId, customerId)
                .eq(CustomerRealnameAuthDO::getIsDeleted, 0)
                .orderByDesc(CustomerRealnameAuthDO::getSubmitTime)
                .orderByDesc(CustomerRealnameAuthDO::getId);
        if (reqDTO.getAuthStatus() != null) qw.eq(CustomerRealnameAuthDO::getAuthStatus, reqDTO.getAuthStatus());
        Long total = customerRealnameAuthMapper.selectCount(qw);
        qw.last("limit " + (pageNo - 1) * pageSize + "," + pageSize);
        java.util.List<CustomerRealnameAuthDO> rows = customerRealnameAuthMapper.selectList(qw);
        java.util.List<AppIdentityListItemVO> list = new java.util.ArrayList<>();
        for (CustomerRealnameAuthDO row : rows) {
            AppIdentityListItemVO vo = new AppIdentityListItemVO();
            vo.setId(row.getId());
            vo.setAuthStatus(row.getAuthStatus());
            vo.setRealnameStatus(mapAuthStatusToRealnameStatus(row.getAuthStatus()));
            vo.setStatusDesc(statusDesc(vo.getRealnameStatus()));
            vo.setSubmitTime(formatDateTime(row.getSubmitTime()));
            vo.setPassTime(formatDateTime(row.getPassTime()));
            vo.setRealName(maskName(decrypt(row.getRealName())));
            vo.setIdCard(IdCardUtil.desensitize(decrypt(row.getIdCard())));
            vo.setFailReason(row.getFailReason());
            list.add(vo);
        }
        AppIdentityListRespVO resp = new AppIdentityListRespVO();
        resp.setTotal(total == null ? 0L : total);
        resp.setList(list);
        return resp;
    }

    @Override
    public AppIdentityDetailRespVO identityDetail(Long id) {
        Long customerId = extractCustomerId(requireLoginUser());
        CustomerRealnameAuthDO row = customerRealnameAuthMapper.selectOne(new LambdaQueryWrapper<CustomerRealnameAuthDO>()
                .eq(CustomerRealnameAuthDO::getId, id)
                .eq(CustomerRealnameAuthDO::getCustomerId, customerId)
                .eq(CustomerRealnameAuthDO::getIsDeleted, 0)
                .last("limit 1"));
        if (row == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "记录不存在");
        Customer customer = customerMapper.selectById(customerId);
        AppIdentityDetailRespVO vo = new AppIdentityDetailRespVO();
        vo.setId(row.getId());
        vo.setAuthStatus(row.getAuthStatus());
        vo.setRealnameStatus(mapAuthStatusToRealnameStatus(row.getAuthStatus()));
        vo.setStatusDesc(statusDesc(vo.getRealnameStatus()));
        vo.setSubmitTime(formatDateTime(row.getSubmitTime()));
        vo.setPassTime(formatDateTime(row.getPassTime()));
        vo.setFailReason(row.getFailReason());
        vo.setRealName(maskName(decrypt(row.getRealName())));
        vo.setIdCard(IdCardUtil.desensitize(decrypt(row.getIdCard())));
        vo.setMobile(maskMobile(decrypt(customer == null ? null : customer.getMobile())));
        vo.setEmail(maskEmail(decrypt(customer == null ? null : customer.getEmail())));
        vo.setGender(row.getGender());
        vo.setBirthday(formatDate(row.getBirthday()));
        vo.setEthnicity(row.getEthnicity());
        vo.setAddress(decrypt(row.getAddress()));
        vo.setIssueAuthority(row.getIssueAuthority());
        vo.setValidStart(formatDate(row.getIdCardValidStart()));
        vo.setValidEnd(formatDate(row.getIdCardValidEnd()));
        vo.setIdCardFrontFileId(row.getIdCardFrontFileId());
        vo.setIdCardBackFileId(row.getIdCardBackFileId());
        AppFileUploadDO front = appFileUploadMapper.selectById(row.getIdCardFrontFileId());
        AppFileUploadDO back = appFileUploadMapper.selectById(row.getIdCardBackFileId());
        if (front != null) vo.setIdCardFrontUrl(ossPrivateFileService.generatePreviewUrl(front.getFileKey(), previewExpireSeconds));
        if (back != null) vo.setIdCardBackUrl(ossPrivateFileService.generatePreviewUrl(back.getFileKey(), previewExpireSeconds));
        return vo;
    }

    private void validateFile(AppFileUploadDO file, Long customerId, String fileType) {
        if (file == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "身份证文件不存在");
        if (!customerId.equals(file.getCustomerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "身份证文件不属于当前用户");
        if (!fileType.equals(file.getFileType())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "身份证文件类型错误");
    }
    private Date parseDate(String v){ if(StrUtil.isBlank(v)) return null; try{return java.sql.Date.valueOf(v);}catch(Exception e){return null;}}
    private String encrypt(String plain){ if(StrUtil.isBlank(plain)) return plain; String key=environment.getProperty("app.security.aes-key"); return StrUtil.isBlank(key)?plain:SecureUtil.aes(key.getBytes()).encryptBase64(plain); }
    private String decrypt(String encrypted){ if(StrUtil.isBlank(encrypted)) return encrypted; String key=environment.getProperty("app.security.aes-key"); return StrUtil.isBlank(key)?encrypted:SecureUtil.aes(key.getBytes()).decryptStr(encrypted); }
    private String maskName(String n){ if(StrUtil.isBlank(n)) return n; return n.length()==1?"*":n.charAt(0)+"*"; }
    private String getFirstNotBlank(String first, String second) { return StrUtil.isNotBlank(first) ? first : second; }
    private Integer mapAuthStatusToRealnameStatus(Integer authStatus) { if (authStatus == null) return 0; if (authStatus == 1) return 1; if (authStatus == 2) return 2; return 0; }
    private void fillStatusFields(AppRealnameStatusRespVO respVO, Integer realnameStatus) { if (realnameStatus != null && realnameStatus == 1) { respVO.setStatusDesc("已实名"); respVO.setCanSubmit(false); return; } if (realnameStatus != null && realnameStatus == 2) { respVO.setStatusDesc("实名失败"); respVO.setCanSubmit(true); return; } respVO.setStatusDesc("未实名"); respVO.setCanSubmit(true); }
    private AppRealnameSubmitRespVO buildResp(Integer rs,Integer fs,Integer as,String desc,String orderNo,String name,String id,String fail){ AppRealnameSubmitRespVO v=new AppRealnameSubmitRespVO(); v.setRealnameStatus(rs);v.setFaceAuthStatus(fs);v.setAuthStatus(as);v.setStatusDesc(desc);v.setAuthOrderNo(orderNo);v.setRealName(maskName(name));v.setIdCard(IdCardUtil.desensitize(id));v.setFailReason(fail); return v; }
    private void afterSubmitUpdateRedis(Customer customer, CustomerRealnameAuthDO auth, boolean passed, String name, String idCard){
        Long customerId = customer.getId();
        String idxKey="bajie:auth:app-user-tokens:"+customerId;
        Set<String> tokens=redisTemplate.opsForSet().members(idxKey);
        if(tokens!=null){
            Iterator<String> it=tokens.iterator();
            while(it.hasNext()){
                String t=it.next();
                String userKey="bajie:auth:app-user:"+t;
                String val=redisTemplate.opsForValue().get(userKey);
                if(StrUtil.isBlank(val)){
                    redisTemplate.opsForSet().remove(idxKey,t);
                    continue;
                }
                try{
                    Map m=JacksonUtil.str2Obj(val, Map.class);
                    m.put("realnameStatus", passed?1:2);
                    m.put("faceAuthStatus",0);
                    m.put("realnamePassed",passed);
                    if(passed){
                        m.put("realNameMasked", maskName(name));
                        m.put("idCardMasked", IdCardUtil.desensitize(idCard));
                    }
                    redisTemplate.opsForValue().set(userKey, JacksonUtil.obj2Str(m), 2, TimeUnit.HOURS);
                }
                catch(Exception ignore){

                }
            }
        }
        AppCustomerProfileRespVO profile = new AppCustomerProfileRespVO();
        profile.setCustomerId(customerId);
        profile.setNickName(customer.getNickname());
        profile.setAvatarUrl(customer.getAvatarUrl());
        profile.setMobile(decrypt(customer.getMobile()));
        profile.setHasMobile(StrUtil.isNotBlank(decrypt(customer.getMobile())));
        profile.setEmail(decrypt(customer.getEmail()));
        profile.setRealName(decrypt(customer.getRealName()));
        profile.setIdCard(decrypt(customer.getIdCard()));
        profile.setGender(customer.getGender() == null ? null : (customer.getGender() == 1 ? "男" : (customer.getGender() == 2 ? "女" : "未知")));
        profile.setBirthday(formatDate(customer.getBirthday()));
        profile.setEthnicity(auth.getEthnicity());
        profile.setAddress(decrypt(auth.getAddress()));
        profile.setIssue_authority(auth.getIssueAuthority());
        profile.setId_card_valid_start(formatDate(auth.getIdCardValidStart()));
        profile.setId_card_valid_end(formatDate(auth.getIdCardValidEnd()));
        profile.setRealnameStatus(customer.getRealnameStatus());
        profile.setFaceAuthStatus(customer.getFaceAuthStatus());
        profile.setAccountStatus(customer.getAccountStatus());
        profile.setPlatformName(customer.getPlatformName());
        profile.setSourceChannel(customer.getSourceChannel());
        profile.setThirdPartyId(customer.getThirdPartyId());
        profile.setThirdOpenId(customer.getThirdOpenId());
        profile.setLastLoginTime(formatDateTime(customer.getLastLoginTime()));
        redisTemplate.opsForValue().set(redisPrefix + "customer_base_info:" + customerId, JacksonUtil.obj2Str(profile), 30, TimeUnit.MINUTES);
    }

    private LoginUser<?> requireLoginUser() {
        LoginUser<?> loginUser = AppSecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        if (!UserTypeEnum.CUSTOMER.getValue().equals(loginUser.getUserType())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户类型非法");
        return loginUser;
    }

    private String statusDesc(Integer realnameStatus) {
        if (realnameStatus != null && realnameStatus == 1) return "已实名";
        if (realnameStatus != null && realnameStatus == 2) return "实名失败";
        return "未实名";
    }
    private String formatDateTime(Date d){ return d==null?null: new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);}
    private String formatDate(Date d){ return d==null?null: new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);}
    private String maskMobile(String m){ if(StrUtil.isBlank(m)||m.length()<7) return m; return m.substring(0,3)+"****"+m.substring(m.length()-4);}
    private String maskEmail(String e){ if(StrUtil.isBlank(e)||!e.contains("@")) return e; String[] p=e.split("@",2); String u=p[0]; if(u.length()<=1) return "***@"+p[1]; return u.charAt(0)+"***"+u.charAt(u.length()-1)+"@"+p[1];}

    private Long extractCustomerId(LoginUser<?> loginUser) {
        if (loginUser.getLoginInfo() instanceof CustomerInfo info && info.getCustomerId() != null) {
            return info.getCustomerId();
        }
        if (loginUser.getId() != null) {
            return loginUser.getId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "customerId不存在");
    }

    private AppIdCardSideEnum parseSide(String side) {
        if (StrUtil.isBlank(side)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "side不能为空");
        }
        try {
            return AppIdCardSideEnum.valueOf(side.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "side非法");
        }
    }

    private String parseExt(String filename) {
        if (StrUtil.isBlank(filename) || !filename.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件名非法");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String buildFileKey(Long customerId, AppIdCardSideEnum side, String ext) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sidePart = side.name().toLowerCase(Locale.ROOT);
        return String.format("%s/%d/%s/%s/%s-%s.%s", ossPrefix, customerId, date, sidePart,
                UUID.randomUUID().toString().replace("-", ""), RandomUtil.randomString(6), ext);
    }

    private String sha256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "计算hash失败");
        }
    }
}
