package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppCustomerProfileRespVO {

    @Schema(description = "C端用户ID")
    private Long customerId;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "头像地址")
    private String avatarUrl;

    @Schema(description = "脱敏手机号")
    private String mobile;

    @Schema(description = "是否绑定手机号")
    private Boolean hasMobile;

    @Schema(description = "实名状态")
    private Integer realnameStatus;

    @Schema(description = "人脸认证状态")
    private Integer faceAuthStatus;

    @Schema(description = "脱敏真实姓名")
    private String realName;

    @Schema(description = "脱敏身份证号")
    private String idCard;

    @Schema(description = "账户状态")
    private Integer accountStatus;

    @Schema(description = "平台来源")
    private String platformName;

    @Schema(description = "来源渠道")
    private String sourceChannel;

    @Schema(description = "最近登录时间")
    private String lastLoginTime;
}
