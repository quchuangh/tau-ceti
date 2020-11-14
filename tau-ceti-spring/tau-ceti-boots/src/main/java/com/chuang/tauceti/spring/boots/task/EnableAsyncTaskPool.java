package com.chuang.tauceti.spring.boots.task;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(AsyncTaskPoolConfiguration.class)
public @interface EnableAsyncTaskPool {
}
