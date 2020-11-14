package com.chuang.tauceti.httpclient;

import com.chuang.tauceti.httpclient.async.AsyncBuilder;
import com.chuang.tauceti.httpclient.async.AsyncHttpClient;
import com.chuang.tauceti.httpclient.sync.HttpClient;
import com.chuang.tauceti.httpclient.sync.SyncBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public class Https {

    public static HttpClient syncClient =  sync().setDefaultCharset("UTF-8")
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .trustAll()
            .build();
    public static AsyncHttpClient asyncClient =  async().setDefaultCharset("UTF-8")
            .workThreadFull()
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .trustAll()
            .build()
            .init();

    public static AsyncBuilder async() {
        return new AsyncBuilder();
    }

    public static SyncBuilder sync() {
        return new SyncBuilder();
    }

    public static AsyncBuilder async(HttpAsyncClientBuilder builder) {
        return new AsyncBuilder(builder);
    }

    public static SyncBuilder async(HttpClientBuilder builder) {
        return new SyncBuilder(builder);
    }
}
