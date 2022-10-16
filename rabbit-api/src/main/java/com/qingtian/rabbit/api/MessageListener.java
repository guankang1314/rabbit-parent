package com.qingtian.rabbit.api;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息监听者
 * @date 2022-10-16 20:06
 */
public interface MessageListener {

    /**
     * 消费消息
     * @param message
     */
    void onMessage(Message message);
}
