package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import com.chuang.tauceti.support.BiValue;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 * 密码校验
 */
public class LoginRealm extends AuthorizingRealm {
    private final IRealmService realmService;

    public LoginRealm(IRealmService realmService) {
        this.realmService = realmService;
    }

    @Override
    public Class<?> getAuthenticationTokenClass() {
        return LoginToken.class;
    }

    @Override
    public String getName() {
        return "login-realm";
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return realmService.getAuthenticationInfoByLoginToken((LoginToken) token);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return realmService.getAuthorizationInfo(principals);
    }


}
