package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtManager;
import com.chuang.tauceti.shiro.spring.web.jwt.JwtPayload;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class JwtRealm extends AuthorizingRealm {

    private final IAuthService authService;
    private final JwtManager manager;

    public JwtRealm(JwtManager manager, IAuthService authService) {
        this.manager = manager;
        this.authService = authService;
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
        return authService.getAuthorizationInfo(principals);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;

        JwtPayload payload;
        try {
            payload = manager.parse(jwtToken.getToken());
        } catch (ClassNotFoundException e) {
            throw new AuthenticationException("JWT Token解码异常", e);
        }
        return new SimpleAuthenticationInfo(payload.getBody(), payload, getName());
    }
}
