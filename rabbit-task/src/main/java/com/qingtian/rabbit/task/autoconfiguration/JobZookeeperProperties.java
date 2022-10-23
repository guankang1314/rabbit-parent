package com.qingtian.rabbit.task.autoconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Guank
 * @version 1.0
 * @description: elastic-job 配置属性
 * @date 2022-10-23 10:08
 */
@ConfigurationProperties(prefix = "elastic.job.zk")
@Data
public class JobZookeeperProperties {

  private String namespace;

  private String serverLists;

  private int connectionTimeoutMilliseconds = 15000;

  private int sessionTimeoutMilliseconds = 60000;

  private int maxRetries = 3;

  private int baseSleepTimeMilliseconds = 1000;

  private int maxSleepTimeMilliseconds = 3000;

  private String digest = "";
}
