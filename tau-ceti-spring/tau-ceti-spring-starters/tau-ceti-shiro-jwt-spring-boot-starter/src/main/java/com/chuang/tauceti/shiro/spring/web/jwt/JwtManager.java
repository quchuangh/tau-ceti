package com.chuang.tauceti.shiro.spring.web.jwt;

import com.alibaba.fastjson.JSONObject;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.JwtProperties;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.tools.basic.StringKit;
import com.chuang.tauceti.tools.third.servlet.HttpKit;
import com.nimbusds.jose.JOSEException;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

public class JwtManager {

    private final JwtProperties properties;

    public JwtManager(JwtProperties properties) {
        this.properties = properties;
    }


    public Optional<String> requestToken() {
        HttpServletRequest request = HttpKit.getRequest().orElseThrow(() -> new IllegalArgumentException("无法从request中获取Jwt，原因：request为空"));
        // 从请求头中获取token
        String token = request.getHeader(properties.getTokenHeader());
        if (StringKit.isBlank(token)) {
            // 从请求参数中获取token
            token = request.getParameter(properties.getTokenHeader());
        }
        return Optional.ofNullable(token);
    }

    public JwtPayload makePayload(String aud, Object body) {
        long now = System.currentTimeMillis();
        return JwtPayload.builder()
                .aud(aud)
                .iss(properties.getIssuer())
                .exp(now + properties.getExpire())
                .sub(properties.getSubject())
                .nbf(now)
                .iat(now)
                .jti(UUID.randomUUID().toString())
                .body(body)
                .bodyClass(body.getClass().getName())
                .build();
    }

    public void refresh(JwtPayload payload) {
        payload.setExp(System.currentTimeMillis() + properties.getExpire());
    }

    public String makeToken(String aud, Object body) throws JOSEException {
        return makeToken(makePayload(aud, body));
    }

    public String makeToken(JwtPayload payload) throws JOSEException {
        return JwtUtil.generateToken(JSONObject.toJSONString(payload), properties.getSecret());
    }

    public JwtPayload parse(String token) throws ClassNotFoundException {
        String payloadStr;
        try {
            payloadStr = JwtUtil.verifySignature(token, properties.getSecret());
        } catch (JOSEException | JwtUtil.JwtSignatureVerifyException | ParseException e) {
            throw new BusinessException("jwt 解码错误", e);
        }
        return parse(JSONObject.parseObject(payloadStr));
    }

    private JwtPayload parse(JSONObject json) throws ClassNotFoundException {
        JSONObject bodyJson = json.getJSONObject("body");
        String bodyClass = json.getString("bodyClass");

        Object body = bodyJson.toJavaObject(Class.forName(bodyClass));
        return JwtPayload.builder()
                .aud(json.getString("aud"))
                .iss(json.getString("iss"))
                .exp(json.getLong("exp"))
                .sub(json.getString("sub"))
                .nbf(json.getLong("nbf"))
                .iat(json.getLong("iat"))
                .jti(json.getString("jti"))
                .bodyClass(bodyClass)
                .body(body)
                .build();
    }
}
