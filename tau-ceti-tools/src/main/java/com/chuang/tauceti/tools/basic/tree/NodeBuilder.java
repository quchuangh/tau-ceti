package com.chuang.tauceti.tools.basic.tree;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ath on 2016/3/14.
 */
@SuppressWarnings("unused")
public class NodeBuilder<K extends Serializable, V extends Serializable> {

    public NodeBuilder() {}

    public NodeBuilder<K, V> index(Iterable<V> resources) {
        for(V obj : resources) {
            sourceIndex.put(relation.relation(obj).getOne(), obj);
        }
        return this;
    }

    public NodeBuilder<K, V> relation(IRelation<K, V> relation){
        this.relation = relation;
        return this;
    }


    public synchronized List<Node<V>> toNode(Iterable<V> iter) {
        rootNode = new NodeImpl<>();

        indexMap.clear();
        indexMap.putAll(sourceIndex);
        for(V obj : iter) {
            indexMap.put(relation.relation(obj).getOne(), obj);
        }

        for(V obj : iter) {
            createNode(relation.relation(obj).getOne(), null);
        }

        return rootNode.getChildren();
    }

    private final Map<K, Node<V>> cache = new HashMap<>();
    private final Map<K, V> indexMap = new HashMap<>();

    private final Map<K, V> sourceIndex = new HashMap<>();
    private IRelation<K, V> relation;
    private Node<V> rootNode;


    private Node<V> getRoot(Node<V> node){
        if(node.isRoot()) {
            return node;
        } else {
            return getRoot(node.parent());
        }
    }

    private void createNode(K k, Node<V> child) {
        if(!cache.containsKey(k)) {
            NodeImpl<V> node = new NodeImpl<>();
            node.source = indexMap.get(k);
            node.childs = new ArrayList<>();
            cache.put(k, node);
        }
        NodeImpl<V> node = (NodeImpl<V>)cache.get(k);
        if(null != child) {
            node.childs.add(child);
        }

        K parentID = relation.relation(node.source).getTwo();
        if(parentID == null || indexMap.get(parentID) == null) {
            rootNode.addChild(node);
            return;
        }
        Node<V> parentNode = cache.get(parentID);
        if(null == parentNode) {
            createNode(parentID, node);
        } else {
            //????????????
            parentNode.addChild(node);
            node.parent = parentNode;
        }


    }

//    private void createNode(T obj) {
//        K myID = relation.myID(obj);
//        if(cache.containsKey(myID)) {
//            Node<T> node = cache.get(myID);
//            node.source = obj;
//        } else {
//            Node<T> node = new Node<>();
//            node.childs = new ArrayList<>();
//            node.source = obj;
//
//            createParent(node);
//        }
//
//
//    }
//
//    private void createParent(Node<T> node) {
//        K parentID = relation.parentID(node.source);
//        if(!cache.containsKey(parentID)) {
//            Node<T> parentNode = new Node<>();
//            cache.put(parentID, parentNode);
//        }
//
//        Node<T> parentNode = cache.get(parentID);
//        parentNode.addChild(node);
//    }



    public static class NodeImpl<V extends Serializable> implements Node<V> {
        private V source;
        private List<Node<V>> childs = new ArrayList<>();

        private Node<V> parent;

        public void addChild(Node<V> node) {
            childs.add(node);
        }

        public List<Node<V>> getChildren() {
            return this.childs;
        }

        public V getSource() {
            return source;
        }

        public Node<V> parent() {
            return parent;
        }

        public boolean hasChild() {
            return null != childs && !childs.isEmpty();
        }

        public boolean isRoot(){
            return null == parent;
        }

        public boolean isLeaf(){
            return !hasChild();
        }

    }


}
