package com.bajiezu.cloud.customer.app.vo.wdy;

import lombok.Data;

import java.util.Map;

/**
 * 短信消息项
 */
@Data
public class MessageItem {
    private String phone;                // 手机号码（必需）
    private String content;              // 短信内容（与templateId二选一）
    private Integer templateId;          // 短信模板ID（与content二选一）
    private Map<String, String> params;  // 模板变量参数（使用模板时可选）
    private String extcode;              // 扩展码（可选）
    private String callData;             // 用户回传数据（可选）

    public MessageItem() {
    }

    public MessageItem(String phone, String content) {
        this.phone = phone;
        this.content = content;
    }

    public MessageItem(String phone, Integer templateId, Map<String, String> params) {
        this.phone = phone;
        this.templateId = templateId;
        this.params = params;
    }
}