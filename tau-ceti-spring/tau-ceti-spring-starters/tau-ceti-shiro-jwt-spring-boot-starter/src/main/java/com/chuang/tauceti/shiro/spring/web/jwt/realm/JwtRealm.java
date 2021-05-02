package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class JwtRealm extends AuthorizingRealm {

    private final IShiroService shiroService;

    public JwtRealm(IShiroService shiroService) {
        this.shiroService = shiroService;
    }

    @Override
    public String getName() {
        return "jwt-realm";
    }

    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return shiroService.getAuthorizationInfo(principals);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
        return new SimpleAuthenticationInfo(jwtToken.getPayload(), token.getCredentials(), getName());
    }
}
