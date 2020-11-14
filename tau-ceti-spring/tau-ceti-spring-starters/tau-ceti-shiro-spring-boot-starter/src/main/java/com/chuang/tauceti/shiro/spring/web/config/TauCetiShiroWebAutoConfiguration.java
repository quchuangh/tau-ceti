package com.chuang.tauceti.shiro.spring.web.config;

import com.chuang.tauceti.shiro.spring.web.config.properties.ShiroProperties;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.apache.shiro.spring.web.config.AbstractShiroWebConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(ShiroWebAutoConfiguration.class) // 让此配置在 ShiroWebAutoConfiguration 之前加载
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
@EnableConfigurationProperties({
        ShiroProperties.class
})
public class TauCetiShiroWebAutoConfiguration extends AbstractShiroWebConfiguration {

    private final ShiroProperties shiroProperties;

    @Autowired
    public TauCetiShiroWebAutoConfiguration(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "shiro.session")
    protected SessionDAO sessionDAO() {
        return super.sessionDAO();
    }

    /**
     * 覆盖父类的实现
     */
    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionManager sessionManager() {
        TauCetiShiroSessionManager sessionManager = new TauCetiShiroSessionManager();
        sessionManager.setSessionDAO(sessionDAO());
//        sessionManager.setSessionFactory();
//        sessionManager.setDeleteInvalidSessions();

        return sessionManager;
    }


}
