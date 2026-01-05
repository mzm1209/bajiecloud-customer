package com.bajiezu.cloud.customer.api;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.api.dto.*;
import com.bajiezu.cloud.customer.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 客户服务")
public interface CustomerApi {

    String PREFIX = ApiConstants.PREFIX;

    @PostMapping(PREFIX + "/isBlack")
    @Operation(summary = "拉黑/解除拉黑")
    CommonResult<Boolean> isBlack(@RequestBody CustomerBlackDto dto);

    @PostMapping(PREFIX + "/addLabel")
    @Operation(summary = "给客户打标签")
    CommonResult<Boolean> addLabel(@RequestBody CustomerLabelAddDto dto);

    @PostMapping(PREFIX + "/baseInfo")
    @Operation(summary = "获取客户基本信息")
    CommonResult<CustomerBaseDetailInfo> getBaseInfo(@RequestBody CustomerBaseDto dto);

    @PostMapping(PREFIX + "/checkIsMember")
    @Operation(summary = "判断客户是否为会员")
    CommonResult<CustomerMemberLevelDto> checkIsMember(@RequestBody CustomerBaseDto dto);

    @PostMapping(PREFIX + "/updateMemberLevel")
    @Operation(summary = "变更客户为会员")
    CommonResult<Boolean> updateMemberLevel(@RequestBody CustomerMemberLevelDto dto);

    @PostMapping(PREFIX + "/behaviorHandle")
    @Operation(summary = "用户行为操作记录")
    CommonResult<Boolean> behaviorHandle(@RequestBody CustomerBehaviorDto dto);
}
