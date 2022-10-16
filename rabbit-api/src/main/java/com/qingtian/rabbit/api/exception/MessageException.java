package com.qingtian.rabbit.api.exception;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息异常
 * @date 2022-10-16 19:50
 */
public class MessageException extends Exception{
    private static final long serialVersionUID = -178120068982336036L;

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message,Throwable cause) {
        super(message,cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }
}
