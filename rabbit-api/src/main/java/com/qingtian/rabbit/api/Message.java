package com.qingtian.rabbit.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息类
 * @date 2022-10-15 21:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 2188563350536054620L;

    /**
     * 唯一消息id
     */
    @NonNull
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

    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, Integer delayMills) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
    }
}
