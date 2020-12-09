package com.chuang.tauceti.shiro.spring.web.jwt.realm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.UsernamePasswordToken;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginToken extends UsernamePasswordToken {

    private String referer;
    private String userAgent;

    public LoginToken(String username, String password) {
        super(username, password);
    }

}
