package com.chuang.tauceti.sdk.payment.deposit;

import com.chuang.tauceti.httpclient.Https;
import com.chuang.tauceti.httpclient.Request;
import com.chuang.tauceti.httpclient.async.AsyncHttpClient;
import com.chuang.tauceti.support.Result;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class GenFormDepositInfo extends DepositInfo {

    public static AsyncHttpClient allow302AsyncHttpClient = Https
            .async(HttpAsyncClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()))
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .trustAll()
            .build()
            .init();

    public GenFormDepositInfo(String merchantId,
                              String content,
                              Long amount,
                              String reference) {
        super(Type.FORM_DOC, merchantId, content, amount, reference);
    }

    public abstract CompletableFuture<Result<DepositInfo>> submit();

    public static GenFormDepositInfo form(HttpHost proxy,
                                          String merchantId,
                                          String apiPath,
                                          String method,
                                          Long amount,
                                          String reference,
                                          Map<String, String> params) {
        StringBuilder html = new StringBuilder("<form action=\"" + apiPath + "\" method=\"" + method + "\">");
        for(String key : params.keySet()) {
            html.append("<input type=\"hidden\" name=\"").append(key).append("\" value=\"").append(params.get(key)).append("\" />");
        }
        html.append("</form><script>document.forms[0].submit();</script>");

        return new GenFormDepositInfo(merchantId, html.toString(), amount, reference) {
            @Override
            public CompletableFuture<Result<DepositInfo>> submit() {
                return Request.newBuilder().method(method).url(apiPath).parameter(params).config().setProxy(proxy).done().build()
                        .executeAsString(allow302AsyncHttpClient)
                        .thenApply(s -> Result.success(new DepositInfo(Type.FORM_DOC, merchantId, s, amount, reference)));
            }
        };
    }
}
