package com.chuang.tauceti.generator.impl;

import com.chuang.tauceti.generator.GenType;
import com.chuang.tauceti.generator.JavaGenerator;

public class ServiceGen implements JavaGenerator {
    @Override
    public GenType type() {
        return GenType.SERVICE;
    }

    @Override
    public String template() {
        return "/service/service.java.vm";
    }

}
