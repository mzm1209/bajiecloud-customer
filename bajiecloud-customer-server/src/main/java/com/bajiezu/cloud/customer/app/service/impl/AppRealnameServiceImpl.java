package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AppRealnameServiceImpl implements AppRealnameService {

    @Resource
    private AppFileUploadMapper appFileUploadMapper;
    @Resource
    private OssPrivateFileService ossPrivateFileService;
    @Resource
    private OcrClient ocrClient;

    @Value("${app.realname.idcard.max-file-size:10485760}")
    private long maxFileSize;
    @Value("${app.realname.oss.prefix:private/realname}")
    private String ossPrefix;
    @Value("${app.realname.oss.preview-expire-seconds:600}")
    private long previewExpireSeconds;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_MIME = Set.of("image/jpeg", "image/png");

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
        uploadDO.setFileKey(fileKey);
        uploadDO.setFileSize(file.getSize());
        uploadDO.setMimeType(file.getContentType());
        uploadDO.setFileHash(fileHash);
        uploadDO.setCreateTime(new Date());
        uploadDO.setUpdateTime(new Date());
        uploadDO.setIsDeleted(0);

        try {
            ossPrivateFileService.upload(fileKey, content, file.getContentType());
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
