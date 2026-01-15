package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerInfoDto {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "手机号")
    private String mobile;
}
