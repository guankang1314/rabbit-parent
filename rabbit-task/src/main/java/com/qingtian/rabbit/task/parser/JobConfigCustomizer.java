package com.qingtian.rabbit.task.parser;

import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;

/**
 * @author Guank
 * @version 1.0
 * @description: 定制化es-job的核心 LiteJobConfiguration
 * @date 2022-10-23 17:37
 */
@FunctionalInterface
public interface JobConfigCustomizer {

  void customize(LiteJobConfiguration jobConfig);
}
