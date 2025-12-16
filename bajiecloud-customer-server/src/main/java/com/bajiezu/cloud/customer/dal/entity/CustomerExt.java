package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户扩展信息表
 * 对应数据库表: customer_ext
 */
@Data
@TableName("customer_ext")
public class CustomerExt implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 扩展类型：AlipayInfo, JDInfo, WechatInfo, BaseExt */
    @TableField("ext_type")
    private String extType;

    /** 扩展字段Key */
    @TableField("ext_key")
    private String extKey;

    /** 扩展字段Value（JSON格式存储） */
    @TableField("ext_value")
    private String extValue;

    /** 数据来源：System, Alipay, JD, Wechat, Manual */
    @TableField("source_from")
    private String sourceFrom;

    /** 是否有效：0-否，1-是 */
    @TableField("is_valid")
    private Boolean isValid;

    /** 创建者 */
    @TableField("create_by")
    private Long createBy;

    /** 更新者 */
    @TableField("updated_by")
    private Long updatedBy;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    private Date updateTime;

    /** 是否删除：0-否，1-是 */
    @TableField("is_deleted")
    private Integer isDeleted;
}
