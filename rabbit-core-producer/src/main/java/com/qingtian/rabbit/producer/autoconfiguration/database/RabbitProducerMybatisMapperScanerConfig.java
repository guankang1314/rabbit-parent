
package com.qingtian.rabbit.producer.autoconfiguration.database;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(RabbitProducerDataSourceAutoConfiguration.class)
public class RabbitProducerMybatisMapperScanerConfig {

  @Bean(name = "rabbitProducerMapperScannerConfigurer")
  public MapperScannerConfigurer rabbitProducerMapperScannerConfigurer() {
    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    mapperScannerConfigurer.setSqlSessionFactoryBeanName("rabbitProducerSqlSessionFactory");
    mapperScannerConfigurer.setBasePackage("com.qingtian.rabbit.producer.mapper");
    return mapperScannerConfigurer;
  }

}
