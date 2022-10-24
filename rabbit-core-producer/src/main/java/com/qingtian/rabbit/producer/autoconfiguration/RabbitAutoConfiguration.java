package com.qingtian.rabbit.producer.autoconfiguration;

import com.qingtian.rabbit.producer.broker.ProducerClient;
import com.qingtian.rabbit.producer.broker.RabbitBroker;
import com.qingtian.rabbit.producer.broker.RabbitBrokerImpl;
import com.qingtian.rabbit.producer.broker.RabbitTemplateContainer;
import com.qingtian.rabbit.producer.mapper.BrokerMessageMapper;
import com.qingtian.rabbit.producer.service.MessageStoreService;
import com.qingtian.rabbit.producer.task.RetryMessageDataflowJob;
import com.qingtian.rabbit.task.annotation.EnableElasticJob;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-16 21:49
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.qingtian.rabbit.producer.mapper"})
public class RabbitAutoConfiguration {

  @Bean
  public RabbitBroker rabbitBroker(
      RabbitTemplateContainer rabbitTemplateContainer, MessageStoreService messageStoreService) {
    return new RabbitBrokerImpl(rabbitTemplateContainer, messageStoreService);
  }

  @Bean
  public ProducerClient producerClient(RabbitBroker rabbitBroker) {
    return new ProducerClient(rabbitBroker);
  }

  @Bean
  public RabbitTemplateContainer rabbitTemplateContainer(ConnectionFactory connectionFactory,
      MessageStoreService messageStoreService) {
    return new RabbitTemplateContainer(connectionFactory, messageStoreService);
  }

  @Bean
  public MessageStoreService messageStoreService(BrokerMessageMapper brokerMessageMapper) {
    return new MessageStoreService(brokerMessageMapper);
  }

  @Bean
  public RetryMessageDataflowJob retryMessageDataflowJob(MessageStoreService messageStoreService,
      RabbitBroker rabbitBroker) {
    return new RetryMessageDataflowJob(messageStoreService, rabbitBroker);
  }

}
