package com.chuang.tauceti.generator.impl;

import com.chuang.tauceti.generator.GenType;
import com.chuang.tauceti.generator.JavaGenerator;

public class MapperGen implements JavaGenerator {

    @Override
    public String template() {
        return "/templates/mapper/mapper.java.vm";
    }

    @Override
    public GenType type() {
        return GenType.MAPPER;
    }

}
