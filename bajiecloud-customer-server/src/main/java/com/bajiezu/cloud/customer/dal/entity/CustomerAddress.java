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

    /** 地址类型：1-家庭，2-公司，3-学校，4-其他 */
    @TableField("address_type")
    private Integer addressType;

    /** 收货人姓名 */
    @TableField("receiver_name")
    private String receiverName;

    /** 收货人手机号（加密存储） */
    @TableField("receiver_mobile")
    private String receiverMobile;

    /** 选中的区县代码，关联地址表的code字段 */
    @TableField("area_code")
    private String areaCode;

    /** 详细街道地址 */
    @TableField("street_address")
    private String streetAddress;

    /** 邮政编码 */
    @TableField("postal_code")
    private String postalCode;

    /** 是否默认地址：0-否，1-是 */
    @TableField("is_default")
    private Boolean isDefault;

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
