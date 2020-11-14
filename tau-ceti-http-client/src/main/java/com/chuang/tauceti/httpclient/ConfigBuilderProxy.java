package com.chuang.tauceti.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;


/**
 * RequestConfig的代理类.
 * @param <T>
 */
@NotThreadSafe
public abstract class ConfigBuilderProxy<T extends ConfigBuilderProxy<T, H>, H> {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected String defaultCharset = "UTF-8";


    // =================== SSL 相关 +===========================
    private String trustStoreType;
    private InputStream trustKeyStoreIn;
    private String trustKeyStorePass;

    private String privateStoreType;
    private InputStream privateKeyStoreIn;
    private String privateKeyStorePass;

    protected String protocol;
    protected String[] sslVersion;

    private boolean isTrustAll;

    protected boolean ignoreSSLCert;


    public T useTLS() {
        this.protocol = "TLS";
        //noinspection unchecked
        return (T) this;
    }

    public T trustAll(){
        this.isTrustAll = true;
        //noinspection unchecked
        return (T) this;
    }

    public T ignoreSSLCert() {
        this.ignoreSSLCert = true;
        //noinspection unchecked
        return (T) this;
    }

    public T useSSL() {
        this.protocol = "SSL";
        //noinspection unchecked
        return (T) this;
    }
    public T setDefaultCharset(final String defaultCharset) {
        this.defaultCharset = defaultCharset;
        //noinspection unchecked
        return (T) this;
    }

    public T setSupportSSLVersion(final String... sslVersion) {
        this.sslVersion = sslVersion;
        //noinspection unchecked
        return (T) this;
    }


    public T setTrustKeyStore(final String trustStoreType, final InputStream trustKeyStoreIn, final String trustKeyStorePass) {
        this.trustKeyStorePass = trustKeyStorePass;
        this.trustKeyStoreIn = trustKeyStoreIn;
        this.trustStoreType = trustStoreType;
        //noinspection unchecked
        return (T) this;
    }

    public T setTrustKeyStore(final InputStream trustKeyStoreIn, final String trustKeyStorePass) {
        return setTrustKeyStore(KeyStore.getDefaultType(), trustKeyStoreIn, trustKeyStorePass);
    }

    public T setPrivateKeyStore(final String privateStoreType, final InputStream privateKeyStoreIn, final String privateKeyStorePass) {
        this.privateKeyStorePass = privateKeyStorePass;
        this.privateStoreType = privateStoreType;
        this.privateKeyStoreIn = privateKeyStoreIn;
        //noinspection unchecked
        return (T) this;
    }

    // ================= request config builder =================
    protected final RequestConfig.Builder configBuilder = RequestConfig.custom().setRedirectsEnabled(false);

    public T setExpectContinueEnabled(final boolean expectContinueEnabled) {
        configBuilder.setExpectContinueEnabled(expectContinueEnabled);
        //noinspection unchecked
        return (T) this;
    }

    public T setProxy(final HttpHost proxy) {
        configBuilder.setProxy(proxy);
        //noinspection unchecked
        return (T) this;
    }

    public T setLocalAddress(final InetAddress localAddress) {
        configBuilder.setLocalAddress(localAddress);
        //noinspection unchecked
        return (T) this;
    }

//    public T setStaleConnectionCheckEnabled(final boolean staleConnectionCheckEnabled) {
//        configBuilder.setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
//        return (T) this;
//    }

    public T setCookieSpec(final String cookieSpec) {
        configBuilder.setCookieSpec(cookieSpec);
        //noinspection unchecked
        return (T) this;
    }

    public T setRedirectsEnabled(final boolean redirectsEnabled) {
        configBuilder.setRedirectsEnabled(redirectsEnabled);
        //noinspection unchecked
        return (T) this;
    }

    public T setRelativeRedirectsAllowed(final boolean relativeRedirectsAllowed) {
        configBuilder.setRelativeRedirectsAllowed(relativeRedirectsAllowed);
        //noinspection unchecked
        return (T) this;
    }

    public T setCircularRedirectsAllowed(final boolean circularRedirectsAllowed) {
        configBuilder.setCircularRedirectsAllowed(circularRedirectsAllowed);
        //noinspection unchecked
        return (T) this;
    }

    public T setMaxRedirects(final int maxRedirects) {
        configBuilder.setMaxRedirects( maxRedirects);
        //noinspection unchecked
        return (T) this;
    }

    public T setAuthenticationEnabled(final boolean authenticationEnabled) {
        configBuilder.setAuthenticationEnabled(authenticationEnabled);
        //noinspection unchecked
        return (T) this;
    }

    public T setTargetPreferredAuthSchemes(final Collection<String> targetPreferredAuthSchemes) {
        configBuilder.setTargetPreferredAuthSchemes(targetPreferredAuthSchemes);
        //noinspection unchecked
        return (T) this;
    }

    public T setProxyPreferredAuthSchemes(final Collection<String> proxyPreferredAuthSchemes) {
        configBuilder.setProxyPreferredAuthSchemes(proxyPreferredAuthSchemes);
        //noinspection unchecked
        return (T) this;
    }

    public T setConnectionRequestTimeout(final int connectionRequestTimeout) {
        configBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
        //noinspection unchecked
        return (T) this;
    }

    public T setConnectTimeout(final int connectTimeout) {
        configBuilder.setConnectTimeout(connectTimeout);
        //noinspection unchecked
        return (T) this;
    }

    public T setSocketTimeout(final int socketTimeout) {
        configBuilder.setSocketTimeout(socketTimeout);
        //noinspection unchecked
        return (T) this;
    }

    public H build() {
        try {
            KeyStore trustStore = null;
            KeyStore privateStore = null;

            if(!Tools.isBlank(trustStoreType)) {
                trustStore = KeyStore.getInstance(trustStoreType);
                trustStore.load(trustKeyStoreIn, trustKeyStorePass.toCharArray());
            }
            if(!Tools.isBlank(privateStoreType)) {
                privateStore = KeyStore.getInstance(privateStoreType);
                privateStore.load(privateKeyStoreIn, privateKeyStorePass.toCharArray());
            }
            return build(trustStore, privateStore, privateKeyStorePass);
        } catch (UnrecoverableKeyException | KeyManagementException | KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("SSL 构建异常", e);

            return null;
        }
    }

    public H build(KeyStore trustStore, KeyStore privateStore, String privateKeyStorePass) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        //创建trustmanager
        final TrustManager[] tms;
        if(trustStore != null) {
            final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmfactory.init(trustStore);
            tms = tmfactory.getTrustManagers();
            if (tms != null) {
                for (int i = 0; i < tms.length; i++) {
                    final TrustManager tm = tms[i];
                    if (tm instanceof X509TrustManager) {
                        tms[i] = new TrustSelfManager((X509TrustManager) tm);
                    }
                }
            }
        } else if(ignoreSSLCert) {
            tms = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) {
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
        } else if(isTrustAll) {
            tms = new TrustManager[] { new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) {

                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
        } else {
            tms = null;
        }

        //创建keymanager
        final KeyManager[] kms;
        if(privateStore != null) {
            final KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmfactory.init(privateStore, privateKeyStorePass.toCharArray());
            kms =  kmfactory.getKeyManagers();
        } else {
            kms = null;
        }

        return build(kms, tms, null);
    }


    public abstract H build(KeyManager[] keymanagers, TrustManager[] trustmanagers, SecureRandom secureRandom) throws KeyManagementException, NoSuchAlgorithmException;

    static class TrustSelfManager implements X509TrustManager {

        private final X509TrustManager trustManager;

        TrustSelfManager(final X509TrustManager trustManager) {
            super();
            this.trustManager = trustManager;
        }

        public void checkClientTrusted(
                final X509Certificate[] chain, final String authType) throws CertificateException {
            this.trustManager.checkClientTrusted(chain, authType);
        }

        public void checkServerTrusted(
                final X509Certificate[] chain, final String authType) throws CertificateException {
            if (chain.length != 1) {
                this.trustManager.checkServerTrusted(chain, authType);
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return this.trustManager.getAcceptedIssuers();
        }

    }

}
