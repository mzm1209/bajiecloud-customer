package com.bajiezu.cloud.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    IS_BLACK(1, "is_black","拉黑"),
    UN_BLACK(2, "un_black","取消拉黑"),
    ADD_LABEL(3, "add_label","添加标签"),

    ;


    private final Integer status;

    private final String code;

    private final String desc;
}
