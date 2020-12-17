package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class LoginRealm extends AuthorizingRealm {
    private final IAuthService realmService;

    public LoginRealm(IAuthService realmService) {
        this.realmService = realmService;
    }

    @Override
    public String getName() {
        return "login-realm";
    }

    public boolean supports(AuthenticationToken token) {
        return token instanceof LoginToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return realmService.getAuthorizationInfo(principals);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return realmService.getAuthenticationInfoByLoginToken((LoginToken) token, getName());
    }
}
