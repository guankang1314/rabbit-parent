package com.qingtian.rabbit.producer.service;

import com.qingtian.rabbit.producer.constant.BrokerMessageStatus;
import com.qingtian.rabbit.producer.entity.BrokerMessage;
import com.qingtian.rabbit.producer.mapper.BrokerMessageMapper;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Guank
 * @version 1.0
 * @description: 可靠性信息投递 消息本地存储
 * @date 2022-10-22 12:34
 */
@Service
public class MessageStoreService {

  @Autowired
  private BrokerMessageMapper brokerMessageMapper;

  public Integer insert(BrokerMessage message) {
    return brokerMessageMapper.insert(message);
  }

  /**
   * 更新对应的消息状态为发送成功
   *
   * @param messageId
   */
  public void success(String messageId) {
    brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK.getCode(), new Date());
  }

  public void failure(String messageId) {
    brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_FAIL.getCode(), new Date());
  }
}
