package com.bajiezu.cloud.customer.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppMobileLoginReqDTO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;

    @Schema(description = "设备标识")
    private String deviceId;
}
