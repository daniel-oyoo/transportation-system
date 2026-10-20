package com.daniel.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadConfig {

@Bean("requestExecutor")
public Executor requestExecutor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);//8 threads alive
    executor.setMaxPoolSize(20);//20 threads if q fulll
    executor.setQueueCapacity(100);//hold 100 re in queue
    executor.setThreadNamePrefix("req-worker-");//re-worker-1
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
}
   
}
