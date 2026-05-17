package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.vo.wdy.SmsSendOneResponse;

public interface WeiDaoYunSmsService {

    /**
     * 短信一对一发送（单条消息）
     */
    SmsSendOneResponse sendSingleMessage(String phone, String content);
}

