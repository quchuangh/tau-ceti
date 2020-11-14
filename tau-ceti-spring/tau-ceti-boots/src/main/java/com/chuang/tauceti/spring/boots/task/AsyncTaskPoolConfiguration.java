package com.chuang.tauceti.spring.boots.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class AsyncTaskPoolConfiguration implements AsyncConfigurer, SchedulingConfigurer {

    @Resource private ThreadPoolProperties properties;
    private final Logger log = LoggerFactory.getLogger(AsyncTaskPoolConfiguration.class);

    /**
     * 任务提交失败策略，当线程都在工作中，且队列已满，则任务提交失败。
     * 这里使用DiscardOld策略，当任务提交失败时，移除队列最前面的任务
     * 这里发现EnableScheduling实际需要的是TaskScheduler。该线程池无法满足需要。
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setThreadNamePrefix(properties.getThreadNamePrefix());
        pool.setCorePoolSize(properties.getCoreSize());
        pool.setMaxPoolSize(properties.getMaximumSize());
        pool.setQueueCapacity(properties.getWorkQueueCapacity());
        pool.setKeepAliveSeconds(properties.getKeepAliveTime());
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy());
        pool.initialize();
        return pool;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable throwable, Method method, Object... objects) ->
                log.error("异步任务错误", throwable);
    }

    /**
     * 在这里初始化定时任务的线程。
     * 定时任务需要的是 TaskScheduler ，如果没有，则以 DEBUG 方式报错，并寻找ScheduledExecutorService。
     * 如果仍然没有，则再次以 DEBUG     方式报错。然后自行调用Executors.newSingleThreadScheduledExecutor();创建一个
     * 单线程的定时任务处理器。这里我们来创建，方便控制。而且和上面的异步线程池分离开。
     * 避免异步线程池任务满了之后，定时任务无法及时处理。
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler pool = new ThreadPoolTaskScheduler();
        pool.setThreadNamePrefix(properties.getSchedulerNamePrefix());
        pool.setPoolSize(properties.getSchedulerPoolSize());
        pool.initialize();
        taskRegistrar.setTaskScheduler(pool);
    }
}
