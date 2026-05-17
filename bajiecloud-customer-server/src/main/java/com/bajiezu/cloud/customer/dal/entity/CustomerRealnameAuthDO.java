package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("customer_realname_auth")
public class CustomerRealnameAuthDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("customer_id") private Long customerId;
    @TableField("real_name") private String realName;
    @TableField("id_card") private String idCard;
    @TableField("id_card_hash") private String idCardHash;
    @TableField("gender") private Integer gender;
    @TableField("birthday") private Date birthday;
    @TableField("ethnicity") private String ethnicity;
    @TableField("address") private String address;
    @TableField("issue_authority") private String issueAuthority;
    @TableField("id_card_valid_start") private Date idCardValidStart;
    @TableField("id_card_valid_end") private Date idCardValidEnd;
    @TableField("id_card_front_file_id") private Long idCardFrontFileId;
    @TableField("id_card_back_file_id") private Long idCardBackFileId;
    @TableField("auth_status") private Integer authStatus;
    @TableField("face_auth_status") private Integer faceAuthStatus;
    @TableField("fail_reason") private String failReason;
    @TableField("face_auth_result") private String faceAuthResult;
    @TableField("submit_time") private Date submitTime;
    @TableField("pass_time") private Date passTime;
    @TableField("create_time") private Date createTime;
    @TableField("update_time") private Date updateTime;
    @TableField("is_deleted") private Integer isDeleted;
}
