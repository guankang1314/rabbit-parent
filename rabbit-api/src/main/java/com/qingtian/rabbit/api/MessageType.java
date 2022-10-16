package com.qingtian.rabbit.api;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息类型
 * @date 2022-10-15 21:24
 */
public final class MessageType {

    /**
     * 迅速消息：不需要保证消息的可靠性，也不需要进行 confirm 确认
     */
    public static final String RAPID = "0";

    /**
     * 确认消息：不需要保证消息的可靠性，需要进行 confirm 确认
     */
    public static final String CONFIRM = "1";

    /**
     * 可靠性消息：需要保证消息的可靠性，需要进行 confirm 确认
     */
    public static final String RELIANT = "2";
}
