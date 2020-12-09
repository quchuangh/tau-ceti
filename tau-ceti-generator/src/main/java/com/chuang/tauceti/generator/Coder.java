package com.chuang.tauceti.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.chuang.tauceti.generator.config.GenConfig;

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
        mpg.setPackageInfo(config.getPackageCfg());

        mpg.execute();
    }
}
