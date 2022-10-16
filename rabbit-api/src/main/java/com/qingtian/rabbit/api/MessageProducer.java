package com.qingtian.rabbit.api;

import com.qingtian.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息发送
 * @date 2022-10-16 20:00
 */
public interface MessageProducer {

    /**
     * 发送消息
     * @param message
     * @throws MessageRunTimeException
     */
    void send(Message message) throws MessageRunTimeException;

    /**
     * 消息批量发送
     * @param messages
     * @throws MessageRunTimeException
     */
    void send(List<Message> messages) throws MessageRunTimeException;

    /**
     * 消息发送附带 sendCallback 回调函数执行
     * @param message
     * @param sendCallback
     * @throws MessageRunTimeException
     */
    void send(Message message, SendCallback sendCallback) throws MessageRunTimeException;
}
