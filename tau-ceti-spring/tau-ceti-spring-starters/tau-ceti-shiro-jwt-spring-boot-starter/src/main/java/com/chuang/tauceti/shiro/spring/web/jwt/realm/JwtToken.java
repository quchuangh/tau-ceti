package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtPayload;
import lombok.Data;
import lombok.Getter;
import org.apache.shiro.authc.AuthenticationToken;

public class JwtToken implements AuthenticationToken {

    @Getter
    private final JwtPayload payload;
    @Getter
    private final String token;

    public JwtToken(JwtPayload payload, String token) {
        this.payload = payload;
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return payload;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
