package com.chuang.urras.rowquery.handlers;

import java.util.Optional;
import java.util.function.Supplier;

public class DefaultValueGetter<T> implements ValueGetter<T> {

    private final Supplier<Optional<T>> getter;

    private volatile T tmp;

    public DefaultValueGetter(Supplier<Optional<T>> getter) {
        this.getter = getter;
    }

    @Override
    public void set(T obj) {
        this.tmp = obj;
    }

    @Override
    public void release() {
        this.tmp = null;
    }

    @Override
    public Optional<T> get() {
        final T finalTmp = tmp;
        if(null == finalTmp) {
            return getter.get();
        } else {
            return Optional.of(finalTmp);
        }
    }
}
