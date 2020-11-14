package com.chuang.tauceti.sdk.payment;


import com.chuang.tauceti.support.enums.PaymentType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class PaymentPlatformConfig {
    private final String merchantId;

    private final String privateKey;

    private final @Nullable
    String publicKey;

    private final String proxyUrl;

    private final String apiUrl;

    private final Map<String, Object> ext;

    private final String frontCallback;

    private final String serverCallback;

    private final String platform;

    private final PaymentType type;

    /**
     * 该配置的数据源，由实现者自行填充或转换，这里只提供一个参数用于辅助实现者去保存源数据
     */
    private final Object source;

    /**
     * 配置key，该参数提供实现者一个随时可以进行查找配置的唯一key
     * 查找方式由{@link ConfigLoadPolicy} 确定
     */
    private final String configKey;

    public PaymentPlatformConfig(String configKey,
                                 String platform,
                                 String merchantId,
                                 PaymentType type,
                                 String privateKey,
                                 String apiUrl,
                                 String proxyUrl,
                                 Map<String, Object> ext,
                                 String frontCallback,
                                 String serverCallback,
                                 Object source,
                                 @Nullable String publicKey) {
        this.configKey = configKey;
        this.platform = platform;
        this.merchantId = merchantId;
        this.type = type;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.proxyUrl = proxyUrl;
        this.ext = ext;
        this.apiUrl = apiUrl;
        this.frontCallback = frontCallback;
        this.serverCallback = serverCallback;
        this.source = source;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public Optional<String> getPublicKey() {
        return Optional.ofNullable(publicKey);
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public Object getExt(String key) {
        return ext.get(key);
    }

    public Object getExtOrDefault(String key, Object elseValue) {
        return ext.getOrDefault(key, elseValue);
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public String getFrontCallback() {
        return frontCallback;
    }

    public String getServerCallback() {
        return serverCallback;
    }

    public PaymentType getType() {
        return type;
    }

    public String getFrontCallbackAbsolute() {
        return this.frontCallback + "/" + configKey + (type.isWithdraw() ? "/withdraw" : "/deposit");
    }

    public String getServerCallbackAbsolute() {
        return this.serverCallback + "/" + configKey + (type.isWithdraw() ? "/withdraw" : "/deposit");
    }

    public Object getSource() {
        return source;
    }
}
