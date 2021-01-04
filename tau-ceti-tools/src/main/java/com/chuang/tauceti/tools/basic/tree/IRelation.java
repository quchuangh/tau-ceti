package com.chuang.tauceti.tools.basic.tree;

import com.chuang.tauceti.support.BiValue;

public interface IRelation<ID, T> {

    BiValue<ID, ID> relation(T t);
}
