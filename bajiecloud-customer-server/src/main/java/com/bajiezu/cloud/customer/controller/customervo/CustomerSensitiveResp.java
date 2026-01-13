package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerSensitiveResp {

    @Schema(description = "参数字段", example = "1")
    private String param;

    @Schema(description = "对应的值", example = "1")
    private String value;
}
