package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerMemberLevelDto {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "会员等级 0:普通成员 大于0:会员 (在根据数据大小确认会员等级)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer memberLevel;
}
