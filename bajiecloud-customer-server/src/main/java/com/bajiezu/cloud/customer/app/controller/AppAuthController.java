package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.dto.AppAlipayLoginReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppMobileLoginReqDTO;
import com.bajiezu.cloud.customer.app.service.AppAuthService;
import com.bajiezu.cloud.customer.app.vo.AppLoginRespVO;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/auth")
@Validated
public class AppAuthController {

    @Resource
    private AppAuthService appAuthService;

    @PostMapping("/alipay-login")
    public CommonResult<AppLoginRespVO> alipayLogin(@RequestBody @Valid AppAlipayLoginReqDTO reqDTO) {
        return CommonResult.success(appAuthService.alipayLogin(reqDTO));
    }

    @PostMapping("/mobile-login")
    public CommonResult<AppLoginRespVO> mobileLogin(@RequestBody @Valid AppMobileLoginReqDTO reqDTO) {
        return CommonResult.success(appAuthService.mobileLogin(reqDTO));
    }
}
