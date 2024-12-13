package com.lcyy.aicloud.config;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: dlwlrma
 * @data 2024年11月08日 15:46
 * @Description: TODO:使用springboot自带的线程池执行多线程
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置参数：
        //1.线程池的核心线程数:为当前设备的cpu数+1；Runtime.getRuntime().availableProcessors(),为获取当前设备可用的cpu数量
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);

        //设置线程池的最大线程数,为cpu数量的2倍+1
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);

        //设置线程池的任务队列为100000
        executor.setQueueCapacity(100000);

        //设置线程池的拒绝策略,自定义的拒绝策略
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                //todo:1出现异常线程时，保存异常线程
                //todo:2.通知中心相关人员来查看异常情况
            }
        });

        //线程池的初始化
        executor.initialize();
        //返回线程池可以在其他地方通过注入的方式获取
        return executor;
    }
}
