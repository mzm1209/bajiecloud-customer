package com.bajiezu.cloud.customer.controller.customerbehaviorVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerBehaviorRespVO {

    @Schema(description = "列表ID",  example = "1")
    private Integer id;

    @Schema(description = "行为编码",  example = "1")
    private Integer behaviorCode;

    @Schema(description = "行为描述",  example = "1")
    private String behaviorDesc;

    @Schema(description = "逾期天数",  example = "1")
    private Integer days;

    @Schema(description = "操作动作 1: 发放 2:扣减 ",  example = "1")
    private Integer operateAction;

    @Schema(description = "操作数值",  example = "100")
    private Integer count;

    @Schema(description = "操作时间",  example = "xxxxx")
    private Date operateTime;
}
