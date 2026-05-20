package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.dto.AppIdentityListReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppRealnameSubmitReqDTO;
import com.bajiezu.cloud.customer.app.service.AppRealnameService;
import com.bajiezu.cloud.customer.app.vo.AppIdentityDetailRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdentityListRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameStatusRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameSubmitRespVO;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/app/customer/realname")
@Validated
public class AppRealnameController {

    @Resource
    private AppRealnameService appRealnameService;

    @GetMapping("/status")
    public CommonResult<AppRealnameStatusRespVO> status() {
        return CommonResult.success(appRealnameService.getStatus());
    }

    @PostMapping("/idcard/upload")
    public CommonResult<AppIdCardUploadRespVO> uploadIdCard(@RequestParam("side") @NotBlank String side,
                                                             @RequestParam("file") @NotNull MultipartFile file) {
        return CommonResult.success(appRealnameService.uploadIdCard(side, file));
    }

    @PostMapping("/submit")
    public CommonResult<AppRealnameSubmitRespVO> submit(@RequestBody @Validated AppRealnameSubmitReqDTO reqDTO) {
        return CommonResult.success(appRealnameService.submit(reqDTO));
    }

    @GetMapping("/identity/list")
    public CommonResult<AppIdentityListRespVO> identityList(@ModelAttribute AppIdentityListReqDTO reqDTO) {
        return CommonResult.success(appRealnameService.identityList(reqDTO));
    }

    @GetMapping("/identity/detail/{id}")
    public CommonResult<AppIdentityDetailRespVO> identityDetail(@PathVariable("id") Long id) {
        return CommonResult.success(appRealnameService.identityDetail(id));
    }

}
