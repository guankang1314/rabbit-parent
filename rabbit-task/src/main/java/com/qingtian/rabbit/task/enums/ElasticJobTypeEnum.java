package com.qingtian.rabbit.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ElasticJob 作业类型枚举类
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ElasticJobTypeEnum {

	SIMPLE("SimpleJob", "简单类型job"),
	DATAFLOW("DataflowJob", "流式类型job"),
	SCRIPT("ScriptJob", "脚本类型job");
	
	private String type;
	
	private String desc;

}
