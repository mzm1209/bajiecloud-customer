package com.bajiezu.cloud.customer.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("app_file_upload")
public class AppFileUploadDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("customer_id")
    private Long customerId;
    @TableField("biz_type")
    private String bizType;
    @TableField("file_type")
    private String fileType;
    @TableField("file_name")
    private String fileName;
    @TableField("file_key")
    private String fileKey;
    @TableField("file_size")
    private Long fileSize;
    @TableField("mime_type")
    private String mimeType;
    @TableField("file_hash")
    private String fileHash;
    @TableField("upload_status")
    private Integer uploadStatus;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("is_deleted")
    private Integer isDeleted;
}
