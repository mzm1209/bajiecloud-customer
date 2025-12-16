package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户合并日志表
 * 对应数据库表: merge_log
 */
@Data
@TableName("merge_log")
public class MergeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 合并ID */
    @TableId(value = "merge_id", type = IdType.AUTO)
    private Long mergeId;

    /** 合并类型：1-自动合并，2-用户手动合并，3-管理员合并 */
    @TableField("merge_type")
    private Integer mergeType;

    /** 合并规则：MobileMatch, IDCardMatch, ThirdPartyMatch */
    @TableField("merge_rule")
    private String mergeRule;

    /** 主账户ID */
    @TableField("master_customer_id")
    private Long masterCustomerId;

    /** 从账户ID列表 (JSON格式存储) */
    @TableField("slave_customer_ids")
    private String slaveCustomerIds; // 映射为 String 或使用 TypeHandler 处理 JSON

    /** 合并状态：1-进行中，2-成功，3-失败，4-已回滚 */
    @TableField("merge_status")
    private Integer mergeStatus;

    /** 合并前数据快照 (JSON格式存储) */
    @TableField("merge_before_snapshot")
    private String mergeBeforeSnapshot;

    /** 合并后数据快照 (JSON格式存储) */
    @TableField("merge_after_snapshot")
    private String mergeAfterSnapshot;

    /** 业务数据迁移结果 (JSON格式存储) */
    @TableField("business_transfer_result")
    private String businessTransferResult;

    /** 操作人ID（用户或管理员） */
    @TableField("operator_id")
    private Long operatorId;

    /** 操作人类型：System, User, Admin */
    @TableField("operator_type")
    private String operatorType;

    /** 合并备注 */
    @TableField("merge_remark")
    private String mergeRemark;

    /** 回滚原因 */
    @TableField("rollback_reason")
    private String rollbackReason;

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
