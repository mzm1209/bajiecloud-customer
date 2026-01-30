package com.bajiezu.cloud.customer.controller.labelvo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class LabelRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @ExcelProperty(value = "标签名称", index = 0)
    @ColumnWidth(25)
    private String name;

    @Schema(description = "customerSize 标签人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "标签人数", index = 1)
    private Integer customerSize;

    @Schema(description = "标签类型 1: 手动添加 默认传1", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer labelType;

    @Schema(description = "备注",  example = "xxx")
    @ExcelProperty(value = "备注", index = 3)
    @ColumnWidth(40)
    private String remark;

    @Schema(description = "标签状态 0:禁用 1:使用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @ExcelProperty(value = "标签状态", index = 2)
    private String statusDesc;

    @Schema(description = "createTime 创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "创建时间", index = 4)
    @ColumnWidth(25)
    private Date createTime;

}
