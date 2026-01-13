package com.bajiezu.cloud.customer.controller;


import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorListReqVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorRespVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerTotalPointRespVO;
import com.bajiezu.cloud.customer.controller.customervo.*;
import com.bajiezu.cloud.customer.enums.ApiConstants;
import com.bajiezu.cloud.customer.service.CustomerBehaviorService;
import com.bajiezu.cloud.customer.service.CustomerCacheService;
import com.bajiezu.cloud.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "管理后台 - 客户中心")
@RestController
@RequestMapping("/customer")
@Validated
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    public CustomerCacheService customerCacheService;

    @Autowired
    private CustomerBehaviorService behaviorService;

    @PostMapping("/add")
    @Operation(summary = "客户新增")
    public CommonResult<Boolean> add() {
        customerService.mockAddCustomer();
        return CommonResult.success(true);
    }

    @PostMapping("/list")
    @Operation(summary = "客户列表")
//    @PreAuthorize("@ss.hasPermission('customer:list')")
    public CommonResult<PageResult<CustomerRespVO>> list(@RequestBody CustomerListReqVO reqVO) {
        return CommonResult.success(customerService.list(reqVO));
    }

    @PostMapping("/detail")
    @Operation(summary = "客户详情")
//    @PreAuthorize("@ss.hasPermission('customer:detail')")
    public CommonResult<CustomerDetailRespVO> mod(@RequestBody CustomerBaseReqVO reqVO) {
        return CommonResult.success(customerService.detail(reqVO));
    }

    @PostMapping("/isBlack")
    @Operation(summary = "拉黑/解除拉黑")
//    @PreAuthorize("@ss.hasPermission('customer:isBlack')")
    public CommonResult<Boolean> isBlack(@RequestBody CustomerBlackReqVO reqVO) {
        customerService.isBlack(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/getLabel")
    @Operation(summary = "获取客户标签列表")
//    @PreAuthorize("@ss.hasPermission('customer:getLabel')")
    public CommonResult<List<CustomerLabelRespVO>> getLabel(@RequestBody CustomerBaseReqVO reqVO) {
        return CommonResult.success(customerService.getLabel(reqVO));
    }

    @PostMapping("/addLabel")
    @Operation(summary = "给客户打标签")
//    @PreAuthorize("@ss.hasPermission('customer:addLabel')")
    public CommonResult<Boolean> addLabel(@RequestBody CustomerLabelAddVO reqVO) {
        customerService.addLabel(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/baseInfo")
    @Operation(summary = "获取客户基本信息")
//    @PreAuthorize("@ss.hasPermission('customer:baseInfo')")
    public CommonResult<CustomerBaseDetail> getBaseInfo(@RequestBody CustomerBaseReqVO reqVO) {
        return CommonResult.success(customerCacheService.getBaseInfo(reqVO.getCustomerId()));
    }

    @PostMapping("/sensitiveData")
    @Operation(summary = "获取客户非脱敏信息")
//    @PreAuthorize("@ss.hasPermission('customer:sensitiveData')")
    public CommonResult<CustomerSensitiveResp> getCustomerSensitiveData(@RequestBody CustomerSensitiveReq reqVO) {
        return CommonResult.success(customerCacheService.getCustomerSensitiveData(reqVO));
    }

    @PostMapping("/checkIsMember")
    @Operation(summary = "判断客户是否为会员")
//    @PreAuthorize("@ss.hasPermission('customer:checkIsMember')")
    public CommonResult<CustomerMemberLevelVO> checkIsMember(@RequestBody CustomerBaseReqVO reqVO) {
        return CommonResult.success(customerService.checkIsMember(reqVO));
    }

    @PostMapping("/updateMemberLevel")
    @Operation(summary = "变更客户为会员")
//    @PreAuthorize("@ss.hasPermission('customer:updateMemberLevel')")
    public CommonResult<Boolean> updateMemberLevel(@RequestBody CustomerMemberLevelVO reqVO) {
        customerService.updateMemberLevel(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/addAddress")
    @Operation(summary = "添加客户地址")
//    @PreAuthorize("@ss.hasPermission('customer:addAddress')")
    public CommonResult<Boolean> addAddress(@RequestBody CustomerAddressVO reqVO) {
        customerService.addAddress(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/addressList")
    @Operation(summary = "获取客户地址列表")
//    @PreAuthorize("@ss.hasPermission('customer:addressList')")
    public CommonResult<PageResult<CustomerAddressVO>> addressList(@RequestBody CustomerAddressListVO reqVO) {
        return CommonResult.success(customerService.addressInfoList(reqVO));
    }

    @PostMapping("/behaviorList")
    @Operation(summary = "客户积分/成长值列表")
//    @PreAuthorize("@ss.hasPermission('customer:behaviorList')")
    public CommonResult<PageResult<CustomerBehaviorRespVO>> list(@RequestBody CustomerBehaviorListReqVO reqVO) {
        return CommonResult.success(behaviorService.list(reqVO));
    }

    @PostMapping("/totalPoint")
    @Operation(summary = "客户累计积分/成长值")
//    @PreAuthorize("@ss.hasPermission('customer:totalPoint')")
    public CommonResult<CustomerTotalPointRespVO> totalPoint(@RequestBody CustomerBaseReqVO reqVO) {
        return CommonResult.success(behaviorService.customerTotalPoint(reqVO));
    }
}
