package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.bajiezu.cloud.customer.app.service.AppCustomerService;
import com.bajiezu.cloud.customer.app.dto.AddressCreateRequest;
import com.bajiezu.cloud.customer.app.dto.AddressDeleteRequest;
import com.bajiezu.cloud.customer.app.dto.AddressSetDefaultRequest;
import com.bajiezu.cloud.customer.app.dto.AddressUpdateRequest;
import com.bajiezu.cloud.customer.app.vo.AddressDetailVO;
import com.bajiezu.cloud.customer.app.vo.AddressListVO;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import com.bajiezu.cloud.customer.dal.entity.CustomerAddress;
import com.bajiezu.cloud.customer.dal.entity.CustomerRealnameAuthDO;
import com.bajiezu.cloud.customer.dal.mapper.CustomerAddressMapper;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.dal.mapper.CustomerRealnameAuthMapper;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bajiezu.cloud.framework.security.po.CustomerInfo;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Date;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_ACCOUNT_ABNORMAL;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.ADDRESS_NOT_EXIST;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.ADDRESS_LIMIT_EXCEEDED;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.ADDRESS_TYPE_INVALID;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_NOT_EXIST;

@Service
public class AppCustomerServiceImpl implements AppCustomerService {

    private static final String CUSTOMER_BASE_INFO_KEY = "customer_base_info:%s";
    private static final long PROFILE_CACHE_TTL_SECONDS = 1800L;
    private static final int MAX_ADDRESS_COUNT = 20;
    private static final Set<Integer> VALID_ADDRESS_TYPES = Set.of(1, 2, 3, 4, 5, 9);

    @Value("${spring.redis.key.prefix:}")
    private String redisPrefix;
    @Value("${app.security.aes-key:}")
    private String aesKey;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerAddressMapper customerAddressMapper;
    @Resource
    private CustomerRealnameAuthMapper customerRealnameAuthMapper;

    @Override
    public AppCustomerProfileRespVO getProfile() {
        Long customerId = getCurrentCustomerId();
        String key = redisPrefix + String.format(CUSTOMER_BASE_INFO_KEY, customerId);
        String cache = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cache)) {
            try {
                return JacksonUtil.str2Obj(cache, AppCustomerProfileRespVO.class);
            } catch (IOException ignored) {
                // ignore invalid cache and fallback to db
            }
        }
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw exception(CUSTOMER_NOT_EXIST);
        }
        if (!Objects.equals(customer.getAccountStatus(), 1)) {
            throw exception(CUSTOMER_ACCOUNT_ABNORMAL);
        }
        AppCustomerProfileRespVO respVO = buildProfile(customer);
        redisTemplate.opsForValue().set(key, JacksonUtil.obj2Str(respVO), PROFILE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        return respVO;
    }

    @Override
    public List<AddressListVO> getAddressList() {
        Long customerId = getCurrentCustomerId();
        List<CustomerAddress> addresses = customerAddressMapper.selectList(new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .orderByDesc(CustomerAddress::getIsDefault)
                .orderByDesc(CustomerAddress::getUpdateTime));
        List<AddressListVO> list = new ArrayList<>();
        for (CustomerAddress address : addresses) {
            AddressListVO vo = new AddressListVO();
            vo.setId(address.getId());
            vo.setReceiverName(address.getReceiverName());
            vo.setReceiverMobile(maskMobile(decryptIfPresent(address.getReceiverMobile())));
            vo.setProvinceName(address.getProvinceName());
            vo.setCityName(address.getCityName());
            vo.setAreaName(address.getAreaName());
            vo.setAreaCode(address.getAreaCode());
            vo.setStreetAddress(address.getStreetAddress());
            vo.setFullAddress(address.getFullAddress());
            vo.setAddressType(address.getAddressType());
            vo.setAddressTag(address.getAddressTag());
            vo.setIsDefault(Objects.equals(address.getIsDefault(), 1));
            list.add(vo);
        }
        return list;
    }

    @Override
    public AddressDetailVO getAddressDetail(Long id) {
        Long customerId = getCurrentCustomerId();
        CustomerAddress address = customerAddressMapper.selectOne(new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getId, id)
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .last("limit 1"));
        if (address == null) {
            throw exception(ADDRESS_NOT_EXIST);
        }
        AddressDetailVO vo = new AddressDetailVO();
        vo.setId(address.getId());
        vo.setReceiverName(address.getReceiverName());
        vo.setReceiverMobile(decryptIfPresent(address.getReceiverMobile()));
        vo.setProvinceCode(address.getProvinceCode());
        vo.setProvinceName(address.getProvinceName());
        vo.setCityCode(address.getCityCode());
        vo.setCityName(address.getCityName());
        vo.setAreaCode(address.getAreaCode());
        vo.setAreaName(address.getAreaName());
        vo.setStreetAddress(address.getStreetAddress());
        vo.setFullAddress(address.getFullAddress());
        vo.setPostalCode(address.getPostalCode());
        vo.setAddressType(address.getAddressType());
        vo.setAddressTag(address.getAddressTag());
        vo.setLongitude(address.getLongitude());
        vo.setLatitude(address.getLatitude());
        vo.setIsDefault(Objects.equals(address.getIsDefault(), 1));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Long> createAddress(AddressCreateRequest req) {
        Long customerId = getCurrentCustomerId();
        validateAddressType(req.getAddressType());
        Long count = customerAddressMapper.selectCount(new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0));
        if (count != null && count >= MAX_ADDRESS_COUNT) {
            throw exception(ADDRESS_LIMIT_EXCEEDED);
        }
        boolean firstAddress = count == null || count == 0;
        Integer isDefault = resolveIsDefault(req.getIsDefault(), firstAddress);
        if (isDefault == 1) {
            clearDefaultByCustomerId(customerId);
        }
        CustomerAddress entity = new CustomerAddress();
        fillAddress(entity, req.getReceiverName(), req.getReceiverMobile(), req.getProvinceCode(), req.getProvinceName(), req.getCityCode(), req.getCityName(), req.getAreaCode(), req.getAreaName(), req.getStreetAddress(), req.getPostalCode(), req.getAddressType(), req.getAddressTag(), req.getLongitude(), req.getLatitude(), isDefault);
        entity.setCustomerId(customerId);
        entity.setIsDeleted(0);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setCreateBy(customerId);
        entity.setUpdatedBy(customerId);
        customerAddressMapper.insert(entity);
        Map<String, Long> resp = new HashMap<>();
        resp.put("id", entity.getId());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAddress(AddressUpdateRequest req) {
        Long customerId = getCurrentCustomerId();
        validateAddressType(req.getAddressType());
        CustomerAddress exist = customerAddressMapper.selectOne(new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getId, req.getId())
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .last("limit 1"));
        if (exist == null) {
            throw exception(ADDRESS_NOT_EXIST);
        }
        Integer isDefault = Boolean.TRUE.equals(req.getIsDefault()) ? 1 : 0;
        if (isDefault == 1) {
            clearDefaultByCustomerId(customerId);
        }
        CustomerAddress entity = new CustomerAddress();
        fillAddress(entity, req.getReceiverName(), req.getReceiverMobile(), req.getProvinceCode(), req.getProvinceName(), req.getCityCode(), req.getCityName(), req.getAreaCode(), req.getAreaName(), req.getStreetAddress(), req.getPostalCode(), req.getAddressType(), req.getAddressTag(), req.getLongitude(), req.getLatitude(), isDefault);
        entity.setId(req.getId());
        entity.setCustomerId(customerId);
        entity.setIsDeleted(0);
        entity.setUpdateTime(new Date());
        entity.setUpdatedBy(customerId);
        return customerAddressMapper.updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAddress(AddressDeleteRequest req) {
        Long customerId = getCurrentCustomerId();
        CustomerAddress exist = getByIdAndCustomerId(req.getId(), customerId);
        boolean wasDefault = Objects.equals(exist.getIsDefault(), 1);
        logicDeleteByIdAndCustomerId(req.getId(), customerId);
        if (wasDefault) {
            CustomerAddress latest = findLatestAddressByCustomerId(customerId);
            if (latest != null) {
                clearDefaultByCustomerId(customerId);
                setDefaultByIdAndCustomerId(latest.getId(), customerId);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefaultAddress(AddressSetDefaultRequest req) {
        Long customerId = getCurrentCustomerId();
        getByIdAndCustomerId(req.getId(), customerId);
        clearDefaultByCustomerId(customerId);
        setDefaultByIdAndCustomerId(req.getId(), customerId);
        return true;
    }

    private void fillAddress(CustomerAddress entity, String receiverName, String receiverMobile, String provinceCode,
                             String provinceName, String cityCode, String cityName, String areaCode, String areaName,
                             String streetAddress, String postalCode, Integer addressType, String addressTag,
                             java.math.BigDecimal longitude, java.math.BigDecimal latitude, Integer isDefault) {
        entity.setReceiverName(receiverName);
        entity.setReceiverMobile(encryptIfPresent(receiverMobile));
        entity.setProvinceCode(provinceCode);
        entity.setProvinceName(provinceName);
        entity.setCityCode(cityCode);
        entity.setCityName(cityName);
        entity.setAreaCode(areaCode);
        entity.setAreaName(areaName);
        entity.setStreetAddress(streetAddress);
        entity.setPostalCode(postalCode);
        entity.setAddressType(addressType);
        entity.setAddressTag(resolveAddressTag(addressType, addressTag));
        entity.setLongitude(longitude);
        entity.setLatitude(latitude);
        entity.setIsDefault(isDefault);
        entity.setFullAddress(buildFullAddress(provinceName, cityName, areaName, streetAddress));
    }

    private void clearDefaultByCustomerId(Long customerId) {
        customerAddressMapper.update(null, new LambdaUpdateWrapper<CustomerAddress>()
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .eq(CustomerAddress::getIsDefault, 1)
                .set(CustomerAddress::getIsDefault, 0));
    }

    private CustomerAddress getByIdAndCustomerId(Long id, Long customerId) {
        CustomerAddress address = customerAddressMapper.selectOne(new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getId, id)
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .last("limit 1"));
        if (address == null) {
            throw exception(ADDRESS_NOT_EXIST);
        }
        return address;
    }

    private void logicDeleteByIdAndCustomerId(Long id, Long customerId) {
        int rows = customerAddressMapper.update(null, new LambdaUpdateWrapper<CustomerAddress>()
                .eq(CustomerAddress::getId, id)
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .set(CustomerAddress::getIsDeleted, 1)
                .set(CustomerAddress::getIsDefault, 0)
                .set(CustomerAddress::getUpdatedBy, customerId)
                .set(CustomerAddress::getUpdateTime, new Date()));
        if (rows <= 0) {
            throw exception(ADDRESS_NOT_EXIST);
        }
    }

    private void setDefaultByIdAndCustomerId(Long id, Long customerId) {
        int rows = customerAddressMapper.update(null, new LambdaUpdateWrapper<CustomerAddress>()
                .eq(CustomerAddress::getId, id)
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .set(CustomerAddress::getIsDefault, 1)
                .set(CustomerAddress::getUpdatedBy, customerId)
                .set(CustomerAddress::getUpdateTime, new Date()));
        if (rows <= 0) {
            throw exception(ADDRESS_NOT_EXIST);
        }
    }

    private CustomerAddress findLatestAddressByCustomerId(Long customerId) {
        return customerAddressMapper.selectOne(new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getCustomerId, customerId)
                .eq(CustomerAddress::getIsDeleted, 0)
                .orderByDesc(CustomerAddress::getUpdateTime)
                .last("limit 1"));
    }

    private Integer resolveIsDefault(Boolean isDefault, boolean firstAddress) {
        if (firstAddress) {
            return 1;
        }
        return Boolean.TRUE.equals(isDefault) ? 1 : 0;
    }

    private void validateAddressType(Integer addressType) {
        if (addressType == null || !VALID_ADDRESS_TYPES.contains(addressType)) {
            throw exception(ADDRESS_TYPE_INVALID);
        }
    }

    private String resolveAddressTag(Integer addressType, String addressTag) {
        if (StrUtil.isNotBlank(addressTag)) {
            return addressTag;
        }
        return switch (addressType) {
            case 1 -> "家";
            case 2 -> "公司";
            case 3 -> "学校";
            case 4 -> "父母";
            case 5 -> "朋友";
            default -> "其他";
        };
    }

    private String buildFullAddress(String provinceName, String cityName, String areaName, String streetAddress) {
        return StrUtil.blankToDefault(provinceName, "") + StrUtil.blankToDefault(cityName, "")
                + StrUtil.blankToDefault(areaName, "") + StrUtil.blankToDefault(streetAddress, "");
    }

    private String encryptIfPresent(String plain) {
        if (StrUtil.hasBlank(plain, aesKey)) {
            return plain;
        }
        return SecureUtil.aes(aesKey.getBytes()).encryptBase64(plain);
    }

    private Long getCurrentCustomerId() {
        LoginUser<?> loginUser = AppSecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || !Objects.equals(loginUser.getUserType(), UserTypeEnum.CUSTOMER.getValue())) {
            throw exception(CUSTOMER_NOT_EXIST);
        }
        CustomerInfo loginInfo = (CustomerInfo) loginUser.getLoginInfo();
        Long customerId = loginInfo != null ? loginInfo.getCustomerId() : null;
        if (customerId == null) {
            throw exception(CUSTOMER_NOT_EXIST);
        }
        return customerId;
    }

    private AppCustomerProfileRespVO buildProfile(Customer customer) {
        AppCustomerProfileRespVO vo = new AppCustomerProfileRespVO();
        vo.setCustomerId(customer.getId());
        vo.setNickName(customer.getNickname());
        vo.setAvatarUrl(customer.getAvatarUrl());
        String mobile = decryptIfPresent(customer.getMobile());
        vo.setMobile(mobile);
        vo.setHasMobile(StrUtil.isNotBlank(mobile));
        vo.setEmail(decryptIfPresent(customer.getEmail()));
        vo.setRealName(decryptIfPresent(customer.getRealName()));
        vo.setIdCard(decryptIfPresent(customer.getIdCard()));
        vo.setRealnameStatus(customer.getRealnameStatus());
        vo.setFaceAuthStatus(customer.getFaceAuthStatus());
        vo.setGender(formatGender(customer.getGender()));
        vo.setBirthday(formatDate(customer.getBirthday()));
        vo.setAccountStatus(customer.getAccountStatus());
        vo.setPlatformName(customer.getPlatformName());
        vo.setSourceChannel(customer.getSourceChannel());
        vo.setThirdPartyId(customer.getThirdPartyId());
        vo.setThirdOpenId(customer.getThirdOpenId());
        CustomerRealnameAuthDO latestAuth = customerRealnameAuthMapper.selectOne(new LambdaQueryWrapper<CustomerRealnameAuthDO>()
                .eq(CustomerRealnameAuthDO::getCustomerId, customer.getId())
                .eq(CustomerRealnameAuthDO::getIsDeleted, 0)
                .orderByDesc(CustomerRealnameAuthDO::getId)
                .last("limit 1"));
        if (latestAuth != null) {
            vo.setEthnicity(latestAuth.getEthnicity());
            vo.setAddress(decryptIfPresent(latestAuth.getAddress()));
            vo.setIssue_authority(latestAuth.getIssueAuthority());
            vo.setId_card_valid_start(formatDate(latestAuth.getIdCardValidStart()));
            vo.setId_card_valid_end(formatDate(latestAuth.getIdCardValidEnd()));
        }
        if (customer.getLastLoginTime() != null) {
            vo.setLastLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(customer.getLastLoginTime()));
        }
        return vo;
    }

    private String decryptIfPresent(String encrypted) {
        if (StrUtil.hasBlank(encrypted, aesKey)) {
            return encrypted;
        }
        return SecureUtil.aes(aesKey.getBytes()).decryptStr(encrypted);
    }

    private String maskMobile(String mobile) {
        if (StrUtil.isBlank(mobile) || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    private String maskName(String name) {
        if (StrUtil.isBlank(name)) {
            return name;
        }
        return name.charAt(0) + "*";
    }

    private String maskIdCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() <= 8) {
            return idCard;
        }
        return idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4);
    }

    private String maskEmail(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@", 2);
        String username = parts[0];
        if (username.length() <= 2) {
            return username.charAt(0) + "***@" + parts[1];
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + parts[1];
    }

    private String formatGender(Integer gender) {
        if (gender == null) {
            return null;
        }
        if (gender == 1) {
            return "男";
        }
        if (gender == 2) {
            return "女";
        }
        return "未知";
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
