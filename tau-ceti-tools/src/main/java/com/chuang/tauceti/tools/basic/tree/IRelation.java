package com.chuang.tauceti.tools.basic.tree;

public interface IRelation<K, V> {
        K parentID(V t);
        K myID(V t);
}
