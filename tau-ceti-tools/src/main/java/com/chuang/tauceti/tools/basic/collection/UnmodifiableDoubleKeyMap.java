package com.chuang.tauceti.tools.basic.collection;

import java.util.*;
import java.util.function.BiFunction;

public class UnmodifiableDoubleKeyMap<K1, K2, V> {

    private final DoubleKeyMap<K1, K2, V> map;

    UnmodifiableDoubleKeyMap(DoubleKeyMap<K1, K2, V> map) {
        this.map = map;
    }

    public V get(K1 k, K2 k2) {
        return map.get(k, k2);
    }

    public boolean contains(K1 k, K2 k2) {
        return map.contains(k, k2);
    }

    public V getOrDefault(K1 k1, K2 k2, V defaultV) {
        return map.getOrDefault(k1, k2, defaultV);
    }

    public V getOrDefault(K1 k1, K2 k2, BiFunction<K1, K2, V> creator) {
        return map.getOrDefault(k1, k2, creator);
    }

    public Map<K2, V> get(K1 k) {
        return Collections.unmodifiableMap(map.get(k));
    }


    public int size0() {
        return map.size0();
    }

    public int size1() {
        return map.size1();
    }

    public List<V> values() {
        return Collections.unmodifiableList(map.values());
    }


    public Set<K1> keys1() {
        return Collections.unmodifiableSet(map.keys1());
    }

    public Set<K2> keys2(K1 k) {
        return Collections.unmodifiableSet(map.keys2(k));
    }

    public Set<K2> allKeys() {
        return Collections.unmodifiableSet(map.allKeys());
    }

    public Collection<Map<K2, V>> maps() {
        return Collections.unmodifiableCollection(map.maps());
    }
}
