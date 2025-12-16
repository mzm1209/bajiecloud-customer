package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户标签表（客户和标签关联关系）
 * 对应数据库表: customer_label_info
 */
@Data
@TableName("customer_label_info")
public class CustomerLabelInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 标签ID */
    @TableField("label_id")
    private Long labelId;

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