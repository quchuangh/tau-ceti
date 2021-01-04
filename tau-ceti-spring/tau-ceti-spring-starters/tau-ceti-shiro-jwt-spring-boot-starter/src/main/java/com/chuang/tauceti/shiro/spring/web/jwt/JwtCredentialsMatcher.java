package com.chuang.tauceti.shiro.spring.web.jwt;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

public class JwtCredentialsMatcher implements CredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        JwtPayload payload = (JwtPayload)token.getPrincipal();
        if(payload.expired()) {
            throw new AuthenticationException("token 已经过期");
        }
        return true;
    }
}
