package com.bajiezu.cloud.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelStatusEnum {

    enable(1, "启用"),
    disable(0, "禁用");

    private final Integer status;

    private final String desc;
}
