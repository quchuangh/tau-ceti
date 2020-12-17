package com.chuang.tauceti.shiro.spring.web.jwt;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
@Builder
public class JwtPayload {

    /** 签发人(issuer) 一般可以是自己的网站域名，也可以设置为请求人自己 */
    @Getter
    private final String iss;

    /** 过期时间 */
    @Getter
    @Setter
    private Long exp;

    /** 主题 */
    @Getter
    private final String sub;

    /** 受众（audience）接受方，通常设定为请求jwt的用户或网站(如api调用时就可以是网站) */
    @Getter
    private final String aud;

    /** 生效时间 设定token在这个时间之前无法被使用 */
    @Getter
    private final Long nbf;

    /** 签发时间 */
    @Getter
    private final Long iat;

    /** JWT的ID */
    @Getter
    private final String jti;

    @Getter
    private final Object body;

    @Getter
    private final String bodyClass;

    public boolean expired() {
        return System.currentTimeMillis() > exp;
    }

}
