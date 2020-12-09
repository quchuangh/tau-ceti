package com.chuang.tauceti.shiro.spring.web.jwt.properties;

import lombok.Data;

@Data
public class JwtProperties {

    private int jwtRefreshStatus = 460;

    private String tokenHeader;

    private String secret;

    private long expire = 3600000;

    private String issuer;

    private String subject;

    private long refreshCountdown = 600000;

    private boolean autoRefresh = true;
}
