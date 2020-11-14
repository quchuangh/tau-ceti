package com.chuang.tauceti.httpclient.async;

import com.chuang.tauceti.httpclient.Request;
import com.chuang.tauceti.httpclient.Response;
import com.chuang.tauceti.httpclient.Tools;
import com.chuang.tauceti.support.CompletableFutureWrapper;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


/**
 * Created by ath on 2017/1/6.
 */
@ThreadSafe
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class AsyncHttpClient {
    private final String defaultCharset;
    private final CloseableHttpAsyncClient asyncHttpClient;
    private final RequestConfig defaultConfig;
    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpClient.class);

    public AsyncHttpClient(final String defaultCharset, final CloseableHttpAsyncClient asyncHttpClient, final RequestConfig defaultConfig) {
        this.defaultCharset = defaultCharset;
        this.asyncHttpClient = asyncHttpClient;
        this.defaultConfig = defaultConfig;
    }

    public AsyncHttpClient init() {
        try {
            start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("开始关闭异步Http客户端");
                AsyncHttpClient.this.shutdown();
                logger.info("异步Http客户端 成功 关闭");
            }));
        } catch (Throwable e) {
            logger.error("无法开启异步http客户端", e);
        }
        return this;
    }

    public void start() {
        asyncHttpClient.start();
    }

    public void shutdown() {
        try {
            asyncHttpClient.close();
        } catch (Exception e) {
            logger.error("无法关闭异步http客户端，请检查相关问题，避免内存泄露", e);
        }
    }

    public CompletableFuture<String> doGet(String url) {
        return doGet(url, null);
    }

    public CompletableFuture<String> doPost(String url) {
        return doPost(url, null);
    }

    public CompletableFuture<String> doGet(String url, Map<String, String> params) {
        return doGet(url, params, defaultCharset);
    }

    public CompletableFuture<String> doPost(String url, Map<String, String> params) {
        return doPost(url, params, defaultCharset);
    }

    public CompletableFuture<String> doPost(String url, Map<String, String> params, String charset) {
        return doPost(url, params, null, null, charset, null).thenApply(Response::asString);
    }

    public CompletableFuture<String> doGet(String url, Map<String, String> params, String charset) {
        return doGet(url, params, null, null,charset, null, -1, -1).thenApply(Response::asString);
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址 不能为空
     * @param params  请求的参数 允许为空
     * @param charset 编码格式  允许为空，若为空取httpclient配置中的默认字符编码
     * @param proxy 代理地址，允许为空，若为空，则不适用代理
     * @return 页面内容
     */
    public CompletableFuture<Response> doGet(String url, Map<String, String> params, Map<String, String> heads, HttpContext context, String charset, HttpHost proxy, int connTimeout, int readTimeout) {

        if (Tools.isBlank(url)) {
            return CompletableFuture.completedFuture(null);
        }

        Request request = Request.Get(url)
                .parameter(params)
                .header(heads)
                .charset(charset)
                .config()
                .setProxy(proxy)
                .setConnectTimeout(connTimeout)
                .setConnectionRequestTimeout(connTimeout)
                .setSocketTimeout(readTimeout)
                .done()
                .build();
        return exec(request, context);

    }


    /**
     * HTTP post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址 不能为空
     * @param params  请求的参数 允许为空
     * @param charset 编码格式  允许为空，若为空取httpclient配置中的默认字符编码
     * @param proxy 代理地址，允许为空，若为空，则不适用代理
     * @return 页面内容
     */
    public CompletableFuture<Response> doPost(String url, Map<String, String> params, HttpContext context, Map<String, String> heads, String charset, HttpHost proxy) {

        if (Tools.isBlank(url)) {
            return CompletableFuture.completedFuture(null);
        }
        Request request = Request.Post(url)
                .parameter(params)
                .header(heads)
                .charset(charset)
                .config()
                .setProxy(proxy)
                .setConnectTimeout(-1)
                .setConnectionRequestTimeout(-1)
                .setSocketTimeout(-1)
                .done()
                .build();
        return exec(request, context);
    }



    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param requestData  请求体字符串
     * @param charset 编码格式
     * @return 页面内容
     */
    public CompletableFuture<Response> doPost(String url, String requestData, HttpContext context, Map<String, String> heads, String charset, HttpHost proxy) {

        if (Tools.isBlank(url)) {
            return CompletableFuture.completedFuture(null);
        }
        Request request = Request.Post(url)
                .body(requestData)
                .header(heads)
                .charset(charset)
                .config()
                .setProxy(proxy)
                .setConnectTimeout(-1)
                .setConnectionRequestTimeout(-1)
                .setSocketTimeout(-1)
                .done()
                .build();
        return exec(request, context);
    }


    /**
     * HTTP put 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param requestData  请求体字符串
     * @param charset 编码格式
     * @return 页面内容
     */
    public CompletableFuture<Response> doPut(String url, String requestData, HttpContext context, Map<String,String> heads, String charset, HttpHost proxy) {

        if (Tools.isBlank(url)) {
            return CompletableFuture.completedFuture(null);
        }

        Request request = Request.Put(url)
                .body(requestData)
                .header(heads)
                .charset(charset)
                .config()
                .setProxy(proxy)
                .setConnectTimeout(-1)
                .setConnectionRequestTimeout(-1)
                .setSocketTimeout(-1)
                .done()
                .build();

        return exec(request, context);
    }

    /**
     * HTTP put 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param parmas  请求体
     * @param charset 编码格式
     * @return 页面内容
     */
    public CompletableFuture<Response> doPut(String url, Map<String, String> parmas, HttpContext context, Map<String,String> heads, String charset, HttpHost proxy) {

        if (Tools.isBlank(url)) {
            return CompletableFuture.completedFuture(null);
        }
        Request request = Request.Put(url)
                .parameter(parmas)
                .header(heads)
                .charset(charset)
                .config()
                .setProxy(proxy)
                .setConnectTimeout(-1)
                .setConnectionRequestTimeout(-1)
                .setSocketTimeout(-1)
                .done()
                .build();
        return exec(request, context);
    }


    public CompletableFuture<Response> exec(Request request, HttpContext context) {
        CompletableFutureWrapper<Response> future = new CompletableFutureWrapper<>();

        RequestConfig config = request.getConfig().cover(defaultConfig);


        HttpRequestBase requestBase = request.get();

        requestBase.setConfig(config);

        Map<String, String> headers = request.getHeaders();
        for(String key : headers.keySet()) {
            requestBase.addHeader(key, headers.get(key));
        }

        String charset = request.getCharset();
        if(Tools.isBlank(charset)) {
            charset = this.defaultCharset;
        }


        try {
           Tools.handEntity(request, charset);
        } catch (IOException e) {
            future.completeExceptionally(e);
        }

        final String finalCharset = charset;

        FutureCallback<HttpResponse> fc = new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                synchronized (future) {
                    Response response = new Response(requestBase, httpResponse, finalCharset);
                    future.complete(response);

                }
            }
            @Override
            public void failed(Exception e) {
                logger.debug("urras request 失败", e);
                future.completeExceptionally(new IOException(requestBase.toString() + "失败", e));
            }

            public void cancelled() {
                logger.debug("urras request 取消");
                future.cancel(true);
            }
        };

        Future<HttpResponse> f;
        if(null == context) {
            f = asyncHttpClient.execute(requestBase, fc);
        } else {
            f = asyncHttpClient.execute(requestBase, context, fc);
        }
        future.setCancelHandler(f::cancel);
        return future;
    }

}
