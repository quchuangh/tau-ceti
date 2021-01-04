package com.chuang.tauceti.shiro.spring.web.jwt.filter;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtManager;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.ShiroProperties;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.IShiroService;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.LoginToken;
import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import com.chuang.tauceti.tools.third.servlet.HttpKit;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class PasswordFilter extends AuthenticatingFilter {

    private final JwtManager manager;
    private final ShiroProperties properties;
    private final IShiroService authService;

    public PasswordFilter(JwtManager manager, ShiroProperties properties, IShiroService authService) {
        this.manager = manager;
        this.properties = properties;
        this.authService = authService;
        setLoginUrl(properties.getLoginUrl());
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        return HttpKit.getRequest().map(request -> {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String referer = request.getHeader(HttpHeaders.REFERER);
            String ua = request.getHeader(HttpHeaders.USER_AGENT);
            if(CollectionKit.isAnyEmpty(username, password)) {
                throw new AuthenticationException("username or password can not be null");
            }
            LoginToken token = new LoginToken(username, password);
            token.setReferer(referer);
            token.setUserAgent(ua);
            return token;
        }).orElseThrow(() -> new AuthenticationException("无法获取参数"));
    }

    /**
     * 当 isAccessAllowed（检查是否登录） 如果没有登录
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        // 如果不是登录请求或者不委托登录请求，则放过去
        if(!this.isLoginRequest(request, response) || !properties.isLoginHosting()) {
            return true;
        }
        try {
            return executeLogin(request, response);
        } catch (Exception e) {
            printError(response, new AuthenticationException(e));
        }
        return false;
    }


    /**
     * 登录成功处理
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        authService.loginSuccess((LoginToken) token);
        Object principal = subject.getPrincipal();

        String jwtToken = manager.makeToken(((LoginToken) token).getUsername(), principal);

        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(properties.getJwt().getRefreshStatus());
        httpResponse.setHeader(properties.getJwt().getTokenHeader(), jwtToken);
        HttpKit.printJson(Result.success(jwtToken));

        return false;
    }

    /**
     * 登录失败处理
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        authService.loginFailure((LoginToken) token, e);
        printError(response, e);
        return false;
    }

    private void printError(ServletResponse response, Exception e) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // 返回200
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        // 设置响应码为401或者直接输出消息
        Result<Boolean> apiResult = Result.fail(e.getMessage());
        HttpKit.printJson(apiResult);
    }
}
