package com.bajiezu.cloud.customer.consumer;


import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorVO;
import com.bajiezu.cloud.customer.dal.entity.CustomerOrderLog;
import com.bajiezu.cloud.customer.dal.mapper.CustomerOrderLogMapper;
import com.bajiezu.cloud.customer.service.CustomerBehaviorService;
import com.bajiezu.cloud.customer.service.CustomerService;
import com.fasterxml.jackson.databind.JavaType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.bajiezu.cloud.rocketmq.config.RocketMQProperties;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "rocketmq", name = "name-server")
public class OrderMsgConsumer extends AbstractRocketMQConsumer{

    @Resource
    private RocketMQProperties rocketMQProperties;

    @Resource
    private CustomerService customerService;

    @Autowired
    private CustomerOrderLogMapper customerOrderLogMapper;

    @Autowired
    private CustomerBehaviorService customerBehaviorService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected String topicKey() {
        return "customer-service";
    }

    @Override
    protected RocketMQProperties rocketMQProperties() {
        return rocketMQProperties;
    }

    @Override
    protected String nameServer() {
        return rocketMQProperties.getNameServer();
    }

    @Override
    protected String consumerAccessKey() {
        return rocketMQProperties.getConsumer().getAccessKey();
    }

    @Override
    protected String consumerSecretKey() {
        return rocketMQProperties.getConsumer().getSecretKey();
    }

    @Override
    protected void handleMessage(MessageExt message) throws Exception {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("收到消息 topic={}, msgId={}, body={}", message.getTopic(), message.getMsgId(), body);

        String msgId = message.getMsgId();

        Map<String, Object> msgMap = null;
        try {
            JavaType mapType = OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            msgMap = OBJECT_MAPPER.readValue(body, mapType);
        } catch (Exception e) {
            log.error("消息JSON解析失败，msgId={}, body={}", msgId, body, e);
            return;
        }
        log.info("handleMessage get msgMap: {}", msgMap);

        String msgType = (msgMap == null) ? null : (String) msgMap.get("msgType");
        if (!"ORDER_CREATE".equals(msgType)) {
            log.info("非ORDER_CREATE类型消息，直接过滤，msgType={}, msgId={}", msgType, msgId);
            return;
        }
        String orderNo = (String) msgMap.get("orderNo");
        if (StringUtils.isBlank(orderNo)) {
            log.info("ORDER_CREATE消息中订单号为空/不存在，msgId={}, msgMap={}", msgId, msgMap);
            return;
        }

        Integer count = customerOrderLogMapper.queryCountByOrderNo(orderNo);
        if (count > 0) {
            log.info("ORDER_CREATE 消息中订单号为已处理，msgId={}, orderNo={}", msgId, orderNo);
            return;
        }

        Long customerId = null;
        Object customerIdObj = msgMap.get("customerId");
        if (customerIdObj != null) {
            customerId = Long.parseLong(customerIdObj.toString());
        }
        if (customerId == null) {
            log.info("消息中 customerId 为空");
            return;
        }

        Long orderId = null;
        Object orderIdObj = msgMap.get("orderId");
        if (orderIdObj != null) {
            orderId = Long.parseLong(orderIdObj.toString());
        }
        if (orderId == null) {
            log.info("消息中 orderId 为空");
            return;
        }
        log.info("handleMessage get msgId: {}, orderNo: {}, customerId: {}, orderId: {}", msgId, orderNo, customerId, orderId);
        insertCustomerOrderLog(msgId, orderNo, customerId, orderId);
        customerService.customerOrderUpdate(customerId);
        try {
            CustomerBehaviorVO behaviorVO = new CustomerBehaviorVO();
            behaviorVO.setCustomerId(customerId);
            behaviorVO.setBehaviorCode(1);
            customerBehaviorService.handleCustomerBehavior(behaviorVO);
        }catch (Exception e) {
            log.error("handleMessage customerBehavior error");
        }
    }



    private void insertCustomerOrderLog(String msgId, String orderNo, Long customerId, Long orderId) {
        CustomerOrderLog orderLog = new CustomerOrderLog();
        orderLog.setCustomerId(customerId);
        orderLog.setOrderNo(orderNo);
        orderLog.setMsgId(msgId);
        orderLog.setOrderId(orderId);
        orderLog.setStatus(0);
        orderLog.setOrderTime(new Date());
        orderLog.setCreateTime(new Date());
        customerOrderLogMapper.insert(orderLog);
    }

}
