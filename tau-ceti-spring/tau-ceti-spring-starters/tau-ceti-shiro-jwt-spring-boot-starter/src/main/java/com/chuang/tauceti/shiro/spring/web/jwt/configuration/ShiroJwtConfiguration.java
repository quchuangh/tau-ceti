package com.chuang.tauceti.shiro.spring.web.jwt.configuration;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtCredentialsMatcher;
import com.chuang.tauceti.shiro.spring.web.jwt.JwtManager;
import com.chuang.tauceti.shiro.spring.web.jwt.StatelessWebSubjectFactory;
import com.chuang.tauceti.shiro.spring.web.jwt.filter.JwtAuthFilter;
import com.chuang.tauceti.shiro.spring.web.jwt.filter.PasswordFilter;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.ShiroProperties;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.IShiroService;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.JwtRealm;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.LoginRealm;
import com.chuang.tauceti.support.exception.SystemException;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * shiro 配置
 * 需要注意的是，我们这里使用了 shiro-starter, Application
 */
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties({
        ShiroProperties.class
})
public class ShiroJwtConfiguration extends AbstractShiroWebConfiguration {



    @Resource private ShiroProperties shiroProperties;


//    @Bean
//    public JwtAuthFilter jwtFilter() {
//        return ;
//    }
//
//    @Bean
//    public PasswordFilter passwordFilter() {
//        return ;
//    }

    @Bean
    public JwtManager manager() {
        return new JwtManager(shiroProperties.getJwt());
    }


    @Bean(name = "filterMap")
    public Map<String, Filter> filterMap() {
        Map<String, Filter> filterMap = new LinkedHashMap<>();// 其他默认的filter会在 ShiroFilterFactoryBean.getObject 时初始化进去
        filterMap.put("auth", new PasswordFilter(manager(), shiroProperties, authService()));
        filterMap.put("jwt", new JwtAuthFilter(manager(), shiroProperties));
        return filterMap;
    }

//    @Bean
//    public FilterRegistrationBean<AbstractShiroFilter> sessionRepositoryFilterRegistration(ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {
//        FilterRegistrationBean<AbstractShiroFilter> registration = new FilterRegistrationBean<>((AbstractShiroFilter) shiroFilterFactoryBean.getObject());
//        registration.setDispatcherTypes(DispatcherType.REQUEST,DispatcherType.ASYNC,DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE);
//        registration.setOrder(0);
//        return registration;
//    }

//    /**
//     * @see org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration
//     */
//

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();

        filterFactoryBean.setLoginUrl(shiroProperties.getLoginUrl());
        filterFactoryBean.setSuccessUrl(shiroProperties.getSuccessUrl());
        filterFactoryBean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());

        filterFactoryBean.setSecurityManager(createSecurityManager());

//        filterFactoryBean.setGlobalFilters(Arrays.asList(
//                DefaultFilter.anon.name(),
//                DefaultFilter.invalidRequest.name()
//        ));
        filterFactoryBean.setFilters(filterMap());
        filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());

        return filterFactoryBean;
    }

    @Bean
    @Override
    protected SessionsSecurityManager createSecurityManager() {
        SessionsSecurityManager securityManager = super.createSecurityManager();
        securityManager.setAuthenticator(authenticator());
        securityManager.setAuthorizer(authorizer());
        securityManager.setRealms(Arrays.asList(loginRealm(), jwtRealm()));
        securityManager.setSessionManager(sessionManager());
        securityManager.setEventBus(eventBus);

        if (cacheManager != null) {
            securityManager.setCacheManager(cacheManager);
        }

        return securityManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashIterations(shiroProperties.getHashedCredential().getIterations());
        credentialsMatcher.setHashAlgorithmName(shiroProperties.getHashedCredential().getAlgorithm());
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

    @Bean
    @ConditionalOnMissingBean
    public IShiroService authService() {
        throw new SystemException("IShiroService bean can not found~~");
    }


    @Bean
    @ConditionalOnMissingBean
    public JwtRealm jwtRealm() {
        JwtRealm realm = new JwtRealm(authService());
        realm.setCredentialsMatcher(new JwtCredentialsMatcher());
        return realm;
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginRealm loginRealm() {
        LoginRealm realm = new LoginRealm(authService());
        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }


    /**
     * Shiro starter 填坑说明
     * 这里必须自定义授权器，虽然 ShiroWebAutoConfiguration 会定义，但由于我们设定自己的配置早于 ShiroWebAutoConfiguration 执行。
     * 因此spring加载 authorizer时判断该类型对象是否被加载时，spring容器中已经存在realm对象，又因为realm对象本身实现了 Authorizer 接口。
     * 因此被spring认为 Authorizer 对象已经被加载。这样导致 ShiroWebAutoConfiguration 在创建 securityManager 时需要通过调用 authorizer()来获取 Authorizer 对象时导致获取失败。
     * 进而导致启动失败，并提示 authorizer bean不存在。
     *
     * 该问题具体的debug和分析步骤可以查阅下面这篇帖子：
     * https://www.cnblogs.com/insaneXs/p/11028286.html
     */
    @Bean
    @Override
    protected Authorizer authorizer() {
        return super.authorizer();
    }


    @Override
    @Bean
    protected SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultWebSessionStorageEvaluator evaluator = new DefaultWebSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        return evaluator;
    }

    @Override
    @Bean
    protected SubjectFactory subjectFactory() {
        return new StatelessWebSubjectFactory();
    }

    @Override
    @Bean
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
        Map<String, String> filterChainMap = getFilterChainDefinitionMap();

        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinitions(filterChainMap);
        return chainDefinition;
    }


    private Map<String, String> getFilterChainDefinitionMap() {

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();


        if(shiroProperties.getFilterChainDefinitions() != null) {
            filterChainDefinitionMap.putAll(shiroProperties.getFilterChainDefinitions());
        }

        // 获取排除的路径
        List<String[]> anonList = shiroProperties.getAnon();
        if (CollectionUtils.isNotEmpty(anonList)) {
            anonList.forEach(anonArray -> {
                if (CollectionKit.isNotEmpty(anonArray)) {
                    for (String anonPath : anonArray) {
                        filterChainDefinitionMap.put(anonPath, "anon");
                    }
                }
            });
        }
        filterChainDefinitionMap.put(shiroProperties.getLoginUrl(), "auth");
        filterChainDefinitionMap.put("/**", "jwt");
        return filterChainDefinitionMap;
    }


}
