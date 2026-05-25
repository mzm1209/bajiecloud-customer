package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerAddressQueryDto {

    @Schema(description = "地址ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "地址ID不能为空")
    private Long id;

    @Schema(description = "客户ID（可选，用于二次校验地址归属）", example = "1")
    private Long customerId;
}
