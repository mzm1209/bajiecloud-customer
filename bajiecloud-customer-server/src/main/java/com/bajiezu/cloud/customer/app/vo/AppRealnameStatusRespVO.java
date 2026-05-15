package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class AppRealnameStatusRespVO {

    @Schema(description = "实名状态")
    private Integer realnameStatus;

    @Schema(description = "人脸认证状态")
    private Integer faceAuthStatus;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "认证时间")
    private Date authTime;
}
