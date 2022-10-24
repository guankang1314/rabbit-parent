package com.qingtian.rabbit.producer.autoconfiguration.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * 	$BrokerMessageConfiguration 
 * 	帮我执行SQL脚本
 * 	帮我进行数据库表结构的创建
 * @author Alienware
 *
 */
@Configuration
public class BrokerMessageAutoConfiguration {

    private  DataSource rabbitProducerDataSource;
    
    @Value("classpath:rabbit-producer-message-schema.sql")
    private Resource schemaScript;

    public BrokerMessageAutoConfiguration(DataSource rabbitProducerDataSource) {
        this.rabbitProducerDataSource = rabbitProducerDataSource;
    }

    @Bean
    public DataSourceInitializer initDataSourceInitializer(DataSource rabbitProducerDataSource) {
    	System.err.println("--------------rabbitProducerDataSource-----------:" + rabbitProducerDataSource);
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(rabbitProducerDataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }
}
