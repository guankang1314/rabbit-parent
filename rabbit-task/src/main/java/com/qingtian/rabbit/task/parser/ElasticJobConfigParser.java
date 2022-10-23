package com.qingtian.rabbit.task.parser;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.qingtian.rabbit.task.autoconfiguration.JobZookeeperProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Guank
 * @version 1.0
 * @description: ElasticJob配置属性解析器
 * @date 2022-10-23 10:59
 */
@Slf4j
public class ElasticJobConfigParser implements ApplicationListener<ApplicationReadyEvent> {

  private JobZookeeperProperties jobZookeeperProperties;

  private ZookeeperRegistryCenter zookeeperRegistryCenter;

  public ElasticJobConfigParser(JobZookeeperProperties jobZookeeperProperties,
      ZookeeperRegistryCenter zookeeperRegistryCenter) {
    this.jobZookeeperProperties = jobZookeeperProperties;
    this.zookeeperRegistryCenter = zookeeperRegistryCenter;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

  }
}
