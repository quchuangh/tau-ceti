package com.chuang.tauceti.shiro.spring.web.jwt;

public interface JwtPayloadConvert{

    JwtPayload convert(String payloadJsonString);
}
