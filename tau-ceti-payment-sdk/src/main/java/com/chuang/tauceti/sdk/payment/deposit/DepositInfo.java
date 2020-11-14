package com.chuang.tauceti.sdk.payment.deposit;


import com.chuang.tauceti.support.Result;

import javax.annotation.Nullable;
import java.util.Optional;

public class DepositInfo {

    private final Type type;

    private final String merchantId;

    private final String content;

    private final Long amount;

    private final String reference;

    private final @Nullable
    String providerReference;

    public DepositInfo(Type type,
                       String merchantId,
                       String content,
                       Long amount,
                       String reference,
                       @Nullable String providerReference) {
        this.type = type;
        this.merchantId = merchantId;
        this.content = content;
        this.amount = amount;
        this.reference = reference;
        this.providerReference = providerReference;
    }

    public DepositInfo(Type type,
                       String merchantId,
                       String content,
                       Long amount,
                       String reference) {
        this(type, merchantId, content, amount, reference, null);
    }

    public Type getType() {
        return type;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getContent() {
        return content;
    }

    public Long getAmount() {
        return amount;
    }

    public Optional<String> getProviderReference() {
        return Optional.ofNullable(providerReference);
    }

    public String getReference() {
        return reference;
    }

    public enum Type {
        FORM_DOC, URL, QR_CODE_URL, BANK_INFO
    }

    public static Result<DepositInfo> parse(boolean success,
                                            String message,
                                            Type type,
                                            String merchantId,
                                            String content,
                                            Long amount,
                                            String reference) {
        return parse(success, message, type, merchantId, content, amount, reference, null);
    }

    public static Result<DepositInfo> parse(boolean success,
                                            String message,
                                            Type type,
                                            String merchantId,
                                            String content,
                                            Long amount,
                                            String reference,
                                            @Nullable String providerReference) {
        Result<DepositInfo> result = Result.whether(success);

        return result.message(message)
                .data(new DepositInfo(
                   type, merchantId, content, amount, reference, providerReference
                ));
    }
}

