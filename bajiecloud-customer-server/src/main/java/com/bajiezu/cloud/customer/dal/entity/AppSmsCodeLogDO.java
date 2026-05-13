package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("app_sms_code_log")
public class AppSmsCodeLogDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("country_code")
    private String countryCode;
    @TableField("mobile")
    private String mobile;
    @TableField("mobile_hash")
    private String mobileHash;
    @TableField("scene")
    private String scene;
    @TableField("sms_code_hash")
    private String smsCodeHash;
    @TableField("salt")
    private String salt;
    @TableField("expire_time")
    private Date expireTime;
    @TableField("verify_status")
    private Integer verifyStatus;
    @TableField("verify_count")
    private Integer verifyCount;
    @TableField("send_status")
    private Integer sendStatus;
    @TableField("device_id")
    private String deviceId;
    @TableField("request_ip")
    private String requestIp;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
}
