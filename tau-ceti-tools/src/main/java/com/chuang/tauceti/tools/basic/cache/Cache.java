package com.chuang.tauceti.tools.basic.cache;


import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface Cache<K, V> {
    Optional<V> get(K key);

    Boolean put(K key, V value) ;

    Boolean remove(K key)  ;

    Boolean clear() ;

    /**
     * size可能是通过keys().size() 来获取的。
     */
    int size();

    Set<K> keys() ;

    Collection<V> values();
}
