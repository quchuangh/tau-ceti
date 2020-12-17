package com.chuang.tauceti.sdk.payment;

import com.chuang.tauceti.sdk.payment.deposit.DepositCallbackInfo;
import com.chuang.tauceti.sdk.payment.deposit.DepositInfo;
import com.chuang.tauceti.sdk.payment.deposit.DepositRequest;
import com.chuang.tauceti.sdk.payment.deposit.GenFormDepositInfo;
import com.chuang.tauceti.sdk.payment.query.QueryInfo;
import com.chuang.tauceti.sdk.payment.query.QueryRequest;
import com.chuang.tauceti.sdk.payment.withdraw.WithdrawCallbackInfo;
import com.chuang.tauceti.sdk.payment.withdraw.WithdrawInfo;
import com.chuang.tauceti.sdk.payment.withdraw.WithdrawRequest;
import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.enums.Bank;
import com.chuang.tauceti.support.enums.PaymentType;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.tools.basic.FutureKit;
import com.chuang.tauceti.tools.basic.StringKit;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 支付平台接口定义
 */
public interface IPaymentPlatform {

    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    BigDecimal _100 = new BigDecimal("100");
    default BigDecimal toYuan(Long amount) {
        return new BigDecimal(amount.toString()).divide(_100, 2, RoundingMode.FLOOR);
    }
    /**
     * 这是一个忽略 SSL 校验的 httpclient。
     * 对应的创建代码为: .ignoreSSLCert() ,它的含义和 .trustAll() 是不同的。
     * trustAll是信任所有来源，而ignoreSSLCert则是完全忽略（某些接口 https 并没有证书，使用trustAll会无法工作，因此需要进行忽略）。
     */
//    AsyncHttpClient ignoreSSLClient = Https.async().setDefaultCharset("UTF-8")
//            .setConnectTimeout(10000)
//            .setSocketTimeout(10000)
//            .setConnectionRequestTimeout(10000)
//            .ignoreSSLCert()
//            .maxConnectEachHost(5)
//            .build()
//            .init();
    /**
     * 通过api路径和参数生成form
     */
    default CompletableFuture<Result<DepositInfo>> toForm(String apiPath, String method,
                                                          Long amount,
                                                          String reference,
                                                          Map<String, String> params,
                                                          PaymentPlatformConfig config) {
        DepositInfo result = GenFormDepositInfo.form(proxy(config), config.getMerchantId(), config.getApiUrl() + apiPath, method, amount, reference, params);
        logger.info("generate html: -> " + result.getContent());
        Result<DepositInfo> r = Result.success();
        return CompletableFuture.completedFuture(r.data(result));
    }

    default HttpHost proxy(PaymentPlatformConfig config) {
        if(StringKit.isBlank(config.getProxyUrl())) {
            return null;
        } else {
            String[] h = config.getProxyUrl().split(":");
            return new HttpHost(h[0], Integer.parseInt(h[1]));
        }
    }

    /**
     * 获取
     * 银行枚举转换成对应平台的编号字符串。
     * 的映射
     */
    Map<Bank, String> bankCodeMapping();
    /**
     * 获取
     * 支付类型枚举转换成对应平台的支付类型标识字符串
     * 的映射。
     */
    Map<PaymentType, String> paymentTypeCodeMapping();

    /**
     * 存款
     */
    CompletableFuture<Result<DepositInfo>> deposit(DepositRequest request, PaymentPlatformConfig config);

    default CompletableFuture<Result<QueryInfo>> query(QueryRequest info, PaymentPlatformConfig config) {
        return FutureKit.error(new BusinessException(config.getPlatform() + " do not support query status"));
    }

    /**
     * 存款回调
     */
    Result<DepositCallbackInfo> depositCallback(Map<String, String> callbackParams, String body, boolean front, PaymentPlatformConfig config);

    default CompletableFuture<Result<WithdrawInfo>> withdraw(WithdrawRequest request, PaymentPlatformConfig config) {
        throw new BusinessException("withdraw not support");
    }

    default Result<WithdrawCallbackInfo> withdrawCallback(Map<String, String> callbackParams, String body, PaymentPlatformConfig config) {
        throw new BusinessException("withdraw callback not support");
    }


    default Set<Bank> supportBanks() {
        return bankCodeMapping().keySet();
    }

    default Set<PaymentType> supportPaymentTypes() {
        return paymentTypeCodeMapping().keySet();
    }

    default boolean isSupport(PaymentType type, @Nullable Bank bank) {
        return paymentTypeCodeMapping().containsKey(type) && (null != bank && bankCodeMapping().containsKey(bank));
    }
}
