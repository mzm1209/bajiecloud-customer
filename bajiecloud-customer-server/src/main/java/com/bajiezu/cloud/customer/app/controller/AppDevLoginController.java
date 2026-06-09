package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.alipay.AlipayProperties;
import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.framework.security.po.CustomerInfo;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.service.AppLoginTokenService;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_NOT_EXIST;

/**
 * TODO 联调用，上线前移除
 * 按 customerId 直接生成 C 端 token，避免每次都走支付宝沙箱登录拿 authCode。
 */
@RestController
@RequestMapping("/app/customer/dev")
public class AppDevLoginController {

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private AppLoginTokenService appLoginTokenService;

    @Resource
    private AlipayProperties alipayProperties;

    @GetMapping("/token")
    @PermitAll
    public CommonResult<Map<String, Object>> issueToken(@RequestParam("customerId") Long customerId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw exception(CUSTOMER_NOT_EXIST);
        }

        String alipayUserId = customer.getThirdPartyId();
        String alipayAppId = alipayProperties.getMiniapp() != null
                ? alipayProperties.getMiniapp().getAppId() : null;

        CustomerInfo customerInfo = CustomerInfo.builder()
                .customerId(customer.getId())
                .miniProgramOpenId(String.valueOf(customer.getId()))
                .alipayUserId(alipayUserId)
                .alipayAppId(alipayAppId)
                .build();

        LoginUser<CustomerInfo> loginUser = new LoginUser<>();
        loginUser.setId(customer.getId());
        loginUser.setUserType(UserTypeEnum.CUSTOMER.getValue());
        loginUser.setLoginType(LoginUser.LoginType.PHONE);
        loginUser.setLoginInfo(customerInfo);
        loginUser.setLoginTime(LocalDateTime.now());
        loginUser.setLoginSource("DEV_LOCAL");

        String token = appLoginTokenService.generateToken(loginUser);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("customerId", customer.getId());
        resp.put("alipayUserId", alipayUserId);
        resp.put("alipayAppId", alipayAppId);
        resp.put("token", token);
        return CommonResult.success(resp);
    }
}
