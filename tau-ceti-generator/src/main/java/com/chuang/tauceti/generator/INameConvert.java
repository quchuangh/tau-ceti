package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.chuang.tauceti.generator.config.GenConfig;

public interface INameConvert {

    /**
     * 返回名称中可以包含 %s， %s 将自动替换成（表名 转 类名）
     */
    String className(GenType type);

    static String processName(String name, GenConfig config) {
        NamingStrategy strategy = config.getStrategy().getNaming();
        String[] prefix = config.getStrategy().getTablePrefix();
        boolean removePrefix = false;
        if (prefix != null && prefix.length != 0) {
            removePrefix = true;
        }
        String propertyName;
        if (removePrefix) {
            if (strategy == NamingStrategy.underline_to_camel) {
                // 删除前缀、下划线转驼峰
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                // 删除前缀
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            // 下划线转驼峰
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
            // 不处理
            propertyName = name;
        }
        return propertyName;
    }
}
