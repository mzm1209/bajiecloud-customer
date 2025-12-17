package com.bajiezu.cloud.customer.controller.customervo;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerBaseReqVO {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "客户ID不能为空")
    private Long customerId;

    public void validateParam() {
        Preconditions.checkArgument(customerId != null, "客户ID不能为空");
    }
}
