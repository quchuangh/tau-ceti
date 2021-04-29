package com.chuang.tauceti.httpclient;

import com.chuang.tauceti.httpclient.async.AsyncBuilder;
import com.chuang.tauceti.httpclient.async.AsyncHttpClient;
import com.chuang.tauceti.httpclient.sync.HttpClient;
import com.chuang.tauceti.httpclient.sync.SyncBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public class Https {

    private static HttpClient defaultSyncClient = syncBuilder().setDefaultCharset("UTF-8")
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .trustAll()
            .build();
    private static AsyncHttpClient defaultAsyncClient;

    public static synchronized AsyncHttpClient async() {
        if (null == defaultSyncClient) {
            defaultAsyncClient = asyncBuilder().setDefaultCharset("UTF-8")
                    .workThreadFull()
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000)
                    .setConnectionRequestTimeout(10000)
                    .trustAll()
                    .build()
                    .init();
        }
        return defaultAsyncClient;
    }

    public static synchronized void setDefaultAsyncClient(AsyncHttpClient client) {
        if (null != defaultAsyncClient) {
            defaultAsyncClient.shutdown();
        }
        defaultAsyncClient = client;
    }

    public static synchronized void setDefaultSyncClient(HttpClient client) {
        defaultSyncClient = client;
    }

    public static HttpClient sync() {
        return defaultSyncClient;
    }

    public static AsyncBuilder asyncBuilder() {
        return new AsyncBuilder();
    }

    public static SyncBuilder syncBuilder() {
        return new SyncBuilder();
    }

    public static AsyncBuilder asyncBuilder(HttpAsyncClientBuilder builder) {
        return new AsyncBuilder(builder);
    }

    public static SyncBuilder asyncBuilder(HttpClientBuilder builder) {
        return new SyncBuilder(builder);
    }
}
