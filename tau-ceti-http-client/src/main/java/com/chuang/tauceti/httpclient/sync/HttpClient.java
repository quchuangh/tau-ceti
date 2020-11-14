package com.chuang.tauceti.httpclient.sync;

import com.chuang.tauceti.httpclient.Request;
import com.chuang.tauceti.httpclient.Response;
import com.chuang.tauceti.httpclient.Tools;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Map;


/**
 * 一个简易,线程安全的httpclient。 只要职责是提供http客户端配置。如 http 和 ssl 构建，并提供一些简单的通用方法。具体实现一来apache httpclient。
 *
 * 该类是线程安全的，允许多线程访问单例。
 * 示例：
 * <code>
 *      SimpleHttpClient client = SimpleHttpClient.http().build();
 *      Map<String, String> headers = new HashMap();
 *      headers.put(key1, val1);
 *      headers.put(key2, val2);
 *      Map<String, String> params = new HashMap();
 *      params.put(key1, val1);
 *      params.put(key2, val2);
 *
 *      String response = client.doGet(url, params, headers, "UTF-8", null);
 *
 * </code>
 * 以上代码可能过于繁琐。参数含义不够清晰，而且重载方法太多，不容易选择。可以考虑使用HttpRequest
 * <code>
 *      HttpClient client = HttpClient.http().build();
 *      String response = HttpRequest.newBuilder().url(url)
 *          .header(headkey1, headval1)
 *          .header(headkey2, headval2)
 *          .parameter(pk1, pv1)
 *          .parameter(pk2, pv2)
 *          .method("GET")
 *          .charsetUTF()
 *          .build()
 *          .execute(client);
 *
 * </code>
 *
 * Created by ath on 2016/8/18.
 */
@ThreadSafe
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class HttpClient {

    public HttpClient(String defaultCharset, CloseableHttpClient httpClient, RequestConfig defaultConfig) {
        this.defaultCharset = defaultCharset;
        this.httpClient = httpClient;
        this.defaultConfig = defaultConfig;

    }

    private final String defaultCharset;
    private final CloseableHttpClient httpClient;
    private final RequestConfig defaultConfig;

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public String doGet(String url) throws IOException {
        return doGet(url, null);
    }

    public String doPost(String url) throws IOException {
        return doPost(url, null);
    }

    public String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, defaultCharset);
    }

    public String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, params, defaultCharset);
    }

    public String doPost(String url, Map<String, String> params, String charset)  throws IOException {
        return doPost(url, params, null, charset, null).asString();
    }

    public String doGet(String url, Map<String, String> params, String charset) throws IOException {
        return doGet(url, params, null,charset, null, -1, -1).asString() ;
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
    public Response doGet(String url, Map<String, String> params, Map<String, String> heads, String charset, HttpHost proxy, int connTimeout, int readTimeout) throws IOException {

        if (Tools.isBlank(url)) {
            return null;
        }

        return exec(Request.Get(url).parameter(params).header(heads).charset(charset).config().setProxy(proxy).setConnectTimeout(connTimeout).setSocketTimeout(readTimeout).done().build(), null);

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
    public Response doPost(String url, Map<String, String> params, Map<String, String> heads, String charset, HttpHost proxy)  throws IOException {

        if (Tools.isBlank(url)) {
            return null;
        }

        return exec(Request.Post(url).parameter(params).header(heads).charset(charset).config().setProxy(proxy).done().build(), null);
    }



    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param requestData  请求体字符串
     * @param charset 编码格式
     * @return 页面内容
     */
    public Response doPost(String url, String requestData, Map<String, String> heads, String charset, HttpHost proxy)  throws IOException {

        if (Tools.isBlank(url)) {
            return null;
        }
        return exec(Request.Post(url).body(requestData).header(heads).charset(charset).config().setProxy(proxy).done().build(), null);
    }


    /**
     * HTTP put 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param requestData  请求体字符串
     * @param charset 编码格式
     * @return 页面内容
     */
    public Response doPut(String url, String requestData, Map<String,String> heads, String charset, HttpHost proxy)  throws IOException {

        if (Tools.isBlank(url)) {
            return null;
        }
        return exec(Request.Put(url).body(requestData).header(heads).charset(charset).config().setProxy(proxy).done().build(), null);
    }

    /**
     * HTTP put 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求体
     * @param charset 编码格式
     * @return 页面内容
     */
    public Response doPut(String url, Map<String, String> params, Map<String,String> heads, String charset, HttpHost proxy)  throws IOException {

        if (Tools.isBlank(url)) {
            return null;
        }
        return exec(Request.Put(url).parameter(params).header(heads).charset(charset).config().setProxy(proxy).done().build(), null);
    }


    /**
     * 执行请求
     */
    public Response exec(Request request, HttpContext context) throws IOException {

        HttpRequestBase requestBase = request.get();
        requestBase.setConfig(request.getConfig().cover(defaultConfig));

        String charset = request.getCharset();
        if(Tools.isBlank(charset)) {
            charset = this.defaultCharset;
        }


        Map<String, String> headers = request.getHeaders();
        for(String key : headers.keySet()) {
            requestBase.addHeader(key, headers.get(key));
        }

        Tools.handEntity(request, charset);

        try {
            CloseableHttpResponse response;
            if(null == context) {
                response = httpClient.execute(requestBase);
            } else {
                response = httpClient.execute(requestBase, context);
            }
            return new Response(requestBase, response, charset);
        } catch (IOException e) {
            logger.error("http 请求失败", e);
            throw e;
        }
    }
}
