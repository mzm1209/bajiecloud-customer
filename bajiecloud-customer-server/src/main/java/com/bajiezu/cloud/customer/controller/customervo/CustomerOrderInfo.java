package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerOrderInfo {

    @Schema(description = "下单数量")
    private Integer orderCount;

    @Schema(description = "消费总金额")
    private Long orderAmount;

    @Schema(description = "累计积分")
    private Long totalPoint;

    @Schema(description = "累计成长值")
    private Long totalGrowth;

    @Schema(description = "最近下单时间")
    private Date lastOrderTime;
}
