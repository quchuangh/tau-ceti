package com.chuang.tauceti.httpclient;


import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;

import javax.annotation.concurrent.ThreadSafe;
import java.net.InetAddress;
import java.util.Collection;

@SuppressWarnings("ALL")
@ThreadSafe
@Contract(
        threading = ThreadingBehavior.IMMUTABLE
)
public class CoverableRequestConfig {

    private final Boolean expectContinueEnabled;
    private final HttpHost proxy;
    private final InetAddress localAddress;
    private final String cookieSpec;
    private final Boolean redirectsEnabled;
    private final Boolean relativeRedirectsAllowed;
    private final Boolean circularRedirectsAllowed;
    private final Integer maxRedirects;
    private final Boolean authenticationEnabled;
    private final Collection<String> targetPreferredAuthSchemes;
    private final Collection<String> proxyPreferredAuthSchemes;
    private final Integer connectionRequestTimeout;
    private final Integer connectTimeout;
    private final Integer socketTimeout;
    private final Boolean contentCompressionEnabled;
    private final Boolean normalizeUri;


    CoverableRequestConfig(Boolean expectContinueEnabled,
                           HttpHost proxy,
                           InetAddress localAddress,
                           String cookieSpec,
                           Boolean redirectsEnabled,
                           Boolean relativeRedirectsAllowed,
                           Boolean circularRedirectsAllowed,
                           Integer maxRedirects,
                           Boolean authenticationEnabled,
                           Collection<String> targetPreferredAuthSchemes,
                           Collection<String> proxyPreferredAuthSchemes,
                           Integer connectionRequestTimeout,
                           Integer connectTimeout,
                           Integer socketTimeout,
                           Boolean contentCompressionEnabled,
                           Boolean normalizeUri) {
        this.expectContinueEnabled = expectContinueEnabled;
        this.proxy = proxy;
        this.localAddress = localAddress;
        this.cookieSpec = cookieSpec;
        this.redirectsEnabled = redirectsEnabled;
        this.relativeRedirectsAllowed = relativeRedirectsAllowed;
        this.circularRedirectsAllowed = circularRedirectsAllowed;
        this.maxRedirects = maxRedirects;
        this.authenticationEnabled = authenticationEnabled;
        this.targetPreferredAuthSchemes = targetPreferredAuthSchemes;
        this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        this.contentCompressionEnabled = contentCompressionEnabled;
        this.normalizeUri = normalizeUri;
    }

    public Boolean getExpectContinueEnabled() {
        return expectContinueEnabled;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public String getCookieSpec() {
        return cookieSpec;
    }

    public Boolean getRedirectsEnabled() {
        return redirectsEnabled;
    }

    public Boolean getRelativeRedirectsAllowed() {
        return relativeRedirectsAllowed;
    }

    public Boolean getCircularRedirectsAllowed() {
        return circularRedirectsAllowed;
    }

    public Integer getMaxRedirects() {
        return maxRedirects;
    }

    public Boolean getAuthenticationEnabled() {
        return authenticationEnabled;
    }

    public Collection<String> getTargetPreferredAuthSchemes() {
        return targetPreferredAuthSchemes;
    }

    public Collection<String> getProxyPreferredAuthSchemes() {
        return proxyPreferredAuthSchemes;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public Boolean getContentCompressionEnabled() {
        return contentCompressionEnabled;
    }

    public Boolean getNormalizeUri() {
        return normalizeUri;
    }

    public String toString() {
        return "[" +
                "expectContinueEnabled=" + this.expectContinueEnabled +
                ", proxy=" + this.proxy +
                ", localAddress=" + this.localAddress +
                ", cookieSpec=" + this.cookieSpec +
                ", redirectsEnabled=" + this.redirectsEnabled +
                ", relativeRedirectsAllowed=" + this.relativeRedirectsAllowed +
                ", maxRedirects=" + this.maxRedirects +
                ", circularRedirectsAllowed=" + this.circularRedirectsAllowed +
                ", authenticationEnabled=" + this.authenticationEnabled +
                ", targetPreferredAuthSchemes=" + this.targetPreferredAuthSchemes +
                ", proxyPreferredAuthSchemes=" + this.proxyPreferredAuthSchemes +
                ", connectionRequestTimeout=" + this.connectionRequestTimeout +
                ", connectTimeout=" + this.connectTimeout +
                ", socketTimeout=" + this.socketTimeout +
                ", contentCompressionEnabled=" + this.contentCompressionEnabled +
                ", normalizeUri=" + this.normalizeUri +
                "]";
    }


    public Builder copy() {
        return (new Builder())
                .setExpectContinueEnabled(this.expectContinueEnabled)
                .setProxy(this.proxy)
                .setLocalAddress(this.localAddress)
                .setCookieSpec(this.cookieSpec)
                .setRedirectsEnabled(this.redirectsEnabled)
                .setRelativeRedirectsAllowed(this.relativeRedirectsAllowed)
                .setCircularRedirectsAllowed(this.circularRedirectsAllowed)
                .setMaxRedirects(this.maxRedirects)
                .setAuthenticationEnabled(this.authenticationEnabled)
                .setTargetPreferredAuthSchemes(this.targetPreferredAuthSchemes)
                .setProxyPreferredAuthSchemes(this.proxyPreferredAuthSchemes)
                .setConnectionRequestTimeout(this.connectionRequestTimeout)
                .setConnectTimeout(this.connectTimeout)
                .setSocketTimeout(this.socketTimeout)
                .setContentCompressionEnabled(this.contentCompressionEnabled)
                .setNormalizeUri(this.normalizeUri);
    }

    public CoverableRequestConfig cover(CoverableRequestConfig config) {
        Builder builder = config.copy();
        if(null != expectContinueEnabled){ builder.setExpectContinueEnabled(expectContinueEnabled); }
        if(null != proxy){ builder.setProxy(proxy); }
        if(null != localAddress){ builder.setLocalAddress(localAddress); }
        if(null != cookieSpec){ builder.setCookieSpec(cookieSpec); }
        if(null != redirectsEnabled){ builder.setRedirectsEnabled(redirectsEnabled); }
        if(null != relativeRedirectsAllowed){ builder.setRelativeRedirectsAllowed(relativeRedirectsAllowed); }
        if(null != circularRedirectsAllowed){ builder.setCircularRedirectsAllowed(circularRedirectsAllowed); }
        if(null != maxRedirects){ builder.setMaxRedirects(maxRedirects); }
        if(null != authenticationEnabled){ builder.setAuthenticationEnabled(authenticationEnabled); }
        if(null != targetPreferredAuthSchemes){ builder.setTargetPreferredAuthSchemes(targetPreferredAuthSchemes); }
        if(null != proxyPreferredAuthSchemes){ builder.setProxyPreferredAuthSchemes(proxyPreferredAuthSchemes); }
        if(null != connectionRequestTimeout){ builder.setConnectionRequestTimeout(connectionRequestTimeout); }
        if(null != connectTimeout){ builder.setConnectTimeout(connectTimeout); }
        if(null != socketTimeout){ builder.setSocketTimeout(socketTimeout); }
        if(null != contentCompressionEnabled){ builder.setContentCompressionEnabled(contentCompressionEnabled); }
        if(null != normalizeUri){ builder.setNormalizeUri(normalizeUri); }
        return builder.build();
    }

    public RequestConfig cover(RequestConfig config) {
        RequestConfig.Builder builder = RequestConfig.copy(config);
        if(null != expectContinueEnabled){ builder.setExpectContinueEnabled(expectContinueEnabled); }
        if(null != proxy){ builder.setProxy(proxy); }
        if(null != localAddress){ builder.setLocalAddress(localAddress); }
        if(null != cookieSpec){ builder.setCookieSpec(cookieSpec); }
        if(null != redirectsEnabled){ builder.setRedirectsEnabled(redirectsEnabled); }
        if(null != relativeRedirectsAllowed){ builder.setRelativeRedirectsAllowed(relativeRedirectsAllowed); }
        if(null != circularRedirectsAllowed){ builder.setCircularRedirectsAllowed(circularRedirectsAllowed); }
        if(null != maxRedirects){ builder.setMaxRedirects(maxRedirects); }
        if(null != authenticationEnabled){ builder.setAuthenticationEnabled(authenticationEnabled); }
        if(null != targetPreferredAuthSchemes){ builder.setTargetPreferredAuthSchemes(targetPreferredAuthSchemes); }
        if(null != proxyPreferredAuthSchemes){ builder.setProxyPreferredAuthSchemes(proxyPreferredAuthSchemes); }
        if(null != connectionRequestTimeout){ builder.setConnectionRequestTimeout(connectionRequestTimeout); }
        if(null != connectTimeout){ builder.setConnectTimeout(connectTimeout); }
        if(null != socketTimeout){ builder.setSocketTimeout(socketTimeout); }
        if(null != contentCompressionEnabled){ builder.setContentCompressionEnabled(contentCompressionEnabled); }
        if(null != normalizeUri){ builder.setNormalizeUri(normalizeUri); }
        return builder.build();
    }

    public static class Builder<CHILD extends Builder> {
        private Boolean expectContinueEnabled;
        private HttpHost proxy;
        private InetAddress localAddress;
        private String cookieSpec;
        private Boolean redirectsEnabled;
        private Boolean relativeRedirectsAllowed;
        private Boolean circularRedirectsAllowed;
        private Integer maxRedirects;
        private Boolean authenticationEnabled;
        private Collection<String> targetPreferredAuthSchemes;
        private Collection<String> proxyPreferredAuthSchemes;
        private Integer connectionRequestTimeout;
        private Integer connectTimeout;
        private Integer socketTimeout;
        private Boolean contentCompressionEnabled;
        private Boolean normalizeUri;


        public CHILD setExpectContinueEnabled(Boolean expectContinueEnabled) {
            this.expectContinueEnabled = expectContinueEnabled;
            return (CHILD) this;
        }

        public CHILD setProxy(HttpHost proxy) {
            this.proxy = proxy;
            return (CHILD)this;
        }

        public CHILD setLocalAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            return (CHILD) this;
        }

        public CHILD setCookieSpec(String cookieSpec) {
            this.cookieSpec = cookieSpec;
            return (CHILD) this;
        }

        public CHILD setRedirectsEnabled(Boolean redirectsEnabled) {
            this.redirectsEnabled = redirectsEnabled;
            return (CHILD) this;
        }

        public CHILD setRelativeRedirectsAllowed(Boolean relativeRedirectsAllowed) {
            this.relativeRedirectsAllowed = relativeRedirectsAllowed;
            return (CHILD) this;
        }

        public CHILD setCircularRedirectsAllowed(Boolean circularRedirectsAllowed) {
            this.circularRedirectsAllowed = circularRedirectsAllowed;
            return (CHILD) this;
        }

        public CHILD setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
            return (CHILD) this;
        }

        public CHILD setAuthenticationEnabled(Boolean authenticationEnabled) {
            this.authenticationEnabled = authenticationEnabled;
            return (CHILD) this;
        }

        public CHILD setTargetPreferredAuthSchemes(Collection<String> targetPreferredAuthSchemes) {
            this.targetPreferredAuthSchemes = targetPreferredAuthSchemes;
            return (CHILD) this;
        }

        public CHILD setProxyPreferredAuthSchemes(Collection<String> proxyPreferredAuthSchemes) {
            this.proxyPreferredAuthSchemes = proxyPreferredAuthSchemes;
            return (CHILD) this;
        }

        public CHILD setConnectionRequestTimeout(Integer connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return (CHILD) this;
        }

        public CHILD setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return (CHILD) this;
        }

        public CHILD setSocketTimeout(Integer socketTimeout) {
            this.socketTimeout = socketTimeout;
            return (CHILD) this;
        }

        public CHILD setContentCompressionEnabled(Boolean contentCompressionEnabled) {
            this.contentCompressionEnabled = contentCompressionEnabled;
            return (CHILD) this;
        }

        public CHILD setNormalizeUri(Boolean normalizeUri) {
            this.normalizeUri = normalizeUri;
            return (CHILD) this;
        }

        public CoverableRequestConfig build() {
            return new CoverableRequestConfig(this.expectContinueEnabled, this.proxy, this.localAddress, this.cookieSpec, this.redirectsEnabled, this.relativeRedirectsAllowed, this.circularRedirectsAllowed, this.maxRedirects, this.authenticationEnabled, this.targetPreferredAuthSchemes, this.proxyPreferredAuthSchemes, this.connectionRequestTimeout, this.connectTimeout, this.socketTimeout, this.contentCompressionEnabled, this.normalizeUri);
        }
    }


}
