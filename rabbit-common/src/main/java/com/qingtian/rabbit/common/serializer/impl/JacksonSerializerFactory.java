package com.qingtian.rabbit.common.serializer.impl;

import com.qingtian.rabbit.api.Message;
import com.qingtian.rabbit.common.serializer.Serializer;
import com.qingtian.rabbit.common.serializer.SerializerFactory;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-19 23:36
 */
public class JacksonSerializerFactory implements SerializerFactory {

  public static final JacksonSerializerFactory INSTANCE = new JacksonSerializerFactory();

  @Override
  public Serializer create() {
    return JacksonSerializer.createParametricType(Message.class);
  }
}
