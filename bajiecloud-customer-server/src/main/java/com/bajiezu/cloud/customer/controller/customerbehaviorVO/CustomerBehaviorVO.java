package com.bajiezu.cloud.customer.controller.customerbehaviorVO;

import com.bajiezu.cloud.customer.enums.CustomerBehaviorPointEnum;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerBehaviorVO {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long customerId;

    /**
     *  // 任务配置
     *     ORDER_SUCCESS(        1, "order_success","下单成功"),
     *     REPAYMENT_ON_TIME(    2, "repayment_on_time", "按时还款"),
     *     REPAYMENT_IN_ADVANCE( 3, "repayment_in_advance", "提前还款"),
     *     REPAYMENT_OVERDUE(    4, "repayment_overdue", "逾期还款"),
     *
     *     CHECK_IN(             5, "check_in", "签到"),
     *     GET_MEMBERSHIP_CARD(  6, "get_membership_card", "领取会员卡"),
     *     OVERVIEW_GOODS(       7, "overview_goods", "浏览商品"),
     *     JOIN_SHOP_ROOM(       8, "join_shop_room", "加入商家群"),
     *     SUBSCRIPTION_MESSAGE( 9, "subscription_message", "订阅消息"),
     *     SHARE(               10, "share", "分享好友"),
     *     ADD_IN_DESKTOP(      11, "add_in_desktop", "添加到桌面"),
     *     MARK_APPLET(         12, "mark_applet", "收藏小程序"),
     *     FOLLOW_LIFE_NUMBER(  13, "follow_life_number", "关注生活号"),
     *
     *     // 全局规则配置
     *     REDEEM(              14, "redeem", "积分兑换"),
     * */
    @Schema(description = "行为编码 1:下单成功  2:按时还款 3:提前还款 4:逾期还款 5:签到 6:领取会员卡 7:浏览商品 8:加入商家群 9:订阅消息 10:分享好友 11:添加到桌面 12:收藏小程序 13:关注生活号 14:积分兑换", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer behaviorCode;

    // 逾期天数 behaviorCode = REPAYMENT_OVERDUE 必传
    @Schema(description = "逾期天数 behaviorCode = 4 时必传", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer days;

    public void validateParams() {
        Preconditions.checkArgument(customerId != null, "客户ID不能为空");
        Preconditions.checkArgument(behaviorCode != null, "客户行为不能为空");
        if (behaviorCode.equals(CustomerBehaviorPointEnum.REPAYMENT_OVERDUE.getStatus())) {
            if (days == null) {
                throw new RuntimeException("逾期天数不能为空");
            }
        }
    }
}
