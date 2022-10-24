package com.qingtian.rabbit.producer.autoconfiguration.database;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:rabbit-producer-message.properties"})
public class RabbitProducerDataSourceAutoConfiguration {

  private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RabbitProducerDataSourceAutoConfiguration.class);

  @Value("${rabbit.producer.druid.type}")
  private Class<? extends DataSource> dataSourceType;

  @Bean(name = "rabbitProducerDataSource")
  @Primary
  @ConfigurationProperties(prefix = "rabbit.producer.druid.jdbc")
  @ConditionalOnMissingBean(name = "rabbitProducerDataSource")
  public DataSource rabbitProducerDataSource() throws SQLException {
    DataSource rabbitProducerDataSource = DataSourceBuilder.create().type(dataSourceType).build();
    LOGGER.info("============= rabbitProducerDataSource : {} ================", rabbitProducerDataSource);
    return rabbitProducerDataSource;
  }

  public DataSourceProperties primaryDataSourceProperties() {
    return new DataSourceProperties();
  }

  public DataSource primaryDataSource() {
    return primaryDataSourceProperties().initializeDataSourceBuilder().build();
  }

}
