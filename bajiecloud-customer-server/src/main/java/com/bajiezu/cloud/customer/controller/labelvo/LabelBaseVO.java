package com.bajiezu.cloud.customer.controller.labelvo;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class LabelBaseVO {

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @Size(max = 30, message = "标签名称长度不能超过 50 个字符")
    private String name;

    @Schema(description = "标签类型 1: 手动添加 默认传1", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer labelType;

    @Schema(description = "备注",  example = "xxx")
    @Size(max = 100, message = "备注长度不能超过 200 个字符")
    private String remark;

    public void validaParam() {
        Preconditions.checkArgument(StringUtils.isNotEmpty(name), "name为空");
        Preconditions.checkArgument(labelType != null, "类型为空");
    }
}
