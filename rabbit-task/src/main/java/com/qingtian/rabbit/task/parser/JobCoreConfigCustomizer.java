package com.qingtian.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;

/**
 * @author Guank
 * @version 1.0
 * @description: 定制化es-job的核心configuration
 * @date 2022-10-23 17:31
 */
@FunctionalInterface
public interface JobCoreConfigCustomizer {

  void customize(JobCoreConfiguration coreConfig);
}
