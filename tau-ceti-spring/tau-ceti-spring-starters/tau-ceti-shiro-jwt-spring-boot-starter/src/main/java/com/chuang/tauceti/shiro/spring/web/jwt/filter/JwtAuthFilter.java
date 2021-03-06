package com.chuang.tauceti.shiro.spring.web.jwt.filter;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtManager;
import com.chuang.tauceti.shiro.spring.web.jwt.JwtPayload;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.ShiroProperties;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.JwtToken;
import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.tools.third.servlet.HttpKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtAuthFilter extends AuthenticatingFilter {

    public static final String JWT_PAYLOAD_REQUEST_ATTR = "__JWT_PAYLOAD__";
    public static final String JWT_STR_REQUEST_ATTR = "__JWT_TOKEN_STR__";

    private final JwtManager manager;
    private final ShiroProperties properties;

    public JwtAuthFilter(JwtManager manager, ShiroProperties properties) {
        this.manager = manager;
        this.properties = properties;
        setLoginUrl(properties.getLoginUrl());
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        String tokenStr = manager.requestToken()
                .orElseThrow(() -> new AuthenticationException("[" + WebUtils.toHttp(servletRequest).getRequestURI() + "] can not find request jwt"));
        if(tokenStr.startsWith("Bearer ")) {
            tokenStr = tokenStr.substring("Bearer ".length());
        }
        JwtPayload payload;
        try {
            payload = manager.parse(tokenStr);
        } catch (ClassNotFoundException e) {
            throw new AuthenticationException(e);
        }

        servletRequest.setAttribute(JWT_PAYLOAD_REQUEST_ATTR, payload);
        servletRequest.setAttribute(JWT_STR_REQUEST_ATTR, tokenStr);
        return new JwtToken(payload, tokenStr);
    }

    /**
     * ??? isAccessAllowed???????????????????????? ??????????????????
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("isAccessAllowed url:{}", url);
        if (this.isLoginRequest(request, response)) {//?????????????????????????????????
            return true;
        }
        boolean allowed = false;
        try {
            allowed = executeLogin(request, response); //??????????????? createToken ??????????????????JwtToken??????
        } catch (IllegalStateException e) { //not found any token
            printError(response, new AuthenticationException("token ????????????"));
        } catch (AuthenticationException e) {
            printError(response, e);
        } catch (Exception e) {
            printError(response, new AuthenticationException("????????????"));
        }
        return allowed;

    }


    /**
     * ??????????????????
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("????????????, token:{}, url:{}", token.getCredentials(), url);

        JwtPayload payload = ((JwtToken) token).getPayload();
        // ??????token
        if(properties.getJwt().isAutoRefresh() && (payload.getExp() - System.currentTimeMillis()) < properties.getJwt().getRefreshCountdown()) {
            manager.refresh(payload);
            String newToken = manager.makeToken(payload);
            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
            httpServletResponse.setStatus(properties.getJwt().getRefreshStatus());
            httpServletResponse.setHeader(properties.getJwt().getTokenHeader(), newToken);
        }

        return true;
    }

    /**
     * ??????????????????
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        printError(response, e);
        return false;
    }

    private void printError(ServletResponse response, Exception e) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // ??????401
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // ??????????????????401????????????????????????
        Result<Boolean> apiResult = Result.fail("????????????????????????");
        HttpKit.printJson(apiResult);
    }
}
