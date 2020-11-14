package com.chuang.tauceti.tools.third.redis;


import com.chuang.tauceti.tools.basic.cache.Cache;
import com.chuang.tauceti.tools.basic.cache.ExpireCache;

import java.util.*;

public class RedisCachedManager {
    private final RedisHCached cached;

    private final Map<String, Cache<String, ?>> caches = new HashMap<>();
    private final Map<String, ExpireCache<?>> expire_caches = new HashMap<>();

    public RedisCachedManager(RedisHCached cached) {
        this.cached = cached;
    }

    protected Cache<String, ?> createCache(String cacheName) {
        if(caches.containsKey(cacheName)) {
            return caches.get(cacheName);
        }

        RedisCache<String, ?> c = new RedisCache<>(cacheName, cached);
        caches.put(cacheName, c);
        return c;
    }

    protected ExpireCache<?> createExpireCache(String cacheName) {
        if(expire_caches.containsKey(cacheName)) {
            return expire_caches.get(cacheName);
        }
        RedisExpireCache<?> c = new RedisExpireCache<>(cacheName, cached);
        expire_caches.put(cacheName, c);
        return c;
    }

    public Set<String> caches() {
        return Collections.unmodifiableSet(caches.keySet());
    }

    public Set<String> expireCaches() {
        return Collections.unmodifiableSet(expire_caches.keySet());
    }
    public Set<String> cachesAll() {
        Set<String> keyAll = new HashSet<>();
        keyAll.addAll(caches.keySet());
        keyAll.addAll(expire_caches.keySet());
        return keyAll;
    }
}
