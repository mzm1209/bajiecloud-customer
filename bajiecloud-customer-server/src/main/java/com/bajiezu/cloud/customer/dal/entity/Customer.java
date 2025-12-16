package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户基础信息表
 * 对应数据库表: customer
 */
@Data
@TableName("customer")
public class Customer implements Serializable {

    /** 客户ID（平台内部唯一ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID) // 假设由代码分配ID
    private Long id;

    /** 平台统一用户标识 */
    @TableField("platform_uid")
    private String platformUid;

    /** 第三方平台用户ID */
    @TableField("third_party_id")
    private String thirdPartyId;

    /** 来源平台名称 */
    @TableField("platform_name")
    private String platformName;

    /** 来源渠道：AliPay, JD, WeChat */
    @TableField("source_channel")
    private String sourceChannel;

    /** 来源等级 A B C */
    @TableField("source_level")
    private String sourceLevel;

    /** 来源评分 */
    @TableField("source_point")
    private String sourcePoint;

    /** 手机号（加密存储） */
    @TableField("mobile")
    private String mobile;

    /** 邮箱（加密存储） */
    @TableField("email")
    private String email;

    /** 微信ID */
    @TableField("wechat_id")
    private String wechatId;

    /** 微信手机号（加密存储） */
    @TableField("wechat_mobile")
    private String wechatMobile;

    /** 证件号（加密存储） */
    @TableField("id_card")
    private String idCard;

    /** 证件号hash（匹配用） */
    @TableField("id_card_hash")
    private String idCardHash;

    /** 昵称 */
    @TableField("nickname")
    private String nickname;

    /** 头像URL */
    @TableField("avatar_url")
    private String avatarUrl;

    /** 真实姓名（加密存储） */
    @TableField("real_name")
    private String realName;

    /** 性别：0-未知，1-男，2-女 */
    @TableField("gender")
    private Integer gender;

    /** 生日 */
    @TableField("birthday")
    private Date birthday; // 对应 DATE 类型

    /** 归属地,精确到市的区划代码 */
    @TableField("area_code")
    private String areaCode;

    /** 会员等级：1-普通会员，2-高级会员，3-VIP会员 */
    @TableField("member_level")
    private Integer memberLevel;

    /** 是否进黑名单：0-否，1-是 */
    @TableField("in_black_list")
    private Boolean inBlackList;

    /** 进黑名单原因 */
    @TableField("in_black_reason")
    private String inBlackReason; // 对应 TEXT 类型

    /** 是否匿名：0-否，1-是 */
    @TableField("is_anonymous")
    private Boolean isAnonymous;

    /** 账户状态：0-禁用，1-正常，2-冻结，3-合并后归档 */
    @TableField("account_status")
    private Integer accountStatus;

    /** 合并后的主账户ID */
    @TableField("merge_master_id")
    private Long mergeMasterId;

    /** 最后登录时间 */
    @TableField("last_login_time")
    private Date lastLoginTime;

    /** 最后登录IP */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /** 最后下单时间 */
    @TableField("last_order_time")
    private Date lastOrderTime;

    /** 最近一次来源 */
    @TableField("last_source")
    private String lastSource;

    /** 紧急联系人姓名 */
    @TableField("emergency_contact_name")
    private String emergencyContactName;

     @TableField("create_by")
     private Long createBy;

     @TableField("updated_by")
     private Long updatedBy;

     @TableField(value = "version")
     private Integer version; //  @Version 乐观锁

     @TableField(value = "create_time")
     private Date createTime;

     @TableField(value = "update_time")
     private Date updateTime;

     @TableField("is_deleted")
     private Integer isDeleted;
}