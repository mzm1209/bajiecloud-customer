package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户地址表
 * 对应数据库表: customer_address
 */
@Data
@TableName("customer_address")
public class CustomerAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 地址ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 地址类型：1-家，2-公司，3-学校，4-父母，5-朋友，9-其他/自定义 */
    @TableField("address_type")
    private Integer addressType;

    /** 收货人姓名 */
    @TableField("receiver_name")
    private String receiverName;

    /** 收货人手机号（加密存储） */
    @TableField("receiver_mobile")
    private String receiverMobile;


    /** 省编码 */
    @TableField("province_code")
    private String provinceCode;

    /** 省名称 */
    @TableField("province_name")
    private String provinceName;

    /** 市编码 */
    @TableField("city_code")
    private String cityCode;

    /** 市名称 */
    @TableField("city_name")
    private String cityName;

    /** 选中的区县代码，关联地址表的code字段 */
    @TableField("area_code")
    private String areaCode;

    /** 区县名称 */
    @TableField("area_name")
    private String areaName;

    /** 详细街道地址 */
    @TableField("street_address")
    private String streetAddress;

    /** 邮政编码 */
    @TableField("postal_code")
    private String postalCode;

    /** 地址标签 */
    @TableField("address_tag")
    private String addressTag;

    /** 经度 */
    @TableField("longitude")
    private java.math.BigDecimal longitude;

    /** 纬度 */
    @TableField("latitude")
    private java.math.BigDecimal latitude;

    /** 完整地址快照 */
    @TableField("full_address")
    private String fullAddress;

    /** 是否默认地址：0-否，1-是 */
    @TableField("is_default")
    private Integer isDefault;

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
