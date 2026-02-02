package com.bajiezu.cloud.customer.consumer;

import de.danielbechler.util.Assert;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.util.StringUtils;
import com.bajiezu.cloud.rocketmq.config.RocketMQProperties;

@Slf4j
public abstract class AbstractRocketMQConsumer {


    /**
     * 获取主题配置的键名
     *
     * @return 主题键名
     */
    protected abstract String topicKey();

    /**
     * 获取RocketMQ配置属性
     *
     * @return RocketMQ配置属性对象
     */
    protected abstract RocketMQProperties rocketMQProperties();

    /**
     * 获取NameServer地址
     *
     * @return NameServer地址
     */
    protected abstract String nameServer();

    /**
     * 获取消费者访问密钥
     *
     * @return 访问密钥
     */
    protected abstract String consumerAccessKey();

    /**
     * 获取消费者密钥
     *
     * @return 密钥
     */
    protected abstract String consumerSecretKey();

    /**
     * 处理接收到的消息
     *
     * @param message 待处理的消息对象
     * @throws Exception 处理消息时可能抛出的异常
     */
    protected abstract void handleMessage(MessageExt message) throws Exception;

    /**
     * RocketMQ推送消费者实例
     */
    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void start() throws MQClientException {
        RocketMQProperties props = rocketMQProperties();

        String topic = props.getTopicName(topicKey());
        String group = props.getConsumerGroup(topicKey());

        Assert.hasText(topic, "topic 不能为空");
        Assert.hasText(group, "consumerGroup 不能为空");

        RPCHook rpcHook = null;
        String accessKey = consumerAccessKey();
        String secretKey = consumerSecretKey();
        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            rpcHook = new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
        }

        consumer = new DefaultMQPushConsumer(group, rpcHook);
        consumer.setNamesrvAddr(nameServer());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        consumer.subscribe(topic, "*");

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    handleMessage(msg);
                } catch (Exception e) {
                    log.error("消息消费失败 msgId={}", msg.getMsgId(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

        log.info("RocketMQ Consumer started, topic={}, group={}", topic, group);
    }

    @PreDestroy
    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }
}

