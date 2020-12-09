package com.chuang.tauceti.generator.initializer;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.INameConvert;
import com.chuang.tauceti.generator.config.GenConfig;

import java.util.Collections;
import java.util.Map;

/**
 * 这个初始化器主要是添加一些 TC 认为比较通用的变量。其中包括
 * 1，将 MP Hyphen 风格的RequestMapping地址会包含Entity的后缀，比如 UserInfoEntity 生成的地址为 /user-info-entity.
 *      TC 将其改为 /user-info
 * 2，添加一些常用的参数
 */
public class DefaultContextInitializer implements ContextInitializer {

    private Map<String, Object> mpGlobeMap;

    @Override
    public Map<String, Object> prepareInitTableMap(GenConfig config, Map<String, Object> mpGlobeMap) {
        this.mpGlobeMap = mpGlobeMap;
        return mpGlobeMap;
    }

    @Override
    public Map<String, Object> initTableMap(GenConfig config, TableInfo tableInfo) {
        if((boolean) mpGlobeMap.getOrDefault("controllerMappingHyphenStyle", false)) {
            String entityName = INameConvert.processName(tableInfo.getName(), config);
            mpGlobeMap.put("controllerMappingHyphen", StringUtils.camelToHyphen(entityName));
        }
        return Collections.emptyMap();
    }

//    @Override
//    public Map<String, Object> initTableGenMap(GenConfig config, TableInfo table, Generator gen) {
//        Map<String, Object> map = new HashMap<>();
//        java(gen).ifPresent(javaGen -> {
//            String pkg = javaGen.pkg(config.getRootPackage(), table);
//            String entityName = INameConvert.processName(table.getName(), config);
//            entityName = StringKit.firstCharToUpperCase(entityName);
//            String name = String.format(config.getNameConvert().className(gen.type()), entityName);
//            map.put("package_" + gen.type().name(), pkg);
//            map.put("entity_" + gen.type().name(), name);
//
//            if(javaGen.type().equals(GenType.ENTITY)) {
//                mpGlobeMap.put("entity", name);
//            }
//        });
//        return map;
//    }


//    private Optional<JavaGenerator> java(Generator generator) {
//        if(generator instanceof MvnWrapper) {
//            generator =  ((MvnWrapper) generator).getImpl();
//        }
//        if(generator instanceof JavaGenerator) {
//            return Optional.of((JavaGenerator) generator);
//        }
//        return Optional.empty();
//
//    }
}
