package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailRespVO {

    @Schema(description = "客户基础信息信息", example = "xxx")
    private CustomerBaseDetail baseDetail;

    @Schema(description = "客户关联标签信息列表",  example = "[xxx]")
    private List<CustomerLabelRespVO> labelList;

    @Schema(description = "客户扩展信息",  example = "[xxx]")
    private List<CustomerExtInfoVO> extInfos;
}
