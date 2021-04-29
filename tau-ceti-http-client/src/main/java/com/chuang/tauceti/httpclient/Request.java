package com.chuang.tauceti.httpclient;

import com.chuang.tauceti.httpclient.async.AsyncHttpClient;
import com.chuang.tauceti.httpclient.sync.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.methods.*;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


/**
 * 一个简单的HttpRequest。 作用是描述请求内容
 *  多数时候Fluent 通过使用{@link org.apache.http.client.fluent.Request}的实现已经完全够用，例如 String html = org.apache.http.client.fluent.Request.Post("http://www.test.com").execute().returnContent().asString();
 *  Request 的主要作用是和 HttpClient 配合使用，HttpClient提供默认的配置。某些情况下，例如某个平台下的所有接口都必须使用SSL。因此每个
 *  Request的构建都比较麻烦，使用HttpClient可以简化这种配置，并提供给Request。
 *  使用Request的异步方式时，不用担心request和response不一致的情况。换句话说，同一时间发送N个request，每个A request返回的Promise不会获取到 B request的结果。
 *  即便N个request都是访问同一个网站，也不混淆。通过每个request开启一个连接，这样就能保证request和response的一致。
 *  需要注意的是: 虽然目前绝大多数服务器（容器）都能保证在同一连接上发起的所有请求都是有序相应的（一般请求是一个个处理，一个个response，所以是有序的），但请求一次开启一个连接还是更保险一些。
 * Created by ath on 2016/10/28.
 */
@ThreadSafe
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class Request implements Supplier<HttpRequestBase> {

    private final String url;
    private final HttpMethod method;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final HttpEntity entity;
    private final String body;
    private final HttpRequestBase base;

    private final String charset;

    private CoverableRequestConfig config;


    public Request(String url,
                   HttpMethod method,
                   Map<String, String> headers,
                   Map<String, String> params,
                   String body,
                   HttpEntity entity,
                   String charset,
                   CoverableRequestConfig config) {

        this.base = Tools.initBase(method, url);
        this.method = method;
        this.headers = Collections.unmodifiableMap(headers);
        this.charset = charset;
        this.config = config;
        this.params = Collections.unmodifiableMap(params);
        this.url = url;
        this.body = body;
        this.entity = entity;

    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getCharset() {
        return charset;
    }

    public String getURL() {
        return url;
    }

    public String getMethodString() {
        return method.toString();
    }

    public CoverableRequestConfig getConfig() {
        return config;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public Response execute(HttpClient client) throws IOException {
        return client.exec(this, null);
    }

    public CompletableFuture<Response> execute(AsyncHttpClient client) {
        return client.exec(this, null);
    }

    public CompletableFuture<String> executeAsString(AsyncHttpClient client) {

        return execute(client).thenApply(Response::asString);
    }

    public String executeAsString(HttpClient client) throws IOException {
        return execute(client).asString();
    }

    public Response execute() throws IOException {

        return execute(Https.sync());
    }

    public String executeAsString() throws IOException {
        return execute().asString();
    }


    public CompletableFuture<Response> asyncExecute() {
        return execute(Https.async());
    }

    public CompletableFuture<String> asyncExecuteAsString() {
        return asyncExecute().thenApply(Response::asString);
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder Post(String url) {
        return newBuilder().method(HttpMethod.POST).url(url);
    }
    public static Builder Get(String url) {
        return newBuilder().method(HttpMethod.GET).url(url);
    }

    public static Builder Head(String url) {
        return newBuilder().method(HttpMethod.HEAD).url(url);
    }
    public static Builder Delete(String url) {
        return newBuilder().method(HttpMethod.DELETE).url(url);
    }
    public static Builder Patch(String url) {
        return newBuilder().method(HttpMethod.PATCH).url(url);
    }
    public static Builder Put(String url) {
        return newBuilder().method(HttpMethod.PUT).url(url);
    }
    public static Builder Trace(String url) {
        return newBuilder().method(HttpMethod.TRACE).url(url);
    }

    @Override
    public HttpRequestBase get() {
        return base;
    }

    public static class Builder {
        private HttpMethod method = HttpMethod.GET;
        private Map<String, String> headers;
        private Map<String, String> params;
        private HttpEntity entity;
        private String body;
        private String url;
        private String charset;
        private MyCovertConfigBuilder config = new MyCovertConfigBuilder(this);

        public Builder() {
            headers = new HashMap<>();
            params = new LinkedHashMap<>();
            charset = "UTF-8";
        }

        public Map<String, String> headers() {
            return this.headers;
        }
        public Map<String, String> params() {
            return this.params;
        }

        public Builder method(String method) {
            this.method = HttpMethod.valueOf(method.toUpperCase());
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder header(String key, String val){
            headers.put(key, val);
            return this;
        }
        public Builder header(Map<String, String> headers){
            this.headers.putAll(headers);
            return this;
        }

        public Builder parameter(String key, String val){
            params.put(key, val);
            this.entity = null;
            return this;
        }

        public Builder parameter(Map<String, String> map) {
            params.putAll(map);
            this.entity = null;
            return this;
        }

        public Builder charset(String charset) {
            this.charset = charset;
            return this;
        }

        public Builder chartUTF8() {
            this.charset = "UTF-8";
            return this;
        }

        public Builder chartGBK() {
            this.charset = "GBK";
            return this;
        }

        /**
         * body 和 entity 不能共存
         * 优先使用body
         */
        public Builder body(String body) {
            this.body = body;
            this.params.clear();
            return this;
        }

        /**
         * 不能和body共存，如果加入了body，则entity无效
         */
        public Builder entity(HttpEntity entity) {
            this.entity = entity;
            return this;
        }

        public Builder url(String url) {
            this.url = url.trim();
            return this;
        }

        /**
         * 将querystring转化为参数
         */
        public Builder queryString(String queryString) {
            String[] args = queryString.split("&");
            Arrays.stream(args).forEach(s -> {
                String[] kv = s.split("=");
                parameter(kv[0], kv[1]);
            });
            return this;
        }

        public MyCovertConfigBuilder config() {
            return this.config;
        }

        public Request build() {
            return new Request(url, method, headers, params, body, entity, charset, config.build());
        }
    }

    public static class MyCovertConfigBuilder extends CoverableRequestConfig.Builder<MyCovertConfigBuilder> {

        private final Builder builder;

        public MyCovertConfigBuilder(Builder builder) {
            this.builder = builder;
        }

        public Builder done() {
            return builder;
        }

    }

}
