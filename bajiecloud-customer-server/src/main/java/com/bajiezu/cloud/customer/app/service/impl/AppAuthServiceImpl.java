package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.customer.app.dto.AppAlipayLoginReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppMobileLoginReqDTO;
import com.bajiezu.cloud.customer.app.service.AppAuthService;
import com.bajiezu.cloud.customer.app.vo.AppLoginRespVO;
import com.bajiezu.cloud.customer.dal.entity.AppSmsCodeLogDO;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.AppSmsCodeLogMapper;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.framework.security.app.AppLoginTokenService;
import com.bajiezu.cloud.framework.security.app.AppLoginUserInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

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

    @Override
    public AppLoginRespVO alipayLogin(AppAlipayLoginReqDTO reqDTO) {
        throw exception(LOGIN_EXCEPTION);
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
        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getMobile, encryptedMobile)
                .last("limit 1"));
        if (customer == null) {
            customer = new Customer();
            customer.setPlatformUid(buildPlatformUid(reqDTO));
            customer.setThirdPartyId(reqDTO.getCountryCode() + "_" + reqDTO.getMobile());
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
            customerMapper.updateById(customer);
        }
        String masked = maskMobile(reqDTO.getMobile());
        String token = createToken(customer.getId(), masked, reqDTO.getDeviceId());
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
        return vo;
    }

    private String createToken(Long customerId, String mobile, String deviceId) {
        AppLoginUserInfo loginUser = new AppLoginUserInfo();
        loginUser.setCustomerId(customerId);
        loginUser.setMobile(mobile);
        loginUser.setUserType("APP_CUSTOMER");
        loginUser.setRealnameStatus(0);
        loginUser.setFaceAuthStatus(0);
        loginUser.setLoginTime(new Date());
        loginUser.setDeviceId(deviceId);
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
