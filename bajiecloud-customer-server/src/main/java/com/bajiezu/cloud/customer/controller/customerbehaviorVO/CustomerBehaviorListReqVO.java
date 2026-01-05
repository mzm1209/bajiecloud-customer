package com.bajiezu.cloud.customer.controller.customerbehaviorVO;

import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 积分/成长值列表VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerBehaviorListReqVO extends PageParam {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long customerId;

    @Schema(description = "查询列表类型 1:积分明细 2:成长值明细", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer behaviorType;
}
