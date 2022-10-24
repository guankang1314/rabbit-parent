package com.qingtian.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.qingtian.rabbit.api.Message;
import com.qingtian.rabbit.api.MessageProducer;
import com.qingtian.rabbit.api.MessageType;
import com.qingtian.rabbit.api.SendCallback;
import com.qingtian.rabbit.api.exception.MessageRunTimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息生产者实现
 * @date 2022-10-16 21:53
 */

public class ProducerClient implements MessageProducer {

    private final RabbitBroker rabbitBroker;

    public ProducerClient(RabbitBroker rabbitBroker) {
        this.rabbitBroker = rabbitBroker;
    }

    @Override
    public void send(Message message) throws MessageRunTimeException {
        Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {

    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {

    }
}
