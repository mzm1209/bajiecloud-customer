package com.bajiezu.cloud.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelTypeEnum {


    MANUALLY_ADD(1, "手动添加");

    private final Integer status;

    private final String desc;
}
