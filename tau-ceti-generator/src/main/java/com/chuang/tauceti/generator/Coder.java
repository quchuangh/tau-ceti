package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.chuang.tauceti.generator.config.GenConfig;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import com.chuang.tauceti.tools.basic.reflect.ClassSearch;

import java.util.*;
import java.util.stream.Collectors;

public class Coder {

    public void gen(GenConfig config) {

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        mpg.setGlobalConfig(config.getGlobal());
        mpg.setDataSource(config.getDataSource());
        mpg.setStrategy(config.getStrategy());
        mpg.setTemplate(config.getTemplate());
        mpg.setTemplateEngine(config.getTemplateEngine());
        mpg.setCfg(config.getInjection());

        PackageConfig pkgInfo = new PackageConfig();
        mpg.setPackageInfo(pkgInfo);

        mpg.execute();
    }
}
