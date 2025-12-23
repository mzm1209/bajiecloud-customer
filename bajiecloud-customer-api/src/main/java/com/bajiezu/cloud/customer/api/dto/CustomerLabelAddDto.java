package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Schema(description = "管理后台 - 用户中心 - 打标签VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLabelAddDto {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1]")
    @NotBlank(message = "标签ID不能为空")
    private List<Long> labelIds;
}
