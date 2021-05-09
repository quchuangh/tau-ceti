package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.config.GenConfig;
import com.chuang.tauceti.generator.initializer.ContextInitializer;
import com.chuang.tauceti.support.exception.SystemException;

import java.util.Collections;
import java.util.Map;

public interface Generator extends Comparable<Generator>, ContextInitializer {

    /**
     * 不要实现它，没意义， 它与 {@link #initTableMap(GenConfig, TableInfo)} 是等价的，因此系统不会调用它。
     */
    @Override
    @Deprecated
    default Map<String, Object> initTableGenMap(GenConfig config, TableInfo table, Generator gen) {
        throw new SystemException("请不要重写Generator的initTableGenMap，也不要调用它，没有意义。如果需要，请调用 initTableMap");
    }

    @Override
    default Map<String, Object> initTableMap(GenConfig config, TableInfo tableInfo) {
        return Collections.emptyMap();
    }

    /**
     * 请不要重写它，如果生成器要初始化生成器作用域的变量，请重写 {@link #initContext(GenConfig, TableInfo)}
     */
    @Override
    default Map<String, Object> initGenMap(GenConfig config, TableInfo table, Generator gen) {
        if(this == gen) {
            return initContext(config, table);
        }
        throw new SystemException("SDK 异常。请联系开发者解决此问题。" + gen.getClass().getName() + "被委派给了" + this.getClass().getName());
    }

    default Map<String, Object> initContext(GenConfig config, TableInfo info) {
        return Collections.emptyMap();
    }
    /**
     * 生成器排序,默认都为0（不排序）
     * 如果你有2个生成器实现，且希望他们的执行存在先后，可以通过重写这个方法来实现。
     */
    default int compareTo(Generator other) {
        return this.type().order() - other.type().order();
    }

    /**
     * 模板所在路径
     */
    String template();

    GenType type();

    /**
     * 文件输出路径
     */
    String outputFile(GenConfig config, TableInfo info);

    default String tableNameWithoutPrefix(GenConfig config, TableInfo info) {
        String[] prefix = config.getStrategy().getTablePrefix();
        String tableName = info.getName();
        if(prefix != null && prefix.length > 0) {
            for(String pre: prefix) {
                if(tableName.startsWith(pre)) {
                    tableName = tableName.substring(pre.length());
                }
            }
        }

        return tableName;
    }

}
