package com.chuang.tauceti.generator.impl;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.chuang.tauceti.generator.GenType;
import com.chuang.tauceti.generator.Generator;
import com.chuang.tauceti.generator.ResourceGenerator;
import com.chuang.tauceti.generator.config.GenConfig;

public class MapperXmlGen implements ResourceGenerator {


    @Override
    public String outputFile(GenConfig config, TableInfo info) {
        return "/mapper/" + info.getEntityName() + "Mapper.xml";
    }

    @Override
    public String template() {
        return "/templates/mapper/mapper.xml.vm";
    }

    @Override
    public GenType type() {
        return GenType.MAPPER_XML;
    }


}
