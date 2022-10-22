package com.qingtian.rabbit.producer.broker;

import com.qingtian.rabbit.api.Message;
import com.qingtian.rabbit.api.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Guank
 * @version 1.0
 * @description: RabbitBroker 实现类
 * @date 2022-10-16 22:06
 */
@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

  @Autowired
  private RabbitTemplateContainer rabbitTemplateContainer;

  @Override
  public void rapidSend(Message message) {
    message.setMessageType(MessageType.RAPID);
    sendKernel(message);
  }

  /**
   * 发送消息核心方法, 使用异步线程池发迅速消息
   *
   * @param message
   */
  private void sendKernel(Message message) {
    AsyncBaseQueue.submit(() -> {
      CorrelationData correlationData = new CorrelationData(
          String.format("%s#%s", message.getMessageId(), System.currentTimeMillis()));
      RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
      rabbitTemplate.convertAndSend(message.getTopic(), message.getRoutingKey(), message,
          correlationData);
      log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId : [{}]",
          message.getMessageId());
    });

  }

  @Override
  public void confirmSend(Message message) {
    message.setMessageType(MessageType.CONFIRM);
    sendKernel(message);
  }

  @Override
  public void reliantSend(Message message) {

  }

  @Override
  public void sendMessages() {

  }
}
