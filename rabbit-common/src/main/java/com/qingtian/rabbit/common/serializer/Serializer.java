package com.qingtian.rabbit.common.serializer;

/**
 * @author Guank
 * @version 1.0
 * @description: 序列化和反序列化的接口
 * @date 2022-10-19 23:25
 */
public interface Serializer {

  byte[] serializeRaw(Object data);

  String serialize(Object data);

  <T> T deserialize(String content);

  <T> T deserialize(byte[] content);
}
