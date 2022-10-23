package com.qingtian.rabbit.task.autoconfiguration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.qingtian.rabbit.task.parser.ElasticJobConfigParser;
import com.qingtian.rabbit.task.parser.JobConfigCustomizer;
import com.qingtian.rabbit.task.parser.JobCoreConfigCustomizer;
import com.qingtian.rabbit.task.parser.JobTypeNamesCustomizer;
import com.qingtian.rabbit.task.parser.SpringJobSchedulerCustomizer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Guank
 * @version 1.0
 * @description: 自动装配
 * @date 2022-10-23 10:01
 */
@Configuration
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverLists"}, matchIfMissing = false)
@EnableConfigurationProperties({JobZookeeperProperties.class})
@Slf4j
public class JobParserAutoConfiguration {

  @Bean(initMethod = "init", name = "zookeeperRegistryCenter")
  public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobZookeeperProperties) {
    ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(jobZookeeperProperties.getServerLists(),
        jobZookeeperProperties.getNamespace());
    zookeeperConfiguration.setDigest(jobZookeeperProperties.getDigest());
    zookeeperConfiguration.setMaxRetries(jobZookeeperProperties.getMaxRetries());
    zookeeperConfiguration.setBaseSleepTimeMilliseconds(jobZookeeperProperties.getBaseSleepTimeMilliseconds());
    zookeeperConfiguration.setConnectionTimeoutMilliseconds(jobZookeeperProperties.getConnectionTimeoutMilliseconds());
    zookeeperConfiguration.setSessionTimeoutMilliseconds(jobZookeeperProperties.getSessionTimeoutMilliseconds());
    zookeeperConfiguration.setMaxSleepTimeMilliseconds(jobZookeeperProperties.getMaxSleepTimeMilliseconds());
    log.info("初始化job注册中心配置成功, zkAddress : [{}], namespace : [{}]", jobZookeeperProperties.getServerLists(),
        jobZookeeperProperties.getNamespace());
    return new ZookeeperRegistryCenter(zookeeperConfiguration);
  }

  @Bean
  @ConditionalOnMissingBean
  public ElasticJobConfigParser elasticJobConfigParser(JobZookeeperProperties jobZookeeperProperties,
      ZookeeperRegistryCenter zookeeperRegistryCenter,
      ObjectProvider<List<JobTypeNamesCustomizer>> jobTypeNamesCustomizers,
      ObjectProvider<List<JobCoreConfigCustomizer>> jobCoreConfigCustomizers,
      ObjectProvider<List<JobConfigCustomizer>> jobConfigCustomizers,
      ObjectProvider<List<SpringJobSchedulerCustomizer>> springJobSchedulerCustomizers) {
    return new ElasticJobConfigParser(jobZookeeperProperties, zookeeperRegistryCenter, jobTypeNamesCustomizers,
        jobCoreConfigCustomizers, jobConfigCustomizers, springJobSchedulerCustomizers);
  }

}
