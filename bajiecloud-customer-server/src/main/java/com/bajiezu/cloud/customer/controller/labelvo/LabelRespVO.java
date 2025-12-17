package com.bajiezu.cloud.customer.controller.labelvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabelRespVO extends LabelBaseVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "标签状态 0:禁用 1:使用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "createTime 创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Date createTime;

    @Schema(description = "customerSize 标签人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer customerSize;
}
