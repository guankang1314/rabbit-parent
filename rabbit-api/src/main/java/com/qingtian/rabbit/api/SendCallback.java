package com.qingtian.rabbit.api;

/**
 * @author Guank
 * @version 1.0
 * @description: 回调函数处理
 * @date 2022-10-16 20:02
 */
public interface SendCallback {

    void onSuccess();

    void onFailure();
}
