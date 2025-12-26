package com.bajiezu.cloud.customer.controller.labelvo;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 标签管理 - 编辑VO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelModReqVO extends LabelBaseVO{

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    public void validateParam() {
        super.validaParam();
        Preconditions.checkArgument(id != null, "ID不能为空");
    }
}
