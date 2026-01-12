package com.bajiezu.cloud.customer.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.labelvo.*;
import com.bajiezu.cloud.customer.enums.ApiConstants;
import com.bajiezu.cloud.customer.service.LabelService;
import com.bajiezu.cloud.customer.utils.Id2NameDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Tag(name = "管理后台 - 客户中心 - 标签管理")
@RestController
@RequestMapping("/customer")
@Validated
@Slf4j
public class LabelController {

    @Autowired
    private LabelService labelService;

    @PostMapping("/label/add")
    @Operation(summary = "新增")
//    @PreAuthorize("@ss.hasPermission('customer:label:add')")
    public CommonResult<Boolean> add(@Valid @RequestBody LabelAddReqVO reqVO) {
        labelService.add(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/label/mod")
    @Operation(summary = "编辑")
//    @PreAuthorize("@ss.hasPermission('customer:label:mod')")
    public CommonResult<Boolean> mod(@Valid @RequestBody LabelModReqVO reqVO) {
        labelService.mod(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/label/del")
    @Operation(summary = "删除")
//    @PreAuthorize("@ss.hasPermission('customer:label:del')")
    public CommonResult<Boolean> del(@Valid @RequestBody LabelIdReqVO reqVO) {
        labelService.delete(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/label/enable")
    @Operation(summary = "启用禁用")
//    @PreAuthorize("@ss.hasPermission('customer:label:enable')")
    public CommonResult<Boolean> enable(@Valid @RequestBody LabelEnableReqVO reqVO) {
        labelService.enable(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/label/list")
    @Operation(summary = "标签列表")
//    @PreAuthorize("@ss.hasPermission('customer:label:list')")
    public CommonResult<PageResult<LabelRespVO>> list(@Valid @RequestBody LabelListReqVO reqVO) {
        return CommonResult.success(labelService.list(reqVO));
    }

    @PostMapping("/label/all")
    @Operation(summary = "所有标签")
//    @PreAuthorize("@ss.hasPermission('customer:label:all')")
    public CommonResult<List<Id2NameDto>> all() {
        return CommonResult.success(labelService.queryAllLabel());
    }
}
