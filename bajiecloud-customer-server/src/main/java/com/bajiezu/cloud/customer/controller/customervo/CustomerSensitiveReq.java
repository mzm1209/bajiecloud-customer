package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CustomerSensitiveReq {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "脱敏参数: " +
            "realName: 真实姓名，" +
            "mobile: 手机号, " +
            "email: 邮箱," +
            "wechatId: 微信ID， " +
            "wechatMobile: 微信手机号, " +
            "idCard: 证件号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "客户ID不能为空")
    private List<String> params;
}
