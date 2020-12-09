package com.chuang.tauceti.generator.initializer;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.Generator;
import com.chuang.tauceti.generator.config.GenConfig;

import java.util.Collections;
import java.util.Map;

public interface ContextInitializer {


    /**
     * 最早执行，发生在配置初始化时期
     * 初始化 Tauceti 的全局变量，该变量会保存在 cfg.global中。
     * 作用域：全局
     * @param config 配置对象
     * @return Tauceti 全局变量
     */
    default Map<String, Object> initGlobalMap(GenConfig config) {
        return Collections.emptyMap();
    }

    /**
     * 第二个执行，发生在遍历每个表格对象的时期。
     * 处理 mybatis plus 全局变量。
     * 作用域：全局（里面就是Mybatis的所有全局变量）
     * @param config 配置对象，包含生成代码的全部配置
     * @param mpGlobeMap mybatis plus 的全局变量
     * @return 处理后的 mybatis plus 全局变量
     */
    default Map<String, Object> prepareInitTableMap(GenConfig config, Map<String, Object> mpGlobeMap) {
        return mpGlobeMap;
    }

    /**
     * 第三个执行，发生在表格对所有生成器进行预处理时期（生成模板代码前）。
     * 生成表格作用域 变量, 该变量会保存在 cfg.table 中。
     * 作用域：表格
     * @param config 配置对象，包含生成代码的全部配置
     * @param tableInfo 表格信息
     * @return 表格作用域 变量
     */
    default Map<String, Object> initTableMap(GenConfig config, TableInfo tableInfo) {
        return Collections.emptyMap();
    }

    /**
     * 第四个执行，发生在生成器获取输出代码的文件地址时。
     * 生成代码生成器作用域 变量, 该变量会保存在 cfg.tableGen 中。
     * 作用域：表格
     * @param config 配置对象，包含生成代码的全部配置
     * @param table 表格信息
     * @return 生成器作用域 变量
     */
    default Map<String, Object> initTableGenMap(GenConfig config, TableInfo table, Generator gen) {
        return Collections.emptyMap();
    }

    /**
     * 第五个执行，发生在生成器获取输出代码的文件地址时。
     * 生成代码生成器作用域变量, 该变量会保存在 cfg.gen 中。
     * 作用域：Generator
     * @param config 配置对象，包含生成代码的全部配置
     * @param table 表格信息
     * @return 生成器作用域 变量
     */
    default Map<String, Object> initGenMap(GenConfig config, TableInfo table, Generator gen) {
        return Collections.emptyMap();
    }

}
