package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.config.GenConfig;

import javax.annotation.Nullable;
import java.util.Map;

public class MvnWrapper implements Generator {

    private final Generator gen;
    private final String prefix;

    public MvnWrapper(Generator gen) {
        this(gen, null);
    }
    public MvnWrapper(Generator gen, @Nullable String prefix) {
        this.gen = gen;
        this.prefix = prefix;
    }

    public Generator getImpl() {
        return gen;
    };

    @Override
    public String template() {
        return this.gen.template();
    }

    @Override
    public GenType type() {
        return this.gen.type();
    }



    @Override
    public String outputFile(GenConfig config, TableInfo info) {
        if(gen instanceof JavaGenerator) {
            return (null == prefix ? "" : prefix) + "/src/main/java" + this.gen.outputFile(config, info);
        } else {
            return (null == prefix ? "" : prefix) + "/src/main/resources" + this.gen.outputFile(config, info);
        }
    }


    public int compareTo(Generator other) {
        return this.gen.compareTo(other);
    }

    public Map<String, Object> prepareInitTableMap(GenConfig config, Map<String, Object> mpGlobeMap) {
        return gen.prepareInitTableMap(config, mpGlobeMap);
    }

    public Map<String, Object> initGlobalMap(GenConfig config) {
        return gen.initGlobalMap(config);
    }

    public Map<String, Object> initTableMap(GenConfig config, TableInfo tableInfo) {
        return gen.initTableMap(config, tableInfo);
    }

    public Map<String, Object> initGenMap(GenConfig config, TableInfo table, Generator gen) {
        return gen.initGenMap(config, table, gen);
    }

    public Map<String, Object> initContext(GenConfig config, TableInfo info) {
        return gen.initContext(config, info);
    }




}
