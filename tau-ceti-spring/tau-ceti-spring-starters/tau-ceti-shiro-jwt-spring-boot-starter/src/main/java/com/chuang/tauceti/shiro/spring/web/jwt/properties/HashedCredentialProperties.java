package com.chuang.tauceti.shiro.spring.web.jwt.properties;

import lombok.Data;

@Data
public class HashedCredentialProperties {
    private String algorithm = "MD5";
    private int iterations = 3;
    private int saltLen = 6;

}
