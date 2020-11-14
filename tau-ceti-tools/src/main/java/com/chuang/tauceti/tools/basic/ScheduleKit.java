package com.chuang.tauceti.tools.basic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleKit {
    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void schedule(Runnable run, int time, TimeUnit timeUnit) {
        executor.schedule(run, time, timeUnit);
    }
}
