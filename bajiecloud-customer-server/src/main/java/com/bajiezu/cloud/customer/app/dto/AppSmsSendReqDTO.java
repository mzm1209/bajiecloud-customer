package com.bajiezu.cloud.customer.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppSmsSendReqDTO {
    @NotBlank
    @Schema(description = "国家区号")
    private String countryCode;
    @NotBlank
    private String mobile;
    @NotBlank
    private String scene;
    private String deviceId;
}
