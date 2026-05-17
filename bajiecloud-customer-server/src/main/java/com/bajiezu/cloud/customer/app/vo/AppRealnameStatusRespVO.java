package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppRealnameStatusRespVO {

    @Schema(description = "实名状态")
    private Integer realnameStatus;

    @Schema(description = "实名认证记录状态")
    private Integer authStatus;

    @Schema(description = "人脸认证状态")
    private Integer faceAuthStatus;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "是否允许提交实名认证")
    private Boolean canSubmit;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "脱敏姓名")
    private String realName;

    @Schema(description = "脱敏身份证号")
    private String idCard;

    @Schema(description = "实名认证单号")
    private String authOrderNo;

    @Schema(description = "人脸认证地址")
    private String faceAuthUrl;

    @Schema(description = "人脸认证地址剩余有效时间")
    private Integer expireSeconds;
}
