package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

public interface IAuthService {

    /**
     * 根据用户名查找对象
     * @param token 登录
     */
    AuthenticationInfo getAuthenticationInfoByLoginToken(LoginToken token, String realmName);

    AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals);

    void loginSuccess(LoginToken token);

    void loginFailure(LoginToken token, AuthenticationException e);
}
