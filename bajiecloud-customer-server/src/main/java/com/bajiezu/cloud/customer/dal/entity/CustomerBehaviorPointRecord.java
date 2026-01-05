package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户积分操作行为日志表
 * 对应数据库表: customer_behavior_point_record
 */
@Data
@TableName("customer_behavior_point_record")
public class CustomerBehaviorPointRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 积分类型 1:任务配置 2:全局配置 */
    @TableField("setting_type")
    private Integer settingType;

    /** 关联积分任务ID */
    @TableField("task_id")
    private Long taskId;

    /** 关联规则ID */
    @TableField("rule_id")
    private Long ruleId;

    /** 积分数值 */
    @TableField("point_count")
    private Long pointCount;

    /** 成长数值 */
    @TableField("growth_count")
    private Long growthCount;

    /** 操作动作 1: 发放 2:扣减 */
    @TableField("operating_action")
    private Integer operatingAction;

    /** 行为code值 */
    @TableField("behavior_code")
    private Integer behaviorCode;

    /** 行为描述 */
    @TableField("behavior_desc")
    private String behaviorDesc;

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
}