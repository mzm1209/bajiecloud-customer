package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerLabelRespVO {

    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long labelId;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String name;

    @Schema(description = "标签类型 1: 手动添加 默认传1", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer labelType;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String remark;

    @Schema(description = "标签状态 0:禁用 1:使用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "createTime 创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Date createTime;

}
