package com.chuang.tauceti.shiro.spring.web.jwt.configuration;

import com.chuang.tauceti.shiro.spring.web.jwt.JwtManager;
import com.chuang.tauceti.shiro.spring.web.jwt.JwtPayloadConvert;
import com.chuang.tauceti.shiro.spring.web.jwt.StatelessWebSubjectFactory;
import com.chuang.tauceti.shiro.spring.web.jwt.filter.JwtFilter;
import com.chuang.tauceti.shiro.spring.web.jwt.properties.ShiroProperties;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.IRealmService;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.JwtRealm;
import com.chuang.tauceti.shiro.spring.web.jwt.realm.LoginRealm;
import com.chuang.tauceti.support.exception.SystemException;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.web.config.AbstractShiroWebConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
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
    @Resource private JwtPayloadConvert convert;

//    @Bean
//    public CredentialsMatcher credentialsMatcher() {
//        return new JwtCredentialsMatcher();
//    }



    @Bean
    public JwtManager manager() {
        return new JwtManager(shiroProperties.getJwt(), convert);
    }

    @Bean
    public Map<String, Filter> filterMap() {
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("jwt", new JwtFilter(manager(), shiroProperties.getJwt()));
        return filterMap;
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginRealm passwordRealm() {
        return new LoginRealm(realmService());
    }

    @Bean
    @ConditionalOnMissingBean
    public IRealmService realmService() {
        throw new SystemException("IRealmService bean can not found~~");
    }


    @Bean
    @ConditionalOnMissingBean
    public JwtRealm jwtRealm() {
        return new JwtRealm(realmService());
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
        return filterChainDefinitionMap;
    }


}
