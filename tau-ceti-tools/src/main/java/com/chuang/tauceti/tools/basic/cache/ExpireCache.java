package com.chuang.tauceti.tools.basic.cache;


import java.util.Collection;
import java.util.Optional;

public interface ExpireCache<V> extends Cache<String, V> {

    V put(String key, V value, long expireMS) throws Exception ;

    Collection<V> getByPattern(String pattern);


    Long removeByPattern(String pattern);
}
