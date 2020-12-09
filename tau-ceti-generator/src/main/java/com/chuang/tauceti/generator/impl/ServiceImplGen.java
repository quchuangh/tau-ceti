package com.chuang.tauceti.generator.impl;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.GenType;
import com.chuang.tauceti.generator.JavaGenerator;

public class ServiceImplGen implements JavaGenerator {

    @Override
    public GenType type() {
        return GenType.SERVICE_IMPL;
    }

    public String pkg(String rootPkg, TableInfo info) {
        return rootPkg + ".service.impl";
    }

    @Override
    public String template() {
        return "/templates/service/serviceImpl.java.vm";
    }


}
