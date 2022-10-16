package com.qingtian.rabbit.producer.broker;

import com.qingtian.rabbit.api.Message;

/**
 * @author Guank
 * @version 1.0
 * @description: 具体发送不同种类消息的接口
 * @date 2022-10-16 22:03
 */
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessages();
}
