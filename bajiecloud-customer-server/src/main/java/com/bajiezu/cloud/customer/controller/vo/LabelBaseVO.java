package com.bajiezu.cloud.customer.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelBaseVO {

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 30, message = "标签名称长度不能超过 20 个字符")
    private String name;

    @Schema(description = "标签类型 1: 手动添加 默认传1", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "类型不能为空")
    private Integer labelType;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotBlank(message = "备注不能为空")
    @Size(max = 100, message = "备注长度不能超过 100 个字符")
    private String remark;
}
