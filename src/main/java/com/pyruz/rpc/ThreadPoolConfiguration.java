package com.pyruz.rpc;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


public class ThreadPoolConfiguration {

    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(5);
        pool.setMaxPoolSize(5);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.initialize();
        return pool;
    }
}
