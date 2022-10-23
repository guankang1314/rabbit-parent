package com.qingtian.rabbit.task.parser;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * @author Guank
 * @version 1.0
 * @description: 定制化es-job的SpringJobScheduler的BeanDefinitionBuilder
 * @date 2022-10-23 17:42
 */
@FunctionalInterface
public interface SpringJobSchedulerCustomizer {

  void customize(BeanDefinitionBuilder beanDefinitionBuilder);
}
