package com.chuang.tauceti.generator.initializer;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.Generator;
import com.chuang.tauceti.generator.config.GenConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextInitializers implements ContextInitializer {

    private final List<ContextInitializer> list = new ArrayList<>();

    public void add(ContextInitializer initializer) {
        list.add(initializer);
    }

    @Override
    public Map<String, Object> initGlobalMap(GenConfig config) {
        Map<String, Object> map = new HashMap<>();
        list.forEach(initializer -> map.putAll(initializer.initGlobalMap(config)));
        return map;
    }

    @Override
    public Map<String, Object> prepareInitTableMap(GenConfig config, Map<String, Object> mpGlobeMap) {
        Map<String, Object> map = mpGlobeMap;
        for (ContextInitializer contextInitializer : list) {
            map = contextInitializer.prepareInitTableMap(config, map);
        }
        return map;
    }

    @Override
    public Map<String, Object> initTableMap(GenConfig config, TableInfo tableInfo) {
        Map<String, Object> map = new HashMap<>();
        list.forEach(initializer -> map.putAll(initializer.initTableMap(config, tableInfo)));
        return map;
    }

    @Override
    public Map<String, Object> initTableGenMap(GenConfig config, TableInfo tableInfo, Generator gen) {
        Map<String, Object> map = new HashMap<>();
        list.forEach(initializer -> map.putAll(initializer.initTableGenMap(config, tableInfo, gen)));
        return map;
    }

    @Override
    public Map<String, Object> initGenMap(GenConfig config, TableInfo table, Generator gen) {
        Map<String, Object> map = new HashMap<>();
        list.forEach(initializer -> map.putAll(initializer.initGenMap(config, table, gen)));
        return map;
    }
}
