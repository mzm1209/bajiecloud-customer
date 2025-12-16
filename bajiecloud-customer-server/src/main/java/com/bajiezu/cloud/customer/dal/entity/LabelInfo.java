package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签信息表
 * 对应数据库表: label_info
 */
@Data
@TableName("label_info")
public class LabelInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 标签名称 */
    @TableField("name")
    private String name;

    /** 标签描述 */
    @TableField("description")
    private String description;

    /** 标签类型，如：1-手动添加 */
    @TableField("label_type")
    private Integer labelType;

    /** 标签状态：1-启用，0-禁用 */
    @TableField("label_status")
    private Integer labelStatus;

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