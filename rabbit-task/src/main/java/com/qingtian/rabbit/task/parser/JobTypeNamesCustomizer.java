package com.qingtian.rabbit.task.parser;

import java.util.List;

/**
 * @author Guank
 * @version 1.0
 * @description: 定制化需要生成的作业类型
 * @date 2022-10-23 17:09
 */
@FunctionalInterface
public interface JobTypeNamesCustomizer {

  void customize(List<String> jobTypeNames);
}
