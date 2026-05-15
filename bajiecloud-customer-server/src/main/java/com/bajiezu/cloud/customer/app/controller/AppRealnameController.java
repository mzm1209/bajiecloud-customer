package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.dto.AppRealnameSubmitReqDTO;
import com.bajiezu.cloud.customer.app.vo.AppFaceAuthResultRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameStatusRespVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/realname")
@Validated
public class AppRealnameController {

    @PostMapping("/submit")
    public CommonResult<Boolean> submit(@RequestBody @Valid AppRealnameSubmitReqDTO reqDTO) {
        return CommonResult.success(Boolean.TRUE);
    }

    @GetMapping("/status")
    public CommonResult<AppRealnameStatusRespVO> getStatus() {
        return CommonResult.success(new AppRealnameStatusRespVO());
    }

    @PostMapping("/id-card/upload")
    public CommonResult<AppIdCardUploadRespVO> uploadIdCard() {
        return CommonResult.success(new AppIdCardUploadRespVO());
    }

    @PostMapping("/face/init")
    public CommonResult<String> initFaceAuth() {
        return CommonResult.success("");
    }

    @GetMapping("/face/result")
    public CommonResult<AppFaceAuthResultRespVO> getFaceAuthResult() {
        return CommonResult.success(new AppFaceAuthResultRespVO());
    }
}
