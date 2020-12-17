package com.chuang.tauceti.shiro.spring.web.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {

    private boolean enabled = true;

    private String loginUrl = "/login";

    private String successUrl = "/";

    private String unauthorizedUrl;

    private boolean loginHosting = true;

    /**
     * 设置无需权限路径集合
     */
    private List<String[]> anon;

    private LinkedHashMap<String, String> filterChainDefinitions;

    @NestedConfigurationProperty
    private HashedCredentialProperties hashedCredential = new HashedCredentialProperties();

    @NestedConfigurationProperty
    private JwtProperties jwt = new JwtProperties();
}
