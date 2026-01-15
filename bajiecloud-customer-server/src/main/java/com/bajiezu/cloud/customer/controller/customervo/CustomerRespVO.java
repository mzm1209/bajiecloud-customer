package com.bajiezu.cloud.customer.controller.customervo;

import com.bajiezu.cloud.framework.desensitize.core.regex.annotation.EmailDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.ChineseNameDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.FixedPhoneDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.MobileDesensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerRespVO {

    @Schema(description = "用户ID")
    private Long customerId;

    @Schema(description = "平台统一用户ID")
    private String platformUid;

    @Schema(description = "三方平台用户ID")
    private String thirdPartyId;

    @Schema(description = "姓名")
    @ChineseNameDesensitize(prefixKeep = 1)
    private String name;

    @Schema(description = "手机号")
    @MobileDesensitize
    private String mobile;

    @Schema(description = "邮箱")
    @EmailDesensitize
    private String email;

    @Schema(description = "会员等级")
    private Integer memberLevel;

    @Schema(description = "累计积分")
    private Long totalPoint;

    @Schema(description = "累计成长值")
    private Long totalGrowth;

    @Schema(description = "来源渠道：AliPay, JD, WeChat")
    private String sourceChannel;

    @Schema(description = "来源平台名称")
    private String platformName;

    @Schema(description = "是否是黑名单用户")
    private Boolean isBlackList;

    @Schema(description = "下单次数")
    private Integer orderCount;

    @Schema(description = "注册时间")
    private Date registerTime;

    @Schema(description = "最近下单时间")
    private Date lastOrderTime;

}
