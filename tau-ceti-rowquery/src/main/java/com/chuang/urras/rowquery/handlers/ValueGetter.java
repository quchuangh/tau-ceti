package com.chuang.urras.rowquery.handlers;

import java.util.Optional;
import java.util.function.Supplier;

public interface ValueGetter<T> extends Supplier<Optional<T>> {

    void set(T obj);

    void release();

    default void temp(T tmp, Runnable run) {
        T old = this.get().orElse(null);
        set(tmp);
        try {
            run.run();
        } finally {
            set(old);
        }
    }

    default void tempEx(T tmp, RunnableWithEx run) throws Throwable {
        T old = this.get().orElse(null);
        set(tmp);
        try {
            run.run();
        } finally {
            set(old);
        }
    }

    default <R> R temp(T tmp, Supplier<R> run) {
        T old = this.get().orElse(null);
        set(tmp);
        try {
            return run.get();
        } finally {
            set(old);
        }

    }

    default <R> R tempEx(T tmp, SupplierWithEx<R> run) throws Throwable {
        T old = this.get().orElse(null);
        set(tmp);
        try {
            return run.get();
        } finally {
            set(old);
        }

    }

    interface RunnableWithEx {
        void run() throws Throwable;
    }

    interface SupplierWithEx<T> {
        T get() throws Throwable;
    }
}
