package com.qingtian.rabbit.api;

import com.qingtian.rabbit.api.exception.MessageRunTimeException;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-15 21:34
 */
public class MessageBuilder {

    /**
     * 唯一消息id
     */
    private String messageId;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息路由规则
     */
    private String routingKey = "";

    /**
     * 属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 延迟消息的时间
     */
    private Integer delayMills;

    /**
     * 消息类型 默认是 confirm 类型的
     */
    private String messageType = MessageType.CONFIRM;

    private MessageBuilder() {}

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder withDelayMills(Integer delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MessageBuilder withMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message build() {

        // 检查 messageId
        if (StringUtils.isEmpty(messageId)) {
            messageId = UUID.randomUUID().toString();
        }

        if (StringUtils.isEmpty(topic)) {
            throw new MessageRunTimeException("this topic is null");
        }

        return new Message(messageId,topic,routingKey,attributes,delayMills,messageType);
    }
}

