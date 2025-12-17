package com.bajiezu.cloud.customer.controller.labelvo;


import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 用户中心 - 标签管理 - 标签列表VO")
public class LabelListReqVO extends PageParam {

    @Schema(description = "标签名称", example = "xxx")
    private String name;

    @Schema(description = "标签状态 0: 禁用 1: 启用", example = "1")
    private Integer status;
}
