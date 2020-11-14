//package com.chuang.tauceti.tools.pendding.rest;
//
//import org.springframework.beans.factory.FactoryBean;
//
//import java.lang.reflect.Proxy;
//
//public class RemoteApiFactoryBean<T> implements FactoryBean<T> {
//
//    private Class<T> remoteApiClass;
//
//    public RemoteApiFactoryBean() {
//    }
//
//    /**
//     * 由getBean时自动注入。
//     * @param remoteApiClass
//     */
//    public RemoteApiFactoryBean(Class<T> remoteApiClass) {
//        this.remoteApiClass = remoteApiClass;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public T getObject() {
//        return (T) Proxy.newProxyInstance(remoteApiClass.getClassLoader(),
//                new Class[] { remoteApiClass },
//                new RemoteApiProxy(remoteApiClass)
//        );
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return this.remoteApiClass;
//    }
//
//    public void setRemoteApiClass(Class<T> remoteApiClass) {
//        this.remoteApiClass = remoteApiClass;
//    }
//}
