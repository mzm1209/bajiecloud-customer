package com.bajiezu.cloud.customer.api;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.api.dto.CustomerAddressDto;
import com.bajiezu.cloud.customer.api.dto.CustomerAddressQueryDto;
import com.bajiezu.cloud.customer.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 客户收货地址")
public interface CustomerAddressApi {

    String PREFIX = ApiConstants.PREFIX + "/address";

    @PostMapping(PREFIX + "/getAddressById")
    @Operation(summary = "按地址ID查询客户收货地址")
    CommonResult<CustomerAddressDto> getAddressById(@RequestBody CustomerAddressQueryDto dto);
}
