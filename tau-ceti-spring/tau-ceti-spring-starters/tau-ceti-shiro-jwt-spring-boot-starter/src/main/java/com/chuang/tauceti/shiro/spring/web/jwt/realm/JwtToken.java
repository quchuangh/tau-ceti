package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

@Data
public class JwtToken implements AuthenticationToken {

    private final String token;
    private final String secret;

    public JwtToken(String tokenString, String secret) {
        this.token = tokenString;
        this.secret = secret;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return secret;
    }
}
