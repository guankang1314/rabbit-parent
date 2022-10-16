package com.qingtian.rabbit.producer.autoconfiguration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-16 21:49
 */
@Configuration
@ComponentScan({"com.qingtian.rabbit.producer.*"})
public class RabbitAutoConfiguration {
}
