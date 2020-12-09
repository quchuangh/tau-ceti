package com.chuang.tauceti.generator.impl;

import com.chuang.tauceti.generator.GenType;
import com.chuang.tauceti.generator.JavaGenerator;

public class ControllerGen implements JavaGenerator {


    @Override
    public String template() {
        return "/templates/controller/controller.java.vm";
    }

    @Override
    public GenType type() {
        return GenType.CONTROLLER;
    }
}
