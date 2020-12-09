package com.chuang.tauceti.shiro.spring.web.jwt.configuration;

import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Import;


@Import(ShiroJwtConfiguration.class)
@AutoConfigureBefore(ShiroWebAutoConfiguration.class)
public class ShiroJwtAutoConfiguration {
}
