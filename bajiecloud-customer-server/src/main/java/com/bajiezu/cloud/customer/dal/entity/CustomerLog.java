package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户操作日志表
 * 对应数据库表: customer_log
 */
@Data
@TableName("customer_log")
public class CustomerLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO) // 假设ID由外部生成（如雪花ID）
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 操作 类型 */
    @TableField("operate_type")
    private String operateType;

    /** 操作人 */
    @TableField("operator_id")
    private Long operatorId;

    /** 操作描述 */
    @TableField("action_desc")
    private String actionDesc; // 对应 TEXT 类型

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
