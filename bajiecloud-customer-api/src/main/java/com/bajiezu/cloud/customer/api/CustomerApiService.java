package com.bajiezu.cloud.customer.api;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "customerServiceApi", path = "/customer", value = "customer-service")
@ConditionalOnMissingClass("com.bajiezu.cloud.customer.api.CustomerApiServiceImpl")
public interface CustomerApiService {

    @PostMapping("/isBlack")
    @Operation(summary = "拉黑/解除拉黑")
    CommonResult<Boolean> isBlack(@RequestBody CustomerBlackDto dto);

    @PostMapping("/addLabel")
    @Operation(summary = "给客户打标签")
    CommonResult<Boolean> addLabel(@RequestBody CustomerLabelAddDto dto);

    @PostMapping("/baseInfo")
    @Operation(summary = "获取客户基本信息")
    CommonResult<CustomerBaseDetailInfo> getBaseInfo(@RequestBody CustomerBaseDto dto);

    @PostMapping("/checkIsMember")
    @Operation(summary = "判断客户是否为会员")
    CommonResult<CustomerMemberLevelDto> checkIsMember(@RequestBody CustomerBaseDto dto);

    @PostMapping("/updateMemberLevel")
    @Operation(summary = "变更客户为会员")
    CommonResult<Boolean> updateMemberLevel(@RequestBody CustomerMemberLevelDto dto);
}
