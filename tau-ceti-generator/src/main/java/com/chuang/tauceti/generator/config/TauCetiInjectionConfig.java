package com.chuang.tauceti.generator.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.chuang.tauceti.generator.initializer.ContextInitializer;
import com.chuang.tauceti.generator.Generator;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TauCetiInjectionConfig extends InjectionConfig {

    private final GenConfig genConfig;

    /**
     * 自定义返回配置 Map 对象
     */
    private final Map<String, Object> globalMap = new HashMap<>();
    private final Map<String, Object> tableMap = new HashMap<>();
    private final Map<String, Object> genMap = new HashMap<>();

    private final ContextInitializer initializer;

    private final boolean debug;
    private Map<String, Object> currentMap = new HashMap<>();

    public TauCetiInjectionConfig(boolean debug,
                                  GenConfig genConfig,
                                  ContextInitializer initializer) {
        this.debug = debug;
        this.genConfig = genConfig;
        this.initializer = initializer;

        // 添加生成器
        List<FileOutConfig> focList = new ArrayList<>();
        for (Generator generator : genConfig.getGenerators()) {
            focList.add(new FileOutConfig(generator.template()) {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    addEnumType(tableInfo);
                    return TauCetiInjectionConfig.this.onOutputFile(tableInfo, generator);
                }
            });
        }
        this.setFileOutConfigList(focList);
    }



    /**
     * 将 TableInfo 含枚举的字段改为枚举类型
     */
    private void addEnumType(TableInfo tableInfo) {
        Map<String, Class<? extends Enum<?>>> ems = genConfig.getEnums().get(tableInfo.getName());
        if(CollectionKit.isNotEmpty(ems)) {
            tableInfo.getFields().forEach(tableField -> {
                if(ems.containsKey(tableField.getName())) {
                    Class<? extends Enum<?>> clazz = ems.get(tableField.getName());

                    tableField.setColumnType(new IColumnType() {
                        public String getType() {
                            return clazz.getSimpleName();
                        }
                        public String getPkg() {
                            return clazz.getPackage().getName();
                        }
                    });
                    tableInfo.setImportPackages(clazz.getName());
                }
            });
        }
    }


    /**
     * 1 最早调用，仅一次
     * 注入自定义 Map 对象，针对所有表的全局参数
     */
    @Override
    public void initMap() {
        initGlobalMap();
    }

    public void initGlobalMap() {
        this.globalMap.putAll(initializer.initGlobalMap(genConfig));
        genConfig.getGenerators().forEach(generator -> globalMap.putAll(generator.initGlobalMap(genConfig)));
    }

    /**
     * 2 第二步处理，每个tableInfo调用一次
     * 模板待渲染 Object Map 预处理<br>
     * com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine
     * 方法： getObjectMap 结果处理
     */
    @Override
    public Map<String, Object> prepareObjectMap(Map<String, Object> objectMap) {
        Map<String, Object> map = this.initializer.prepareInitTableMap(this.genConfig, objectMap);
        for (Generator generator : genConfig.getGenerators()) {
            map = generator.prepareInitTableMap(genConfig, map);
        }
        currentMap = map;
        return map;
    }


    /**
     * 3 最后调用，一个tableinfo调用一次
     * 依据表相关信息，从三方获取到需要元数据，处理方法环境里面
     *
     */
    @Override
    public void initTableMap(TableInfo tableInfo) {
        // 每张表初始化前都清理之前表和gen的map
        this.tableMap.clear();
        this.genMap.clear();

        // 子类重写注入表对应补充信息
        this.tableMap.putAll(this.initializer.initTableMap(genConfig, tableInfo));
        genConfig.getGenerators().forEach(generator -> {
            this.tableMap.putAll(this.initializer.initTableGenMap(genConfig, tableInfo, generator));
            this.tableMap.putAll(generator.initTableMap(genConfig, tableInfo));
        });
    }



    public void initGenMap(TableInfo table, Generator gen) {
        this.genMap.clear();
        this.genMap.putAll(this.initializer.initGenMap(genConfig, table, gen));
        this.genMap.putAll(gen.initContext(genConfig, table));
    }

    protected String onOutputFile(TableInfo tableInfo, Generator generator) {
        initGenMap(tableInfo, generator);
        if(debug) {
            JSONObject json = new JSONObject(currentMap);
            JSONObject json1 = new JSONObject(getMap());
            log.debug("准备输出模板:{} -> {}相关变量如下: \r\nall: {}\r\ncfg: {}",
                    tableInfo.getName(),
                    generator.type().name(),
                    json.toJSONString(),
                    json1.toJSONString() );
        }
        return genConfig.getGlobal().getOutputDir() + "/" + generator.outputFile(genConfig, tableInfo);
    }


    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("global", globalMap);
        map.put("table", tableMap);
        map.put("gen", genMap);
        return map;
    }

}
