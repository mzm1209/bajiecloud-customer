package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;
import com.bajiezu.cloud.customer.app.service.AppSmsService;
import com.bajiezu.cloud.customer.app.vo.AppSmsSendRespVO;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/sms")
public class AppSmsController {
    @Resource
    private AppSmsService appSmsService;

    @PostMapping("/send")
    @PermitAll
    public CommonResult<AppSmsSendRespVO> send(@RequestBody @Valid AppSmsSendReqDTO reqDTO, HttpServletRequest request) {
        return CommonResult.success(appSmsService.sendLoginSms(reqDTO, request.getRemoteAddr()));
    }
}
