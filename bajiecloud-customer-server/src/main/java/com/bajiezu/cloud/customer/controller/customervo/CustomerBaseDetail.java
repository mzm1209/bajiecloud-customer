package com.bajiezu.cloud.customer.controller.customervo;

import com.bajiezu.cloud.framework.desensitize.core.regex.annotation.EmailDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.ChineseNameDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.IdCardDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.MobileDesensitize;
import com.bajiezu.cloud.framework.desensitize.core.slider.annotation.PasswordDesensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerBaseDetail {

    @Schema(description = "客户ID", example = "xxx")
    private Long customerId;

    @Schema(description = "平台统一用户标识", example = "xxx")
    private String platformUid;

    @Schema(description = "第三方平台用户ID", example = "xxx")
    private String thirdPartyId;

    @Schema(description = "来源平台名称", example = "xxx")
    private String platformName;

    @Schema(description = "来源渠道：AliPay, JD, WeChat", example = "xxx")
    private String sourceChannel;

    @Schema(description = "来源等级 A B C ", example = "xxx")
    private String sourceLevel;

    @Schema(description = "来源评分", example = "xxx")
    private String sourcePoint;

    @Schema(description = "手机号", example = "xxx")
    @MobileDesensitize
    private String mobile;

    @Schema(description = "邮箱", example = "xxx")
//    @EmailDesensitize
    private String email;

    @Schema(description = "微信ID", example = "xxx")
    @PasswordDesensitize
    private String wechatId;

    @Schema(description = "微信手机号", example = "xxx")
    @MobileDesensitize
    private String wechatMobile;

    @Schema(description = "证件号", example = "xxx")
    private String idCard;

    @Schema(description = "年龄", example = "xxx")
    private Integer age;

    @Schema(description = "证件号hash", example = "xxx")
    private String idCardHash;

    @Schema(description = "昵称", example = "xxx")
    private String nickname;

    @Schema(description = "头像URL", example = "xxx")
    private String avatarUrl;

    @Schema(description = "真实姓名", example = "xxx")
    @ChineseNameDesensitize(prefixKeep = 1)
    private String realName;

    @Schema(description = "性别：0-未知，1-男，2-女", example = "xxx")
    private Integer gender;

    @Schema(description = "生日", example = "xxx")
    private Date birthday;

    @Schema(description = "归属地,精确到市的区划代码", example = "xxx")
    private String areaCode;

    @Schema(description = "归属地省市区", example = "xxx")
    private String areaName;

    @Schema(description = "会员等级：0- 普通用户 1-普通会员，2-高级会员，3-VIP会员", example = "xxx")
    private Integer memberLevel;

    @Schema(description = "会员等级：0- 普通用户 1-普通会员，2-高级会员，3-VIP会员")
    private String levelName;

    @Schema(description = "是否进黑名单：0-否，1-是", example = "xxx")
    private Integer inBlackList;

    @Schema(description = "进黑名单原因")
    private String inBlackReason;

    @Schema(description = "是否匿名：0-否，1-是", example = "xxx")
    private Integer isAnonymous;

    @Schema(description = "账户状态：0-禁用，1-正常，2-冻结，3-合并后归档", example = "xxx")
    private Integer accountStatus;

    @Schema(description = "注册时间")
    private Date registerTime;

    @Schema(description = "最后登录时间")
    private Date lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "下单次数")
    private Integer orderCount;

    @Schema(description = "最后下单时间")
    private Date lastOrderTime;

    @Schema(description = "最近一次来源")
    private String lastSource;

    @Schema(description = "紧急联系人姓名")
    private String emergencyContactName;

    @Schema(description = "版本号")
    private Integer version;
}
