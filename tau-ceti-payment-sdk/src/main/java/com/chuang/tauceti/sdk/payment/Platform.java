package com.chuang.tauceti.sdk.payment;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Platform {
    String value();
}
