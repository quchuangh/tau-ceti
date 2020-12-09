package com.chuang.tauceti.generator.impl;

import com.chuang.tauceti.generator.GenType;
import com.chuang.tauceti.generator.JavaGenerator;

public class EntityGen implements JavaGenerator {

    @Override
    public String template() {
        return "/templates/entity/entity.java.vm";
    }

    @Override
    public GenType type() {
        return GenType.ENTITY;
    }
}
