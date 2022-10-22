package com.qingtian.rabbit.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-20 0:12
 */
public class RabbitMessageConverter implements MessageConverter {
  private GenericMessageConverter delegate;

  private final String defaultExprie = String.valueOf(24 * 60 * 60 * 1000);

  public RabbitMessageConverter(GenericMessageConverter genericMessageConverter) {
    Preconditions.checkNotNull(genericMessageConverter);
    this.delegate = genericMessageConverter;
  }


  @Override
  public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
    messageProperties.setExpiration(defaultExprie);
    return this.delegate.toMessage(o, messageProperties);
  }

  @Override
  public Object fromMessage(Message message) throws MessageConversionException {
    return this.delegate.fromMessage(message);
  }
}
