package com.chuang.urras.rowquery.handlers;

import java.util.Optional;

public class ThreadLocalValueGetter<T> implements ValueGetter<T> {
    private final ThreadLocal<T> objectThreadLocal = new ThreadLocal<>();
    @Override
    public Optional<T> get() {
        T obj = objectThreadLocal.get();
        return Optional.ofNullable(obj);
    }

    public void set(T obj) {
        this.objectThreadLocal.set(obj);
    }

    @Override
    public void release() {
        this.objectThreadLocal.set(null);
    }
}
