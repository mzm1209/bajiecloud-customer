package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerSensitiveResp {

    @Schema(description = "mobile: 手机号", example = "1")
    private String mobile;

    @Schema(description = "realName: 真实姓名", example = "1")
    private String realName;

    @Schema(description = "email: 邮箱", example = "1")
    private String email;

    @Schema(description = "wechatId: 微信ID", example = "1")
    private String wechatId;

    @Schema(description = "wechatMobile: 微信手机号", example = "1")
    private String wechatMobile;

    @Schema(description = "idCard: 证件号", example = "1")
    private String idCard;
}
