package com.bajiezu.cloud.customer.app.vo.wdy;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 短信一对一发送响应
 */
@Data
@NoArgsConstructor
public class SmsSendOneResponse {
    private Integer code;                    // 响应码
    private String message;                  // 响应消息
    private Integer smsCount;                // 消耗的短信条数
    private List<MessageResult> data;        // 发送结果明细

    public SmsSendOneResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
