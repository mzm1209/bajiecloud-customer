package com.bajiezu.cloud.customer.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppAlipayLoginReqDTO {

    @Schema(description = "支付宝小程序 authCode", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "authCode不能为空")
    private String authCode;

    @Schema(description = "设备标识")
    private String deviceId;

    @Schema(description = "邀请码")
    private String inviteCode;
}
