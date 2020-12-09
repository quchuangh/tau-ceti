package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.config.GenConfig;
import com.chuang.tauceti.tools.basic.StringKit;

public interface JavaGenerator extends Generator {


    default String pkg(String rootPkg, TableInfo info) {
        return rootPkg + "." + type().name();
    }


    /**
     * 文件输出路径
     */
    default String outputFile(GenConfig config, TableInfo info) {
        String pkg = pkg(config.getRootPackage(), info).replaceAll("\\.", "/");
        String entityName = INameConvert.processName(info.getName(), config);
        entityName = StringKit.firstCharToUpperCase(entityName);
        String name = String.format(config.getNameConvert().className(type()), entityName);
        return "/" + pkg + "/" + name + ".java";
    }
}
