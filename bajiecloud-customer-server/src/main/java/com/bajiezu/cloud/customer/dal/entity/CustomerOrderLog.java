package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("customer_order_log")
public class CustomerOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO) // 假设ID由外部生成（如雪花ID）
    private Long id;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 消息ID */
    @TableField("msg_id")
    private String msgId;

    /** 订单Id */
    @TableField("order_id")
    private Long orderId;

    /** 订单编号 */
    @TableField("order_no")
    private String orderNo;

    /** 处理状态 0 待处理 1 已处理 */
    @TableField("status")
    private Integer status;

    /** 下单时间 */
    @TableField("order_time")
    private Date orderTime;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;

}
