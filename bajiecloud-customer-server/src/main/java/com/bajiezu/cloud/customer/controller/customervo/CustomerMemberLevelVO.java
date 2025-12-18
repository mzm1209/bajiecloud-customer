package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerMemberLevelVO extends CustomerBaseReqVO{

    @Schema(description = "会员等级 0:普通成员 大于0:会员 (在根据数据大小确认会员等级)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer memberLevel;

    public void validateParam() {
        super.validateParam();
        if (memberLevel == null) {
            throw new RuntimeException("会员等级不能为空");
        }
    }
}

