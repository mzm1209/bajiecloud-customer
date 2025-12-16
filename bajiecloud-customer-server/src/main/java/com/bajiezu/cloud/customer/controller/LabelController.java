package com.bajiezu.cloud.customer.controller;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.vo.*;
import com.bajiezu.cloud.customer.service.LabelService;
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

@Tag(name = "管理后台 - 客户中心 - 标签管理")
@RestController
@RequestMapping("/customer/label")
@Validated
@Slf4j
public class LabelController {

    @Autowired
    private LabelService labelService;

    @PostMapping("/add")
    @Operation(summary = "新增")
    @PreAuthorize("@ss.hasPermission('customer:label:add')")
    public CommonResult<Boolean> add(@Valid @RequestBody LabelAddReqVO reqVO) {
        labelService.add(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/mod")
    @Operation(summary = "编辑")
    @PreAuthorize("@ss.hasPermission('customer:label:mod')")
    public CommonResult<Boolean> mod(@Valid @RequestBody LabelModReqVO reqVO) {
        labelService.mod(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/enable")
    @Operation(summary = "启用禁用")
    @PreAuthorize("@ss.hasPermission('customer:label:enable')")
    public CommonResult<Boolean> enable(@Valid @RequestBody LabelEnableReqVO reqVO) {
        labelService.enable(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/list")
    @Operation(summary = "标签列表")
    @PreAuthorize("@ss.hasPermission('customer:label:list')")
    public CommonResult<PageResult<LabelRespVO>> list(@Valid @RequestBody LabelListReqVO reqVO) {
        return CommonResult.success(labelService.list(reqVO));
    }

    @PostMapping("/export")
    @Operation(summary = "列表导出")
    @PreAuthorize("@ss.hasPermission('customer:label:export')")
    public CommonResult<Boolean> export(HttpServletRequest request, HttpServletResponse response) {
        log.info("export params:{}", request.getParameter("params"));
//        Preconditions.checkArgument(StringUtils.isNotBlank(request.getParameter("params")), "请求参数不能为空");
//        LabelListReqVO reqDto = JacksonUtil.str2Obj(request.getParameter("params"), LabelListReqVO.class);
//        labelService.export(reqDto, response);
        return CommonResult.success(true);
    }
}
