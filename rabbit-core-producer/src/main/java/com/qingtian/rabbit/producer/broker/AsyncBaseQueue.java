package com.qingtian.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2022-10-16 22:15
 */
@Slf4j
public class AsyncBaseQueue {

  private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();

  private static final int QUEUE_SIZE = 10000;

  private static ExecutorService senderAsync =
      new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE * 2, 60,
          TimeUnit.SECONDS, new ArrayBlockingQueue<>(QUEUE_SIZE),
          r -> {
            Thread t = new Thread(r);
            t.setName("rabbitmq_client_async_sender");
            return t;
          },
          (r, executor) -> log.info("async sender is error rejected, runnable : [{}], executor : [{}]", r, executor));


  public static void submit(Runnable runnable) {
    senderAsync.submit(runnable);
  }
}
