package com.chuang.tauceti.support;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompletableFutureWrapper<T> extends CompletableFuture<T> {

    private Function<Boolean, Boolean> cancel;
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return cancel.apply(mayInterruptIfRunning);
    }

    public void setCancelHandler(Function<Boolean, Boolean> cancel) {
        this.cancel = cancel;
    }
}
