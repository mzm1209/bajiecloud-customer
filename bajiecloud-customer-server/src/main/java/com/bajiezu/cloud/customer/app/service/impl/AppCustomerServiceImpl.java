package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.bajiezu.cloud.customer.app.service.AppCustomerService;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import com.bajiezu.cloud.framework.security.po.CustomerInfo;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_ACCOUNT_ABNORMAL;
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

    @Override
    public AppCustomerProfileRespVO getProfile() {
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || !Objects.equals(loginUser.getUserType(), UserTypeEnum.CUSTOMER.getValue())) {
            throw exception(CUSTOMER_NOT_EXIST);
        }
        CustomerInfo loginInfo = (CustomerInfo) loginUser.getLoginInfo();
        Long customerId = loginInfo != null ? loginInfo.getCustomerId() : null;
        if (customerId == null) {
            throw exception(CUSTOMER_NOT_EXIST);
        }
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
