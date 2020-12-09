package com.chuang.tauceti.generator.initializer;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.Generator;
import com.chuang.tauceti.generator.INameConvert;
import com.chuang.tauceti.generator.JavaGenerator;
import com.chuang.tauceti.generator.MvnWrapper;
import com.chuang.tauceti.generator.config.GenConfig;
import com.chuang.tauceti.tools.basic.StringKit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultContextInitializer implements ContextInitializer {


    @Override
    public Map<String, Object> initTableGenMap(GenConfig config, TableInfo table, Generator gen) {
        Map<String, Object> map = new HashMap<>();
        java(gen).ifPresent(javaGen -> {
            String pkg = javaGen.pkg(config.getRootPackage(), table);
            String entityName = INameConvert.processName(table.getName(), config);
            entityName = StringKit.firstCharToUpperCase(entityName);
            String name = String.format(config.getNameConvert().className(gen.type()), entityName);
            map.put("package_" + gen.type().name(), pkg);
            map.put("entity_" + gen.type().name(), name);
        });
        return map;
    }

    private Optional<JavaGenerator> java(Generator generator) {
        if(generator instanceof MvnWrapper) {
            generator =  ((MvnWrapper) generator).getImpl();
        }
        if(generator instanceof JavaGenerator) {
            return Optional.of((JavaGenerator) generator);
        }
        return Optional.empty();

    }
}
