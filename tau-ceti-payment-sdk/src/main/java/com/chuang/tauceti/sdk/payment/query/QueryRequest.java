package com.chuang.tauceti.sdk.payment.query;

import com.chuang.tauceti.support.enums.PaymentType;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Optional;

public class QueryRequest {
    private String reference;


    private @Nullable
    String providerReference;
    private Date createDate;

    private PaymentType type;


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public Optional<String> getProviderReference() {
        return Optional.ofNullable(providerReference);
    }

    public void setProviderReference(@Nullable String providerReference) {
        this.providerReference = providerReference;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public static QueryRequest create(String reference, Date createDate, PaymentType type, @Nullable String providerReference) {
        QueryRequest info = new QueryRequest();
        info.type = type;
        info.providerReference = providerReference;
        info.setReference(reference);
        info.setCreateDate(createDate);
        return info;
    }

}
