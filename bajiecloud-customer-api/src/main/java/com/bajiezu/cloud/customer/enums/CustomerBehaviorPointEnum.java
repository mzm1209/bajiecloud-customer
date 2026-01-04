package com.bajiezu.cloud.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerBehaviorPointEnum {

    // 任务配置
    ORDER_SUCCESS(        1, "order_success","下单成功"),
    REPAYMENT_ON_TIME(    2, "repayment_on_time", "按时还款"),
    REPAYMENT_IN_ADVANCE( 3, "repayment_in_advance", "提前还款"),
    REPAYMENT_OVERDUE(    4, "repayment_overdue", "逾期还款"),

    CHECK_IN(             5, "check_in", "签到"),
    GET_MEMBERSHIP_CARD(  6, "get_membership_card", "领取会员卡"),
    OVERVIEW_GOODS(       7, "overview_goods", "浏览商品"),
    JOIN_SHOP_ROOM(       8, "join_shop_room", "加入商家群"),
    SUBSCRIPTION_MESSAGE( 9, "subscription_message", "订阅消息"),
    SHARE(               10, "share", "分享好友"),
    ADD_IN_DESKTOP(      11, "add_in_desktop", "添加到桌面"),
    MARK_APPLET(         12, "mark_applet", "收藏小程序"),
    FOLLOW_LIFE_NUMBER(  13, "follow_life_number", "关注生活号"),

    // 全局规则配置
    REDEEM(              14, "redeem", "积分兑换"),
    ;


    private final Integer status;
    private final String code;
    private final String desc;

}
