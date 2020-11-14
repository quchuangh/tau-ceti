package com.chuang.tauceti.tools.basic.collection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DoubleKeyMap<K1, K2, V> {

    private final Map<K1, Map<K2, V>> map = new ConcurrentHashMap<>();

    public V get(K1 k, K2 k2) {
        return map.computeIfAbsent(k, k1 -> new ConcurrentHashMap<>()).get(k2);
    }

    public boolean contains(K1 k, K2 k2) {
        return map.computeIfAbsent(k, k1 -> new ConcurrentHashMap<>()).containsKey(k2);
    }

    public V put(K1 k, K2 k2, V v) {
        return map.computeIfAbsent(k, k1 -> new ConcurrentHashMap<>()).put(k2, v);
    }

    public V getOrDefault(K1 k1, K2 k2, V defaultV) {
        return map
                .computeIfAbsent(k1, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(k2, k -> defaultV);
    }

    public V getOrDefault(K1 k1, K2 k2, BiFunction<K1, K2, V> creator) {
        return map
                .computeIfAbsent(k1, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(k2, k -> creator.apply(k1, k2));
    }

    public Map<K2, V> get(K1 k) {
        return map.computeIfAbsent(k, k1 -> new ConcurrentHashMap<>());
    }

    public void clear(K1 key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public int size0() {
        return map.size();
    }

    public int size1() {
        return map.values().stream().mapToInt(Map::size).sum();
    }

    public List<V> values() {
        return maps().stream().map(Map::values).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Optional<V> remove(K1 k1, K2 k2) {
        return Optional.ofNullable(get(k1).remove(k2));
    }

    public Set<K1> keys1() {
        return map.keySet();
    }

    public Set<K2> keys2(K1 k) {
        return map.get(k).keySet();
    }

    public Set<K2> allKeys() {
        return maps().stream().map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Collection<Map<K2, V>> maps() {
        return map.values();
    }
}
