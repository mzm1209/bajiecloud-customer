package com.bajiezu.cloud.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberLevelEnum {

    NORMAL(0, "普通成员"),
    MEMBER_LEVEL_1(1, "会员等级1"),

    ;


    private final Integer code;

    private final String desc;
}
