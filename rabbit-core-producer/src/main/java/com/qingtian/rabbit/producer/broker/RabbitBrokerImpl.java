package com.qingtian.rabbit.producer.broker;

import com.qingtian.rabbit.api.Message;
import com.qingtian.rabbit.api.MessageType;
import com.qingtian.rabbit.producer.constant.BrokerMessageConst;
import com.qingtian.rabbit.producer.constant.BrokerMessageStatus;
import com.qingtian.rabbit.producer.entity.BrokerMessage;
import com.qingtian.rabbit.producer.service.MessageStoreService;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
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
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

  private final RabbitTemplateContainer rabbitTemplateContainer;

  private final MessageStoreService messageStoreService;

  public RabbitBrokerImpl(RabbitTemplateContainer rabbitTemplateContainer, MessageStoreService messageStoreService) {
    this.rabbitTemplateContainer = rabbitTemplateContainer;
    this.messageStoreService = messageStoreService;
  }

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
          String.format("%s#%s#%s", message.getMessageId(), System.currentTimeMillis(), message.getMessageType()));
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
    message.setMessageType(MessageType.RELIANT);

    BrokerMessage msg = messageStoreService.selectByPrimaryKey(message.getMessageId());
    if (null == msg) {
      // 在向MQ发送消息之前，将消息记录在本地入库
      Date now = new Date();
      BrokerMessage brokerMessage = new BrokerMessage();
      brokerMessage.setMessageId(message.getMessageId());
      brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
      // tryCount在一开始发送的时候不用设置默认为 0
      // 设置下一次重试时间
      brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
      brokerMessage.setCreateTime(now);
      brokerMessage.setUpdateTime(now);
      brokerMessage.setMessage(message);
      messageStoreService.insert(brokerMessage);
    }
    // 发送消息
    sendKernel(message);
  }

  @Override
  public void sendMessages() {

  }
}
