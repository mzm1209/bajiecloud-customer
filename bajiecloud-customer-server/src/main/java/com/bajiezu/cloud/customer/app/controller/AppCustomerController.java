package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.service.AppCustomerService;
import com.bajiezu.cloud.customer.app.vo.AddressDetailVO;
import com.bajiezu.cloud.customer.app.vo.AddressListVO;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/customer")
@Validated
public class AppCustomerController {

    @Resource
    private AppCustomerService appCustomerService;

    @GetMapping("/profile")
    public CommonResult<AppCustomerProfileRespVO> getProfile() {
        return CommonResult.success(appCustomerService.getProfile());
    }

    @GetMapping("/address/list")
    public CommonResult<List<AddressListVO>> getAddressList() {
        return CommonResult.success(appCustomerService.getAddressList());
    }

    @GetMapping("/address/detail/{id}")
    public CommonResult<AddressDetailVO> getAddressDetail(@PathVariable("id") Long id) {
        return CommonResult.success(appCustomerService.getAddressDetail(id));
    }
}
