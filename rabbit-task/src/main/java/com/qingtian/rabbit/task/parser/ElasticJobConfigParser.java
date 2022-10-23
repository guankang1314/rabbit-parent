package com.qingtian.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.qingtian.rabbit.task.annotation.ElasticJobConfig;
import com.qingtian.rabbit.task.autoconfiguration.JobZookeeperProperties;
import com.qingtian.rabbit.task.enums.ElasticJobTypeEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author Guank
 * @version 1.0
 * @description: ElasticJob配置属性解析器
 * @date 2022-10-23 10:59
 */
@Slf4j
public class ElasticJobConfigParser implements ApplicationListener<ApplicationReadyEvent> {

  private JobZookeeperProperties jobZookeeperProperties;

  private ZookeeperRegistryCenter zookeeperRegistryCenter;

  private final List<JobTypeNamesCustomizer> jobTypeNamesCustomizers;

  private final List<JobCoreConfigCustomizer> jobCoreConfigCustomizers;

  private final List<JobConfigCustomizer> jobConfigCustomizers;
  private final List<SpringJobSchedulerCustomizer> springJobSchedulerCustomizers;

  public ElasticJobConfigParser(JobZookeeperProperties jobZookeeperProperties,
      ZookeeperRegistryCenter zookeeperRegistryCenter,
      ObjectProvider<List<JobTypeNamesCustomizer>> jobTypeNamesCustomizers,
      ObjectProvider<List<JobCoreConfigCustomizer>> jobCoreConfigCustomizers,
      ObjectProvider<List<JobConfigCustomizer>> jobConfigCustomizers,
      ObjectProvider<List<SpringJobSchedulerCustomizer>> springJobSchedulerCustomizers) {
    this.jobZookeeperProperties = jobZookeeperProperties;
    this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    this.jobTypeNamesCustomizers = jobTypeNamesCustomizers.getIfAvailable();
    this.jobCoreConfigCustomizers = jobCoreConfigCustomizers.getIfAvailable();
    this.jobConfigCustomizers = jobConfigCustomizers.getIfAvailable();
    this.springJobSchedulerCustomizers = springJobSchedulerCustomizers.getIfAvailable();
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    try {
      ApplicationContext applicationContext = event.getApplicationContext();
      // 拿出带有ElasticJobConfig注解的bean
      Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
      AtomicInteger count = new AtomicInteger(0);
      for (Iterator<?> it = beanMap.values().iterator(); it.hasNext(); ) {
        Object confBean = it.next();
        // 获取对应的类
        Class<?> clazz = confBean.getClass();
        if (clazz.getName().indexOf("$") > 0) {
          // 如果该bean有父类,获取真正的类
          String className = clazz.getName();
          clazz = Class.forName(className.substring(0, className.indexOf("$")));
        }
        // 获取实现的接口类
        List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
        // 作业类型集合
        List<String> jobTypeNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(interfaces)) {
          for (Class<?> anInterface : interfaces) {
            String interfaceSimpleName = anInterface.getSimpleName();
            if (Stream.of(ElasticJobTypeEnum.values())
                .anyMatch(typeEnum -> typeEnum.getType().equals(interfaceSimpleName))) {
              // 如果该类实现了规定的作业类型接口,加入作业类型集合中
              jobTypeNames.add(interfaceSimpleName);
            }
          }
        }
        applyJobTypeNamesCustomizers(jobTypeNames);
        // 获取配置项
        ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
        String jobClass = clazz.getName();
        String jobName = this.jobZookeeperProperties.getNamespace() + "." + conf.name() + "." + UUID.randomUUID();
        String cron = conf.cron();
        String shardingItemParameters = conf.shardingItemParameters();
        String description = conf.description();
        String jobParameter = conf.jobParameter();
        String jobExceptionHandler = conf.jobExceptionHandler();
        String executorServiceHandler = conf.executorServiceHandler();

        String jobShardingStrategyClass = conf.jobShardingStrategyClass();
        String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
        String scriptCommandLine = conf.scriptCommandLine();

        boolean failover = conf.failover();
        boolean misfire = conf.misfire();
        boolean overwrite = conf.overwrite();
        boolean disabled = conf.disabled();
        boolean monitorExecution = conf.monitorExecution();
        boolean streamingProcess = conf.streamingProcess();

        int shardingTotalCount = conf.shardingTotalCount();
        int monitorPort = conf.monitorPort();
        int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
        int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();

        //创建es-job的相关configuration
        JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
            .shardingItemParameters(shardingItemParameters)
            .jobParameter(jobParameter)
            .description(description)
            .failover(failover)
            .misfire(misfire)
            .jobProperties(JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
            .jobProperties(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
            .build();
        applyJobCoreConfigCustomizers(coreConfig);
        //创建任务配置
        if (!CollectionUtils.isEmpty(jobTypeNames)) {
          Map<JobTypeConfiguration, String> typeConfigs = new HashMap<>();
          for (String jobTypeName : jobTypeNames) {
            JobTypeConfiguration typeConfig = null;
            if (ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)) {
              typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);
            }

            if (ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)) {
              typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, streamingProcess);
            }

            if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
              typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);
            }
            if (null != typeConfig) {
              typeConfigs.put(typeConfig, jobTypeName);
            }
          }

          if (!CollectionUtils.isEmpty(typeConfigs)) {
            for (JobTypeConfiguration typeConfig : typeConfigs.keySet()) {
              // 创建LiteJobConfiguration
              LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(typeConfig)
                  .overwrite(overwrite)
                  .disabled(disabled)
                  .monitorPort(monitorPort)
                  .monitorExecution(monitorExecution)
                  .maxTimeDiffSeconds(maxTimeDiffSeconds)
                  .jobShardingStrategyClass(jobShardingStrategyClass)
                  .reconcileIntervalMinutes(reconcileIntervalMinutes)
                  .build();

              applyJobConfigCustomizers(jobConfig);

              BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(
                  SpringJobScheduler.class);
              factory.setInitMethodName("init");
              factory.setScope("prototype");

              //	1.添加bean构造参数，相当于添加自己的真实的任务实现类
              if (!ElasticJobTypeEnum.SCRIPT.getType().equals(typeConfigs.get(typeConfig))) {
                factory.addConstructorArgValue(confBean);
              }
              //	2.添加注册中心
              factory.addConstructorArgValue(this.zookeeperRegistryCenter);
              //	3.添加LiteJobConfiguration
              factory.addConstructorArgValue(jobConfig);

              //	4.如果有eventTraceRdbDataSource 则也进行添加
              if (StringUtils.hasText(eventTraceRdbDataSource)) {
                BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(
                    JobEventRdbConfiguration.class);
                rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
                factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
              }

              //  5.添加监听
              List<?> elasticJobListeners = getTargetElasticJobListeners(conf);
              factory.addConstructorArgValue(elasticJobListeners);

              applySpringJobSchedulerCustomizers(factory);
              //把SpringJobScheduler注入到Spring容器中
              DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

              String registerBeanName = jobName + "SpringJobScheduler";
              defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
              SpringJobScheduler scheduler = (SpringJobScheduler) applicationContext.getBean(registerBeanName);
              scheduler.init();
              log.info("启动elastic-job作业: jobName : [{}]", jobName);
              count.incrementAndGet();
            }
          }
        }
      }
      log.info("共计启动elastic-job作业数量为: {} 个", count);
    } catch (Exception e) {
      log.error("elastic-job 启动异常, 系统强制退出", e);
      System.exit(1);
    }
  }

  private void applySpringJobSchedulerCustomizers(BeanDefinitionBuilder factory) {
    if (!CollectionUtils.isEmpty(springJobSchedulerCustomizers)) {
      springJobSchedulerCustomizers.forEach(customizer -> customizer.customize(factory));
    }
  }

  private void applyJobConfigCustomizers(LiteJobConfiguration jobConfig) {
    if (!CollectionUtils.isEmpty(jobConfigCustomizers)) {
      jobConfigCustomizers.forEach(customizer -> customizer.customize(jobConfig));
    }
  }

  private void applyJobCoreConfigCustomizers(JobCoreConfiguration coreConfig) {
    if (!CollectionUtils.isEmpty(jobCoreConfigCustomizers)) {
      jobCoreConfigCustomizers.forEach(customizer -> customizer.customize(coreConfig));
    }
  }

  private void applyJobTypeNamesCustomizers(List<String> jobTypeNames) {
    if (!CollectionUtils.isEmpty(jobTypeNamesCustomizers)) {
      for (JobTypeNamesCustomizer customizer : jobTypeNamesCustomizers) {
        customizer.customize(jobTypeNames);
      }
    }
  }

  private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf) {
    List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
    String listeners = conf.listener();
    if (StringUtils.hasText(listeners)) {
      BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
      factory.setScope("prototype");
      result.add(factory.getBeanDefinition());
    }

    String distributedListeners = conf.distributedListener();
    long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
    long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

    if (StringUtils.hasText(distributedListeners)) {
      BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
      factory.setScope("prototype");
      factory.addConstructorArgValue(startedTimeoutMilliseconds);
      factory.addConstructorArgValue(completedTimeoutMilliseconds);
      result.add(factory.getBeanDefinition());
    }
    return result;
  }
}
