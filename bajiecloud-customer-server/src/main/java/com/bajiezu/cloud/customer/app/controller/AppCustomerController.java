package com.bajiezu.cloud.customer.app.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.app.service.AppCustomerService;
import com.bajiezu.cloud.customer.app.dto.AddressCreateRequest;
import com.bajiezu.cloud.customer.app.dto.AddressDeleteRequest;
import com.bajiezu.cloud.customer.app.dto.AddressSetDefaultRequest;
import com.bajiezu.cloud.customer.app.dto.AddressUpdateRequest;
import com.bajiezu.cloud.customer.app.vo.AddressDetailVO;
import com.bajiezu.cloud.customer.app.vo.AddressListVO;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

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

    /**
     * 兼容两种方式：
     * 1) /address/detail/{id}
     * 2) /address/detail?id=xxx
     */
    @GetMapping({"/address/detail/{id}", "/address/detail"})
    public CommonResult<AddressDetailVO> getAddressDetail(
            @PathVariable(value = "id", required = false) Long pathId,
            @RequestParam(value = "id", required = false) Long queryId) {

        Long id = pathId != null ? pathId : queryId;
        if (id == null) {
            // 按你们项目统一异常/返回码风格改
            throw new IllegalArgumentException("id 不能为空");
        }

        return CommonResult.success(appCustomerService.getAddressDetail(id));
    }

    @PostMapping("/address/create")
    public CommonResult<Map<String, Long>> createAddress(@RequestBody @Valid AddressCreateRequest req) {
        return CommonResult.success(appCustomerService.createAddress(req));
    }

    @PostMapping("/address/update")
    public CommonResult<Boolean> updateAddress(@RequestBody @Valid AddressUpdateRequest req) {
        return CommonResult.success(appCustomerService.updateAddress(req));
    }

    @PostMapping("/address/delete")
    public CommonResult<Boolean> deleteAddress(@RequestBody @Valid AddressDeleteRequest req) {
        return CommonResult.success(appCustomerService.deleteAddress(req));
    }

    @PostMapping("/address/set-default")
    public CommonResult<Boolean> setDefaultAddress(@RequestBody @Valid AddressSetDefaultRequest req) {
        return CommonResult.success(appCustomerService.setDefaultAddress(req));
    }
}
