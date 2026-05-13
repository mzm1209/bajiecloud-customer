package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/sms")
@Validated
public class AppSmsController {

    @PostMapping("/send")
    public CommonResult<Boolean> send(@RequestBody @Valid AppSmsSendReqDTO reqDTO) {
        return CommonResult.success(Boolean.TRUE);
    }
}
