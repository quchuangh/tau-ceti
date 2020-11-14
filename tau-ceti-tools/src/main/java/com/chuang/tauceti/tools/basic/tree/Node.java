package com.chuang.tauceti.tools.basic.tree;

import java.util.List;

@SuppressWarnings("unused")
public interface Node<V extends java.io.Serializable> extends java.io.Serializable {

        void addChild(Node<V> node);

        List<Node<V>> getChildren();

        V getSource();

        Node<V> parent();

        boolean hasChild();

        boolean isRoot();

        boolean isLeaf();

}
