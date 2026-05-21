package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.bajiezu.cloud.customer.app.service.WeiDaoYunSmsService;
import com.bajiezu.cloud.customer.app.vo.wdy.MessageItem;
import com.bajiezu.cloud.customer.app.vo.wdy.MessageResult;
import com.bajiezu.cloud.customer.app.vo.wdy.SmsSendOneResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 蔚岛云短信服务客户端
 * 基于Hutool 5.8.42实现
 */
@Slf4j
@Service
public class WeiDaoYunSmsServiceImpl implements WeiDaoYunSmsService {

    @Value("${sms.weidaoyun.singleUrl}")
    private String singleMsgUrl;
    @Value("${sms.weidaoyun.userName}")
    private String userName;
    @Value("${sms.weidaoyun.password}")
    private String password;

    @Override
    public SmsSendOneResponse sendSingleMessage(String phone, String content) {
        List<MessageItem> messageList = new ArrayList<>();
        messageList.add(new MessageItem(phone, content));
        log.debug("待发送内容===== "+messageList);
        return sendMessageOne(messageList);
    }


    /**
     * 短信一对一发送
     * 地址: /api/sendMessageOne
     */
    public SmsSendOneResponse sendMessageOne(List<MessageItem> messageList) {
        try {
            // 1. 准备参数
            long timestamp = System.currentTimeMillis();
            String sign = calculateSign(timestamp);

            // 2. 构建请求体
            JSONObject requestBody = buildRequestBody(messageList, timestamp, sign);

            // 3.发送请求
            HttpResponse response = HttpRequest.post(singleMsgUrl)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json;charset=utf-8")
                    .body(requestBody.toString())
                    .timeout(30000) // 30秒超时
                    .execute();
            log.info("响应体: " + response.body());

            // 4. 解析响应
            return parseResponse(response);
        } catch (Exception e) {
            log.error("短信发送失败: " + e.getMessage(), e);
            return new SmsSendOneResponse(-1, "短信发送失败");
        }
    }

    /**
     * 计算签名
     * 规则: MD5(userName + timestamp + MD5(password))
     */
    private String calculateSign(long timestamp) {
        String md5Password = DigestUtil.md5Hex(password); // 计算MD5(password)
        String rawString = userName + timestamp + md5Password;
        return DigestUtil.md5Hex(rawString);
    }

    /**
     * 构建请求体
     */
    private JSONObject buildRequestBody(List<MessageItem> messageList, long timestamp, String sign) {
        JSONObject requestBody = new JSONObject();
        requestBody.putOpt("userName", userName);
        requestBody.putOpt("timestamp", timestamp);
        requestBody.putOpt("sign", sign);

        // 构建messageList数组
        JSONArray messageArray = new JSONArray();
        for (MessageItem item : messageList) {
            JSONObject messageObj = new JSONObject();
            messageObj.putOpt("phone", item.getPhone());
            messageObj.putOpt("content", item.getContent());

            // 可选参数
            if (item.getExtcode() != null && !item.getExtcode().isEmpty()) {
                messageObj.putOpt("extcode", item.getExtcode());
            }
            if (item.getCallData() != null && !item.getCallData().isEmpty()) {
                messageObj.putOpt("callData", item.getCallData());
            }
            messageArray.add(messageObj);
        }
        requestBody.putOpt("messageList", messageArray);

        return requestBody;
    }

    /**
     * 解析响应结果
     */
    private SmsSendOneResponse parseResponse(HttpResponse response) {
        try {
            if (response.isOk()) {
                String responseBody = response.body();
                JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
                SmsSendOneResponse result = new SmsSendOneResponse();
                result.setCode(jsonResponse.getInt("code"));
                result.setMessage(jsonResponse.getStr("message"));
                if (jsonResponse.containsKey("smsCount")) {
                    result.setSmsCount(jsonResponse.getInt("smsCount"));
                }

                // 解析data数组
                if (jsonResponse.containsKey("data") && jsonResponse.getJSONArray("data") != null) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                    List<MessageResult> dataList = new ArrayList<>();

                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        MessageResult messageResult = new MessageResult();
                        messageResult.setCode(item.getInt("code"));
                        messageResult.setMessage(item.getStr("message"));
                        messageResult.setPhone(item.getStr("phone"));
                        if (item.containsKey("msgId")) {
                            messageResult.setMsgId(item.getLong("msgId"));
                        }

                        if (item.containsKey("smsCount")) {
                            messageResult.setSmsCount(item.getInt("smsCount"));
                        }
                        dataList.add(messageResult);
                    }
                    result.setData(dataList);
                }
                return result;
            } else {
                SmsSendOneResponse errorResult = new SmsSendOneResponse();
                errorResult.setCode(-1);
                errorResult.setMessage("HTTP请求失败: " + response.getStatus());
                return errorResult;
            }
        } catch (Exception e) {
            log.error("解析响应失败: " + e.getMessage(), e);
            SmsSendOneResponse errorResult = new SmsSendOneResponse();
            errorResult.setCode(-1);
            errorResult.setMessage("解析响应失败: " + e.getMessage());
            return errorResult;
        }
    }
}