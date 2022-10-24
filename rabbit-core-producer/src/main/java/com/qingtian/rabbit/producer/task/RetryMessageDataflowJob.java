package com.qingtian.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.qingtian.rabbit.producer.broker.RabbitBroker;
import com.qingtian.rabbit.producer.constant.BrokerMessageStatus;
import com.qingtian.rabbit.producer.entity.BrokerMessage;
import com.qingtian.rabbit.producer.service.MessageStoreService;
import com.qingtian.rabbit.task.annotation.ElasticJobConfig;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Guank
 * @version 1.0
 * @description: 重试消息投递定时任务
 * @date 2022-10-23 19:19
 */
@ElasticJobConfig(
    name = "com.qingtian.rabbit.producer.task.RetryMessageDataflowJob",
    cron = "0/10 * * * * ? ",
    description = "可靠性投递消息补偿任务",
    overwrite = true,
    shardingTotalCount = 1
)
@Slf4j
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {

  private final MessageStoreService messageStoreService;

  private final RabbitBroker rabbitBroker;

  /**
   * 最大重试次数
   */
  private static final Integer MAX_RETRY = 3;

  public RetryMessageDataflowJob(MessageStoreService messageStoreService, RabbitBroker rabbitBroker) {
    this.messageStoreService = messageStoreService;
    this.rabbitBroker = rabbitBroker;
  }

  @Override
  public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
    List<BrokerMessage> list = messageStoreService.fetchTimeoutMessage4Retry(BrokerMessageStatus.SENDING);
    log.info("抓取数据集合，数量 : [{}]",list.size());
    return list;
  }

  @Override
  public void processData(ShardingContext shardingContext, List<BrokerMessage> list) {
    list.forEach(msg -> {
      if (msg.getTryCount() > MAX_RETRY) {
        messageStoreService.failure(msg.getMessageId());
        log.warn("消息重试最终失败，消息状态设置为失败");
      }else {
        // 每次更新时增加重试次数
        messageStoreService.update4TryCount(msg.getMessageId());
        rabbitBroker.reliantSend(msg.getMessage());
      }
    });
  }
}
