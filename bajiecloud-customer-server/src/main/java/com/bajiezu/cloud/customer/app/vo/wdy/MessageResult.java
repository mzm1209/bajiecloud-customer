package com.bajiezu.cloud.customer.app.vo.wdy;

import lombok.Data;

@Data
public class MessageResult {

    private Integer code;        // 单条消息处理结果
    private String message;      // 结果描述
    private String phone;        // 手机号码
    private Long msgId;          // 消息ID
    private Integer smsCount;    // 此号码的计费条数
}