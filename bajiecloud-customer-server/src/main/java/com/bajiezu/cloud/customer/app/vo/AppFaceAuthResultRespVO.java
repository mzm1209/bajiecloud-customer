package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppFaceAuthResultRespVO {

    @Schema(description = "人脸认证状态")
    private Integer faceAuthStatus;

    @Schema(description = "支付宝认证流水号")
    private String certifyId;

    @Schema(description = "失败原因")
    private String failReason;
}
