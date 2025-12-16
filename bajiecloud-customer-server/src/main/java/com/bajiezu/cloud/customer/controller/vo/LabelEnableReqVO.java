package com.bajiezu.cloud.customer.controller.vo;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 标签管理 - 启用禁用VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelEnableReqVO {

    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "ID不能为空")
    private Long id;

    @Schema(description = "标签状态 0: 禁用 1: 启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "状态不能为空")
    private Integer status;

    public void validateParam() {
        Preconditions.checkArgument(id != null, "ID不能为空");
        Preconditions.checkArgument(status != null, "状态不能为空");
        Preconditions.checkArgument(status == 0 || status == 1, "状态值异常");
    }
}
