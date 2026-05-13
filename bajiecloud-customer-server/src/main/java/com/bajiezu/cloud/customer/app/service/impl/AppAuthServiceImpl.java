package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.customer.app.client.alipay.AlipayLoginGateway;
import com.bajiezu.cloud.customer.app.client.alipay.dto.AlipayPhoneInfo;
import com.bajiezu.cloud.customer.app.dto.AppAlipayLoginReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppMobileLoginReqDTO;
import com.bajiezu.cloud.customer.app.service.AppAuthService;
import com.bajiezu.cloud.customer.app.vo.AppLoginRespVO;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Date;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Resource
    private AlipayLoginGateway alipayLoginGateway;
    @Resource
    private CustomerMapper customerMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment environment;

    @Override
    public AppLoginRespVO alipayLogin(AppAlipayLoginReqDTO reqDTO) {
        if (StrUtil.isBlank(reqDTO.getAuthCode())) {
            throw exception(LOGIN_EXCEPTION);
        }
        AlipayPhoneInfo phoneInfo = alipayLoginGateway.getPhone(reqDTO.getAuthCode());
        String openId = phoneInfo.getOpenId();
        if (StrUtil.isBlank(openId)) {
            openId = alipayLoginGateway.getOpenId(reqDTO.getAuthCode());
        }
        if (StrUtil.isBlank(openId)) {
            throw exception(LOGIN_EXCEPTION);
        }

        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getThirdPartyId, openId)
                .eq(Customer::getSourceChannel, "AliPay")
                .last("limit 1"));
        String encryptedMobile = encryptMobileIfPresent(phoneInfo.getMobile());
        if (customer == null) {
            customer = new Customer();
            customer.setThirdPartyId(openId);
            customer.setSourceChannel("AliPay");
            customer.setPlatformName("AliPay");
            customer.setMobile(encryptedMobile);
            customer.setCreateTime(new Date());
            customer.setUpdateTime(new Date());
            customer.setLastLoginTime(new Date());
            customer.setAccountStatus(1);
            customer.setIsDeleted(0);
            customerMapper.insert(customer);
            // TODO 如果线上 customer 表与 third_party_id 映射策略不同，需要按真实字段重新映射。
        } else {
            customer.setLastLoginTime(new Date());
            if (StrUtil.isNotBlank(encryptedMobile)) {
                customer.setMobile(encryptedMobile);
            }
            customer.setUpdateTime(new Date());
            customerMapper.updateById(customer);
        }

        String token = createToken(customer.getId(), maskMobile(phoneInfo.getMobile()), reqDTO.getDeviceId());
        AppLoginRespVO respVO = new AppLoginRespVO();
        respVO.setToken(token);
        respVO.setCustomerId(customer.getId());
        respVO.setMobile(maskMobile(phoneInfo.getMobile()));
        respVO.setRealnameStatus(0);
        respVO.setFaceAuthStatus(0);
        return respVO;
    }

    @Override
    public AppLoginRespVO mobileLogin(AppMobileLoginReqDTO reqDTO) {
        return new AppLoginRespVO();
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

    private String createToken(Long customerId, String mobile, String deviceId) {
        try {
            Object tokenService = applicationContext.getBean("appTokenService");
            Object loginUser = buildLoginUser(customerId, mobile, deviceId);
            Method method = tokenService.getClass().getMethod("createToken", loginUser.getClass());
            return (String) method.invoke(tokenService, loginUser);
        } catch (Exception e) {
            throw exception(LOGIN_EXCEPTION);
        }
    }

    private Object buildLoginUser(Long customerId, String mobile, String deviceId) throws Exception {
        Class<?> cls = Class.forName("com.bajiezu.cloud.framework.security.app.AppLoginUserInfo");
        Object obj = cls.getDeclaredConstructor().newInstance();
        cls.getMethod("setCustomerId", Long.class).invoke(obj, customerId);
        cls.getMethod("setMobile", String.class).invoke(obj, mobile);
        cls.getMethod("setUserType", String.class).invoke(obj, "APP_CUSTOMER");
        cls.getMethod("setRealnameStatus", Integer.class).invoke(obj, 0);
        cls.getMethod("setFaceAuthStatus", Integer.class).invoke(obj, 0);
        cls.getMethod("setLoginTime", Date.class).invoke(obj, new Date());
        cls.getMethod("setDeviceId", String.class).invoke(obj, deviceId);
        return obj;
    }
}
