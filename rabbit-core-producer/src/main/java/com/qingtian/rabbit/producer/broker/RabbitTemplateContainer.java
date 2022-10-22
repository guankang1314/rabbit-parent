package com.qingtian.rabbit.producer.broker;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.qingtian.rabbit.api.Message;
import com.qingtian.rabbit.api.MessageType;
import com.qingtian.rabbit.api.exception.MessageRunTimeException;
import com.qingtian.rabbit.common.convert.GenericMessageConverter;
import com.qingtian.rabbit.common.convert.RabbitMessageConverter;
import com.qingtian.rabbit.common.serializer.SerializerFactory;
import com.qingtian.rabbit.common.serializer.impl.JacksonSerializerFactory;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Guank
 * @version 1.0
 * @description: RabbitTemplate 池化封装 1. 每一个 topic 对应一个 RabbitTemplate 可以针对不同的 topic 定制不同的 RabbitTemplate 2. 提高发送效率
 * @date 2022-10-16 23:36
 */
@Component
@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

  private Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

  private Splitter splitter = Splitter.on("#");

  private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

  @Autowired
  private ConnectionFactory connectionFactory;

  public RabbitTemplate getTemplate(Message message) throws MessageRunTimeException {
    Preconditions.checkNotNull(message);
    String topic = message.getTopic();
    RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
    if (null != rabbitTemplate) {
      return rabbitTemplate;
    }

    log.info("#RabbitTemplateContainer.getTemplate# topic : [{}] is not exist, create one", topic);

    RabbitTemplate newRabbitTemplate = new RabbitTemplate(connectionFactory);
    newRabbitTemplate.setExchange(topic);
    newRabbitTemplate.setRoutingKey(message.getRoutingKey());
    newRabbitTemplate.setRetryTemplate(new RetryTemplate());

    // 对于 message 的序列化方式
    newRabbitTemplate.setMessageConverter(
        new RabbitMessageConverter(new GenericMessageConverter(serializerFactory.create())));
    // 只要消息类型不是迅速消息 就需要设置 ConfirmCallback
    if (!MessageType.RAPID.equals(message.getMessageType())) {
      newRabbitTemplate.setMandatory(true);
      newRabbitTemplate.setConfirmCallback(this);
      newRabbitTemplate.setReturnCallback(this);
    }


    rabbitMap.putIfAbsent(topic, newRabbitTemplate);
    return rabbitMap.get(topic);
  }

  /**
   * 确认消息发送到 broker
   *
   * @param correlationData
   * @param ack
   * @param cause
   */
  @Override
  public void confirm(CorrelationData correlationData, boolean ack, String cause) {
    List<String> strings = splitter.splitToList(correlationData.getId());
    String messageId = strings.get(0);
    Long sendTime = Long.parseLong(strings.get(1));

    if (ack) {
      log.info("send message is OK, confirm messageId: [{}]， sendTime : [{}]", messageId, sendTime);
    } else {
      log.error("send message is FAIL, confirm messageId: [{}]， sendTime : [{}]", messageId, sendTime);
    }
  }

  /**
   * broker 发送到 queue 失败后回调
   * @param message
   * @param replyCode
   * @param replyText
   * @param exchange
   * @param routingKey
   */
  @Override
  public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
    log.warn("send message to queue failed message : [{}],replyCode : [{}],"
        + "replyText : [{}],exchange : [{}], routingKey : [{}]", JSON.toJSONString(message),replyCode,replyText,
        exchange,routingKey);
  }
}
