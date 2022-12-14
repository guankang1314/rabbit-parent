package com.qingtian.rabbit.common.convert;

import com.google.common.base.Preconditions;
import com.qingtian.rabbit.common.serializer.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-19 23:40
 */
public class GenericMessageConverter implements MessageConverter {

  private Serializer serializer;

  public GenericMessageConverter(Serializer serializer) {
    Preconditions.checkNotNull(serializer);
    this.serializer = serializer;
  }


  @Override
  public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
    return new Message(this.serializer.serializeRaw(o),messageProperties);
  }

  @Override
  public Object fromMessage(Message message) throws MessageConversionException {
    return this.serializer.deserialize(message.getBody());
  }
}
