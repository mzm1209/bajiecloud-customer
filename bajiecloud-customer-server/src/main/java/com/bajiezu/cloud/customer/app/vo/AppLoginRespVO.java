package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppLoginRespVO {

    @Schema(description = "C端登录 token")
    private String token;

    @Schema(description = "C端用户ID")
    private Long customerId;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "实名状态")
    private Integer realnameStatus;

    @Schema(description = "人脸认证状态")
    private Integer faceAuthStatus;
}
