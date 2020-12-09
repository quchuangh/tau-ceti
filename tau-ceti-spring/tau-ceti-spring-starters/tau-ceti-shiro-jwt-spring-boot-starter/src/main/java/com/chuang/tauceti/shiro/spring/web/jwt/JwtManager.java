package com.chuang.tauceti.shiro.spring.web.jwt;

import com.alibaba.fastjson.JSONObject;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.JwtProperties;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.tools.basic.StringKit;
import com.chuang.tauceti.tools.third.servlet.HttpKit;
import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public class JwtManager {

    private final JwtProperties properties;
    private final JwtPayloadConvert convert;

    public JwtManager(JwtProperties properties, JwtPayloadConvert convert) {
        this.properties = properties;
        this.convert = convert;
    }



    public JwtPayload payload() {
        String token = token();
        if(StringKit.isBlank(token)) {
            throw new BusinessException(-1, "Jwt 为空");
        }
        String payloadStr;
        try {
            payloadStr = JwtUtil.verifySignature(token(), properties.getSecret());
        } catch (JOSEException | JwtUtil.JwtSignatureVerifyException | ParseException e) {
            throw new BusinessException(-1, "jwt 解码错误", e);
        }
        return convert.convert(payloadStr);
    }

    public void refresh(JwtPayload payload) {
        payload.setExp(properties.getExpire());
    }

    public String token(JwtPayload payload) throws JOSEException {
        return JwtUtil.generateToken(JSONObject.toJSONString(payload), properties.getSecret());
    }

    public String token() {
        HttpServletRequest request = HttpKit.getRequest().orElseThrow(() -> new IllegalArgumentException("无法从request中获取Jwt，原因：request为空"));
        // 从请求头中获取token
        String token = request.getHeader(properties.getTokenHeader());
        if (StringKit.isBlank(token)) {
            // 从请求参数中获取token
            token = request.getParameter(properties.getTokenHeader());
        }
        return token;
    }

}
