package com.chuang.tauceti.tools.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本地锁，通过该方法获取的锁在unlock时会判断引用数，如果等于0 锁对象将会从内存中释放。
 * 这样做的目的是为了防止过多的锁对象被缓存。需要注意的是这个工具类获得的锁在使用不当时会引发其他的并发症。
 * 如果对锁的使用没有信心可以考虑使用
 * {@link com.chuang.tauceti.tools.third.google.guava.GroupGuavaLocks} 的实现。
 * 该实现需要引入 guava
 */
@Deprecated
public class LocalCachedLock {

    private final Object id;
    private final Lock lock;
    private final AtomicInteger count = new AtomicInteger(0);

    private LocalCachedLock(Object id, Lock lock) {
        this.id = id;
        this.lock = lock;
    }


    public void lock() {
        add();
        this.lock.lock();
    }


    public void lockInterruptibility() throws InterruptedException {
        try {
            add();
            this.lock.lockInterruptibly();
        } catch (InterruptedException e) {
            sub();
            throw e;
        }
    }


    public boolean tryLock() {
        boolean ok = this.lock.tryLock();
        if(ok) {
            add();
        }
        return ok;
    }


    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        boolean ok = lock.tryLock(time, unit);
        if(ok) {
            add();
        }
        return ok;
    }


    public void unlock() {
        this.lock.unlock();
        if(sub() == 0) {
            LocalCachedLock.remove(id);
        }
    }

    private int add() {
        return count.incrementAndGet();
    }
    private int sub() {
        return count.decrementAndGet();
    }

    // lock map
    // @GuardedBy("LocalLock.class")
    private final static Map<Object, LocalCachedLock> REF_LOCKS_MAP = new HashMap<>();
    // remove a lock by reference (Thread Safe)
    // @GuardedBy("this")
    private static synchronized void remove(Object id) {
        REF_LOCKS_MAP.remove(id);
        System.out.println("id remove -> " + id);
    }
    public static synchronized LocalCachedLock getLock(Object id) {
        LocalCachedLock lock;
        if(REF_LOCKS_MAP.containsKey(id)) {
            lock = REF_LOCKS_MAP.get(id);
        } else {
            lock = new LocalCachedLock(id, new ReentrantLock());
            REF_LOCKS_MAP.put(id, lock);
        }
        return lock;
    }
}
