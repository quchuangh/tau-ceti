package com.chuang.tauceti.tools.third.google.guava;

import com.google.common.util.concurrent.Striped;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

/**
 * 依赖guava 并发库的锁工具。需要说明的是guava的锁工具不能保证不同的id拿到的锁一定不同。
 * 假设以用户名为锁id，不同的用户名可能会获取到相同的锁对象。另外锁池限制了1024个锁的最大数量。
 * 换句话说这个工具类最多保持1024个锁对象。
 * 如果要求不同id一定不能获取相同的锁，可以考虑使用 {@link com.chuang.tauceti.tools.basic.LocalCachedLock}
 */
@Deprecated
public class GuavaLocks {
    private final Striped<Lock> striped;

    public GuavaLocks(int maxSize) {
        striped = Striped.lazyWeakLock(maxSize);
    }

    public synchronized Lock getLock(Object o, Object... os) {
        if(os.length == 0) {
            return striped.get(o);
        } else {
            String v = Arrays.stream(os).map(Object::toString).reduce("", (s, s2) -> s + ":" + s2);
            return striped.get(o.toString() + ":" + v);
        }
    }
}
