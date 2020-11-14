package com.chuang.tauceti.spring.boots.task;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.task.thread-pool")
public class ThreadPoolProperties {

    private int workQueueCapacity = 0;

    private int coreSize = Runtime.getRuntime().availableProcessors();

    private int maximumSize = Runtime.getRuntime().availableProcessors() * 10;

    private int keepAliveTime = 60;

    private String threadNamePrefix = "task-pool";

    private String schedulerNamePrefix = "task-scheduler";

    private int schedulerPoolSize = 2;

    public int getWorkQueueCapacity() {
        return workQueueCapacity;
    }

    public void setWorkQueueCapacity(int workQueueCapacity) {
        this.workQueueCapacity = workQueueCapacity;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public String getSchedulerNamePrefix() {
        return schedulerNamePrefix;
    }

    public void setSchedulerNamePrefix(String schedulerNamePrefix) {
        this.schedulerNamePrefix = schedulerNamePrefix;
    }

    public int getSchedulerPoolSize() {
        return schedulerPoolSize;
    }

    public void setSchedulerPoolSize(int schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }
}
