package com.chuang.tauceti.shiro.spring.web.jwt;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class JwtPayload implements AuthenticationToken {

    /** 签发人(issuer) 一般可以是自己的网站域名，也可以设置为请求人自己 */
    private String iss;
    /** 过期时间 */
    private Long exp;
    /** 主题 */
    private String sub;
    /** 受众（audience）接受方，通常设定为请求jwt的用户或网站(如api调用时就可以是网站) */
    private String aud;
    /** 生效时间 设定token在这个时间之前无法被使用 */
    private Long nbf;
    /** 签发时间 */
    private Long iat;
    /** JWT的ID */
    private String jti;

    private Object principal;

    private Object credentials;

    public boolean expired() {
        return System.currentTimeMillis() > exp;
    }

}
