package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.config.GenConfig;
import com.chuang.tauceti.generator.impl.ControllerGen;
import com.chuang.tauceti.tools.basic.StringKit;

public interface JavaGenerator extends Generator {


    default String pkg(GenConfig genConfig, TableInfo info) {
        String rootPkg = genConfig.getPackageCfg().getParent();
        String typePkg = "";
        GenType type = type();
        if(type.equals(GenType.CONTROLLER)) {
            typePkg = genConfig.getPackageCfg().getController();
        } else if(type.equals(GenType.SERVICE)) {
            typePkg = genConfig.getPackageCfg().getService();
        } else if(type.equals(GenType.MAPPER)) {
            typePkg = genConfig.getPackageCfg().getMapper();
        } else if(type.equals(GenType.SERVICE_IMPL)) {
            typePkg = genConfig.getPackageCfg().getServiceImpl();
        } else if(type.equals(GenType.ENTITY)) {
            typePkg = genConfig.getPackageCfg().getEntity();
        } else if(type.equals(GenType.MAPPER_XML)) {
            typePkg = genConfig.getPackageCfg().getXml();
        } else {
            typePkg = type().name();
        }
        return rootPkg + StringPool.DOT + typePkg;
    }


    /**
     * 文件输出路径
     */
    default String outputFile(GenConfig config, TableInfo info) {
        String pkg = pkg(config, info).replaceAll("\\.", "/");
        String entityName = INameConvert.processName(info.getName(), config);
        entityName = StringKit.firstCharToUpperCase(entityName);
        String name = String.format(config.getNameConvert().className(type()), entityName);
        return "/" + pkg + "/" + name + ".java";
    }
}
