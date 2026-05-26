package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.bajiezu.cloud.alipay.AlipayProperties;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.bajiezu.cloud.customer.app.client.alipay.AlipayLoginGateway;
import com.bajiezu.cloud.customer.app.client.alipay.dto.AlipayPhoneInfo;
import com.bajiezu.cloud.customer.app.dto.AppAlipayLoginReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppMobileLoginReqDTO;
import com.bajiezu.cloud.customer.app.service.AppAuthService;
import com.bajiezu.cloud.customer.app.vo.AppLoginRespVO;
import com.bajiezu.cloud.customer.dal.entity.AppSmsCodeLogDO;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.AppSmsCodeLogMapper;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.framework.security.po.CustomerInfo;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.service.AppLoginTokenService;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private AppSmsCodeLogMapper appSmsCodeLogMapper;
    @Resource
    private Environment environment;
    @Resource
    private AppLoginTokenService appLoginTokenService;
    @Resource
    private AlipayLoginGateway alipayLoginGateway;
    @Resource
    private AlipayProperties alipayProperties;
    @Autowired
    private SpringUtil springUtil;

    @Override
    public AppLoginRespVO alipayLogin(AppAlipayLoginReqDTO reqDTO) {
        if (StrUtil.isBlank(reqDTO.getAuthCode())) {
            throw exception(LOGIN_EXCEPTION);
        }
        log.debug("开始获取ali userId/openId=====");
        AlipayPhoneInfo phoneInfo = alipayLoginGateway.getPhone(reqDTO.getAuthCode());
        String thirdPartyId = phoneInfo.getUserId();
        String thirdOpenId = phoneInfo.getOpenId();
        if (StrUtil.isBlank(thirdPartyId)) {
            thirdPartyId = alipayLoginGateway.getUserId(reqDTO.getAuthCode());
        }
        log.debug("已获取ali userId：====" + thirdPartyId + ", openId:====" + thirdOpenId);
        if (StrUtil.isBlank(thirdPartyId)) {
            throw exception(LOGIN_EXCEPTION);
        }
        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getThirdPartyId, thirdPartyId)
                .last("limit 1"));
        String mobile = phoneInfo.getMobile();
        String encryptedMobile = encryptMobileIfPresent(mobile);
        if (customer == null) {
            customer = new Customer();
            customer.setPlatformUid("alipay_userid_" + thirdPartyId);
            customer.setThirdPartyId(thirdPartyId);
            customer.setThirdOpenId(thirdOpenId);
            customer.setPlatformName("ALIPAY_MINI_APP");
            customer.setSourceChannel("AliPay");
            customer.setMobile(encryptedMobile);
            customer.setAccountStatus(1);
            customer.setCreateTime(new Date());
            customer.setUpdateTime(new Date());
            customer.setLastLoginTime(new Date());
            customer.setIsDeleted(0);
            customerMapper.insert(customer);
        } else {
            if (StrUtil.isNotBlank(encryptedMobile) && StrUtil.isBlank(customer.getMobile())) {
                customer.setMobile(encryptedMobile);
            }
            if (StrUtil.isNotBlank(thirdOpenId) && StrUtil.isBlank(customer.getThirdOpenId())) {
                customer.setThirdOpenId(thirdOpenId);
            }
            customer.setLastLoginTime(new Date());
            customer.setUpdateTime(new Date());
            customerMapper.updateById(customer);
        }
        String masked = maskMobile(mobile);

        log.debug("开始获取用户登录token");
        String alipayAppId = alipayProperties.getMiniapp() != null ? alipayProperties.getMiniapp().getAppId() : null;
        String token = createToken(customer.getId(), masked, reqDTO.getDeviceId(), "AliPay", thirdOpenId, alipayAppId);
        log.debug("已获取用户登录token："+token);

        AppLoginRespVO vo = new AppLoginRespVO();
        vo.setTokenName("app-user-token");
        vo.setToken(token);
        vo.setExpireTime(7200);
        vo.setCustomerId(customer.getId());
        vo.setHasMobile(StrUtil.isNotBlank(mobile));
        vo.setMobile(masked);
        vo.setRealnameStatus(0);
        vo.setFaceAuthStatus(0);
        vo.setAccountStatus(1);
        vo.setThirdPartyId(customer.getThirdPartyId());
        vo.setThirdOpenId(customer.getThirdOpenId());
        return vo;
    }

    @Override
    public AppLoginRespVO mobileLogin(AppMobileLoginReqDTO reqDTO) {
        if (StrUtil.hasBlank(reqDTO.getCountryCode(), reqDTO.getMobile(), reqDTO.getSmsCode())) {
            throw exception(LOGIN_EXCEPTION);
        }
        String mobileHash = SecureUtil.sha256(reqDTO.getCountryCode() + reqDTO.getMobile());
        AppSmsCodeLogDO log = appSmsCodeLogMapper.selectOne(new LambdaQueryWrapper<AppSmsCodeLogDO>()
                .eq(AppSmsCodeLogDO::getMobileHash, mobileHash)
                .eq(AppSmsCodeLogDO::getScene, "LOGIN")
                .eq(AppSmsCodeLogDO::getVerifyStatus, 0)
                .ge(AppSmsCodeLogDO::getExpireTime, new Date())
                .orderByDesc(AppSmsCodeLogDO::getId)
                .last("limit 1"));
        if (log == null || log.getVerifyCount() >= 5) throw exception(LOGIN_EXCEPTION);
        String hash = SecureUtil.sha256(reqDTO.getSmsCode() + log.getSalt());
        if (!StrUtil.equals(hash, log.getSmsCodeHash())) {
            log.setVerifyCount(log.getVerifyCount() + 1);
            log.setUpdateTime(new Date());
            appSmsCodeLogMapper.updateById(log);
            throw exception(LOGIN_EXCEPTION);
        }
        log.setVerifyStatus(1);
        log.setUpdateTime(new Date());
        appSmsCodeLogMapper.updateById(log);

        String encryptedMobile = encryptMobileIfPresent(reqDTO.getMobile());
        AlipayPhoneInfo phoneInfo = null;
        String thirdPartyId = null;
        String thirdOpenId = null;
        if (StrUtil.isNotBlank(reqDTO.getAuthCode())) {
            phoneInfo = alipayLoginGateway.getPhone(reqDTO.getAuthCode());
            thirdPartyId = phoneInfo.getUserId();
            thirdOpenId = phoneInfo.getOpenId();
            if (StrUtil.isBlank(thirdPartyId)) {
                thirdPartyId = alipayLoginGateway.getUserId(reqDTO.getAuthCode());
            }
        }

        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getMobile, encryptedMobile)
                .last("limit 1"));
        if (customer == null) {
            customer = new Customer();
            customer.setPlatformUid(buildPlatformUid(reqDTO));
            customer.setThirdPartyId(thirdPartyId);
            customer.setThirdOpenId(thirdOpenId);
            customer.setMobile(encryptedMobile);
            customer.setSourceChannel(StrUtil.blankToDefault(reqDTO.getSourceChannel(), "AliPay"));
            customer.setPlatformName("AliPay");
            customer.setAccountStatus(1);
            customer.setCreateTime(new Date());
            customer.setUpdateTime(new Date());
            customer.setLastLoginTime(new Date());
            customer.setIsDeleted(0);
            customerMapper.insert(customer);
        } else {
            customer.setLastLoginTime(new Date());
            customer.setUpdateTime(new Date());
            if (StrUtil.isNotBlank(thirdPartyId) && StrUtil.isBlank(customer.getThirdPartyId())) {
                customer.setThirdPartyId(thirdPartyId);
            }
            if (StrUtil.isNotBlank(thirdOpenId) && StrUtil.isBlank(customer.getThirdOpenId())) {
                customer.setThirdOpenId(thirdOpenId);
            }
            customerMapper.updateById(customer);
        }
        String masked = maskMobile(reqDTO.getMobile());
        String token = createToken(customer.getId(), masked, reqDTO.getDeviceId(), StrUtil.blankToDefault(reqDTO.getSourceChannel(), "AliPay"), null, null);
        AppLoginRespVO vo = new AppLoginRespVO();
        vo.setTokenName("app-user-token");
        vo.setToken(token);
        vo.setExpireTime(7200);
        vo.setCustomerId(customer.getId());
        vo.setHasMobile(true);
        vo.setMobile(masked);
        vo.setRealnameStatus(0);
        vo.setFaceAuthStatus(0);
        vo.setAccountStatus(1);
        vo.setThirdPartyId(customer.getThirdPartyId());
        vo.setThirdOpenId(customer.getThirdOpenId());
        return vo;
    }

    @Override
    public Boolean logout(String token) {
        LoginUser<?> loginUser = AppSecurityFrameworkUtils.getLoginUser();
        if (StrUtil.isBlank(token) && loginUser != null) {
            token = loginUser.getToken();
        }
        invokeDeleteToken(loginUser, token);
        clearLoginContext();
        return Boolean.TRUE;
    }

    private void invokeDeleteToken(LoginUser<?> loginUser, String token) {
        try {
            Method deleteByToken = appLoginTokenService.getClass().getMethod("deleteToken", String.class);
            deleteByToken.invoke(appLoginTokenService, token);
            return;
        } catch (NoSuchMethodException ignored) {
            // fallback to other method signatures
        } catch (Exception ignored) {
            return;
        }
        if (loginUser == null) {
            return;
        }
        try {
            Method deleteByLoginUser = appLoginTokenService.getClass().getMethod("deleteToken", LoginUser.class);
            deleteByLoginUser.invoke(appLoginTokenService, loginUser);
        } catch (Exception ignored) {
            // ignore logout delete errors to keep API idempotent
        }
    }


    private void clearLoginContext() {
        try {
            Class<?> contextClass = Class.forName("com.bajiezu.cloud.framework.security.context.AppLoginUserContext");
            try {
                contextClass.getMethod("remove").invoke(null);
                return;
            } catch (NoSuchMethodException ignored) {
                // try clear
            }
            contextClass.getMethod("clear").invoke(null);
        } catch (Exception ignored) {
            // ignore context clean failures
        }
    }

    private String createToken(Long customerId, String maskedMobile, String deviceId, String sourceChannel, String alipayOpenId, String alipayAppId) {
        CustomerInfo customerInfo = CustomerInfo.builder()
                .customerId(customerId)
                .phone(maskedMobile)
                .miniProgramOpenId(String.valueOf(customerId))
                .alipayOpenId(alipayOpenId)
                .alipayAppId(alipayAppId)
                .build();
        LoginUser<CustomerInfo> loginUser = new LoginUser<>();
        loginUser.setId(customerId);
        loginUser.setUserType(UserTypeEnum.CUSTOMER.getValue());
        loginUser.setLoginType(LoginUser.LoginType.PHONE);
        loginUser.setLoginInfo(customerInfo);
        loginUser.setLoginTime(LocalDateTime.now());
        loginUser.setLoginSource(sourceChannel);
        String token = appLoginTokenService.generateToken(loginUser);
        return StrUtil.isNotBlank(token) ? token : loginUser.getToken();
    }

    private String buildPlatformUid(AppMobileLoginReqDTO reqDTO) {
        return "app_" + reqDTO.getCountryCode().replace("+", "") + "_" + System.currentTimeMillis();
    }

    private String encryptMobileIfPresent(String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return null;
        }
        String aesKey = environment.getProperty("app.security.aes-key");
        if (StrUtil.isBlank(aesKey)) {
            throw exception(LOGIN_EXCEPTION);
        }
        return SecureUtil.aes(aesKey.getBytes()).encryptBase64(mobile);
    }

    private String maskMobile(String mobile) {
        if (StrUtil.isBlank(mobile) || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
