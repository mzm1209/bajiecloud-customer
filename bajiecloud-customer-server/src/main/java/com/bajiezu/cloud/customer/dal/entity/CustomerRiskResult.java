package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户风控结果记录表
 * 对应数据库表: customer_risk_result
 */
@Data
@TableName("customer_risk_result")
public class CustomerRiskResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 风控事件ID（全局唯一，用于幂等） */
    @TableField("risk_event_id")
    private String riskEventId;

    /** 风控场景：LOGIN, ORDER, PAYMENT, WITHDRAW, ACTIVITY */
    @TableField("risk_scene")
    private String riskScene;

    /** 风险等级：1-低风险，2-中风险，3-高风险 */
    @TableField("risk_level")
    private Integer riskLevel;

    /** 风险评分（0-1000分，越高越风险） */
    @TableField("risk_score")
    private Integer riskScore;

    /** 信用评分 */
    @TableField("credit_score")
    private Integer creditScore;

    /** 风险标签数组：["新设备", "异地登录", "高频尝试"] (JSON格式) */
    @TableField("risk_tags")
    private String riskTags;

    /** 完整风控结果详情（原始响应或关键指标） (JSON格式) */
    @TableField("risk_detail")
    private String riskDetail;

    /** 触发风控的请求上下文（IP、设备、行为等） (JSON格式) */
    @TableField("request_context")
    private String requestContext;

    /** 风控处置建议：PASS, REVIEW, REJECT, CHALLENGE */
    @TableField("decision_action")
    private String decisionAction;

    /** 是否触发人工审核：0-否，1-是 */
    @TableField("is_manual_review")
    private Boolean isManualReview;

    /** 人工审核状态：1-待审核，2-审核通过，3-审核拒绝 */
    @TableField("review_status")
    private Integer reviewStatus;

    /** 风险结果过期时间（用于临时风险） */
    @TableField("expire_time")
    private Date expireTime;

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