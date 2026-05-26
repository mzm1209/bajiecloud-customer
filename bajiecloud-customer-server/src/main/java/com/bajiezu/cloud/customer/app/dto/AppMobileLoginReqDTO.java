package com.bajiezu.cloud.customer.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppMobileLoginReqDTO {
    @NotBlank
    private String countryCode;
    @NotBlank
    private String mobile;
    @NotBlank
    private String smsCode;
    /**
     * 支付宝小程序授权码（可选）
     */
    private String authCode;
    private String sourceChannel;
    private String deviceId;
}
