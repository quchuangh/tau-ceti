package com.chuang.tauceti.shiro.spring.web.jwt.properties;

import lombok.Data;

import java.util.UUID;

@Data
public class JwtProperties {

    private int refreshStatus = 460;

    private String tokenHeader = "Authorization";

    private String secret = UUID.randomUUID().toString();

    private long expire = 3600000;

    private String issuer;

    /** 主题 */
    private String subject;

    /** 刷新时机，设定到期时间小于这个值时token才会刷新 */
    private long refreshCountdown = 600000;

    private boolean autoRefresh = true;
}
