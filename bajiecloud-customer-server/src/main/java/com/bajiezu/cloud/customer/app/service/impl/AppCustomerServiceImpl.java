package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.bajiezu.cloud.customer.app.service.AppCustomerService;
import com.bajiezu.cloud.customer.app.vo.AddressDetailVO;
import com.bajiezu.cloud.customer.app.vo.AddressListVO;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import com.bajiezu.cloud.customer.dal.entity.CustomerAddress;
import com.bajiezu.cloud.customer.dal.mapper.CustomerAddressMapper;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bajiezu.cloud.framework.security.po.CustomerInfo;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_ACCOUNT_ABNORMAL;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.ADDRESS_NOT_EXIST;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_NOT_EXIST;

@Service
public class AppCustomerServiceImpl implements AppCustomerService {

    private static final String CUSTOMER_BASE_INFO_KEY = "customer_base_info:%s";
    private static final long PROFILE_CACHE_TTL_SECONDS = 1800L;

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
        vo.setMobile(maskMobile(mobile));
        vo.setHasMobile(StrUtil.isNotBlank(mobile));
        vo.setRealName(maskName(decryptIfPresent(customer.getRealName())));
        vo.setIdCard(maskIdCard(decryptIfPresent(customer.getIdCard())));
        vo.setRealnameStatus(0);
        vo.setFaceAuthStatus(0);
        vo.setAccountStatus(customer.getAccountStatus());
        vo.setPlatformName(customer.getPlatformName());
        vo.setSourceChannel(customer.getSourceChannel());
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
}
