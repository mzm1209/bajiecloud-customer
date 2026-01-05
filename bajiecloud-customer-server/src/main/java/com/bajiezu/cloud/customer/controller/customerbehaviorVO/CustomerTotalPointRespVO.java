package com.bajiezu.cloud.customer.controller.customerbehaviorVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerTotalPointRespVO {

    @Schema(description = "累计积分",  example = "100")
    private Long totalPoint;


    @Schema(description = "累计成长值",  example = "100")
    private Long totalGrowth;
}
