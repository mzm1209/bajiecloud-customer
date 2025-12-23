package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - 用户中心 - 拉黑/解除拉黑VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerBlackDto {

    @Schema(description = "拉黑状态 0: 解除拉黑 1: 拉黑", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "拉黑状态不能为空")
    private Integer isBlack;

    @Schema(description = "拉黑原因描述", example = "xxx")
    private String blackReason;


    @Schema(description = "客户ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1]")
    @NotBlank(message = "客户ID列表不能为空")
    private List<Long> customerIds;
}
