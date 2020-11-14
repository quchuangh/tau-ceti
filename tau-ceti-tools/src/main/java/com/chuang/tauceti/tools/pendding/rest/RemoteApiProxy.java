//package com.chuang.tauceti.tools.pendding.rest;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.chuang.tauceti.tools.pendding.rest.annotation.Mapping;
//import com.chuang.tauceti.tools.pendding.rest.annotation.RestRemoteApi;
//
//import java.io.Serializable;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//public class RemoteApiProxy implements InvocationHandler, Serializable {
//
//    private static final long serialVersionUID = -1L;
//
//    private final Class<?> remoteApiClass;
//
//    public RemoteApiProxy(Class<?> remoteApiClass) {
//        this.remoteApiClass = remoteApiClass;
//    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) {
//        RestRemoteApi restRemoteApi = remoteApiClass.getAnnotation(RestRemoteApi.class);
//        Mapping mapping = method.getAnnotation(Mapping.class);
//
//        CompletableFuture<Object> obj = Request.newBuilder()
//                .url(restRemoteApi.domain() + mapping.value())
//                .method(mapping.method())
//                .parameter(toMap(method, args))
//                .build()
//                .asyncExecuteAsString()
//                .thenApply(s -> {
//                    Class<?> clazz = method.getReturnType();
//                    if(clazz == CompletableFuture.class) {
//                        clazz = clazz.getComponentType();
//                    }
//                    return JSONObject.parseObject(s, clazz);
//                });
//
//        if (method.getReturnType() == CompletableFuture.class) {
//            return obj;
//        } else {
//            return obj.join();
//        }
//    }
//
//    private Map<String, String> toMap(Method method, Object[] args) {
//        Parameter[] parameters = method.getParameters();
//
//        Map<String, String> params = new HashMap<>();
//        for(int i = 0; i < parameters.length; i++) {
//            params.put(parameters[i].getName(), args[i].toString());
//        }
//        return params;
//    }
//
//}
