package com.chuang.tauceti.tools.third.google.guava;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

public class GroupGuavaLocks {
    private final Map<String, GuavaLocks> namedLockGroup = new ConcurrentHashMap<>();
    private final Function<String, Integer> lockGroupSizeMapping;

    public GroupGuavaLocks(Function<String, Integer> lockGroupSizeMapping) {
        this.lockGroupSizeMapping = lockGroupSizeMapping;
    }

    public GroupGuavaLocks() {
        this.lockGroupSizeMapping = s -> 100;
    }

    public Lock getLock(String groupName, Object key, Object... keys) {
        return namedLockGroup.computeIfAbsent(groupName, s -> new GuavaLocks(lockGroupSizeMapping.apply(s)))
                .getLock(key, keys);
    }
}
