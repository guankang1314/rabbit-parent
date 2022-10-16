package com.qingtian.rabbit.api.exception;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息运行时异常
 * @date 2022-10-16 19:53
 */
public class MessageRunTimeException extends RuntimeException{
    private static final long serialVersionUID = 7022075442258978614L;

    public MessageRunTimeException() {
        super();
    }

    public MessageRunTimeException(String message) {
        super(message);
    }

    public MessageRunTimeException(String message,Throwable cause) {
        super(message,cause);
    }

    public MessageRunTimeException(Throwable cause) {
        super(cause);
    }
}
