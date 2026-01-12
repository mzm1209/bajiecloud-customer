package com.bajiezu.cloud.customer.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 用户中心 - 积分/成长值操作")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerBehaviorDto {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long customerId;

    @Schema(description = "行为编码 " +
            "1:下单成功  " +
            "2:按时还款 " +
            "3:提前还款 " +
            "4:逾期还款 " +
            "5:签到 " +
            "6:领取会员卡 " +
            "7:浏览商品 " +
            "8:加入商家群 " +
            "9:订阅消息 " +
            "10:分享好友 " +
            "11:添加到桌面 " +
            "12:收藏小程序 " +
            "13:关注生活号 " +
            "14:积分兑换", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer behaviorCode;

    @Schema(description = "逾期天数 behaviorCode = 4 时必传", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer days;

    @Schema(description = "兑换积分 behaviorCode = 14 时必传", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer points;
}
