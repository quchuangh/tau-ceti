package com.chuang.tauceti.shiro.spring.web.jwt.filter;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtManager;
import com.chuang.tauceti.shiro.spring.web.jwt.JwtPayload;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtFilter extends AuthenticatingFilter {

    private final JwtManager manager;
    private final JwtProperties properties;

    public JwtFilter(JwtManager manager, JwtProperties properties) {
        this.manager = manager;
        this.properties = properties;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {


        JwtPayload payload = manager.payload();

        if (payload.expired()) {
            throw new AuthenticationException("JWT Token已过期,token:" + payload);
        }

//        // 如果开启redis二次校验，或者设置为单个用户token登录，则先在redis中判断token是否存在
//        if (properties.isRedisCheck() || properties.isSingleLogin()) {
//            boolean redisExpired = loginRedisService.exists(token);
//            if (!redisExpired) {
//                throw new AuthenticationException("Redis Token不存在,token:" + token);
//            }
//        }

        return payload;
    }

    /**
     * 访问失败处理
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
//        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
//        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
//        // 返回401
//        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        // 设置响应码为401或者直接输出消息
//        String url = httpServletRequest.getRequestURI();
//        log.error("onAccessDenied url：{}", url);
//        ApiResult<Boolean> apiResult = ApiResult.fail(ApiCode.UNAUTHORIZED);
//        HttpServletResponseUtil.printJson(httpServletResponse, apiResult);
//        return false;
        throw new UnauthenticatedException("验证失败");
    }

    /**
     * 判断是否允许访问
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("isAccessAllowed url:{}", url);
        if (this.isLoginRequest(request, response)) {
            return true;
        }
        boolean allowed = false;
        try {
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) { //not found any token
            log.error("Token不能为空", e);
        } catch (Exception e) {
            log.error("访问错误", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 登录成功处理
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("鉴权成功, token:{}, url:{}", token, url);
        JwtPayload payload = (JwtPayload) token;
        // 刷新token
        if(properties.isAutoRefresh() && (payload.getExp() - System.currentTimeMillis()) < properties.getRefreshCountdown()) {
            manager.refresh(payload);
            String newToken = manager.token(payload);
            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
            httpServletResponse.setStatus(properties.getJwtRefreshStatus());
            httpServletResponse.setHeader(properties.getTokenHeader(), newToken);
        }

        return true;
    }

    /**
     * 登录失败处理
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.error("登录失败，token:" + token + ",error:" + e.getMessage(), e);
        return false;
    }

}
