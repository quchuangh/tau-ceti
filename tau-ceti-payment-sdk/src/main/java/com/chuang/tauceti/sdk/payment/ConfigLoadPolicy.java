package com.chuang.tauceti.sdk.payment;

import java.util.Optional;

/**
 * @author Ath
 */
public interface ConfigLoadPolicy {

    Optional<PaymentPlatformConfig> loadConfig(PaymentRequest request, PaymentSDK sdk);

    Optional<PaymentPlatformConfig> loadConfig(String key);
}
