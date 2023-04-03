package cn.iurac.testsystem.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import cn.iurac.testsystem.shiro.cache.GuavaCacheManager;
import cn.iurac.testsystem.shiro.realm.CustomerRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.*;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }

    @Bean
    public DefaultWebSecurityManager securityManager(CustomerRealm customerRealm, GuavaCacheManager guavaCacheManager){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置密码匹配器
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1024);
        customerRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        //禁用session管理
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        //设置缓存管理器
        customerRealm.setCachingEnabled(true);
        customerRealm.setAuthorizationCachingEnabled(true);
        customerRealm.setAuthenticationCachingEnabled(true);
        customerRealm.setAuthorizationCacheName("authorizationCacheName");
        customerRealm.setAuthenticationCacheName("authenticationCacheName");
        securityManager.setCacheManager(guavaCacheManager);
        //设置自定义域
        securityManager.setRealm(customerRealm);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);//设置安全管理器

        LinkedHashMap<String, String> urlMap = new LinkedHashMap<>(32);
        urlMap.put("/index","anon");
        urlMap.put("/", "anon");
        urlMap.put("/register", "anon");
        urlMap.put("/login", "anon");
        urlMap.put("/logout", "anon");
        urlMap.put("/kaptcha/**", "anon");
        urlMap.put("/layer/**", "anon");
        urlMap.put("/layui/**", "anon");
        urlMap.put("/**/*.js", "anon");
        urlMap.put("/**/*.png", "anon");
        urlMap.put("/swagger-ui/**", "anon");
        urlMap.put("/v2/**", "anon");
        urlMap.put("/swagger-resources/**", "anon");
        urlMap.put("/sys/notice/getLatest", "anon");
        urlMap.put("/**", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(urlMap);
        shiroFilterFactoryBean.setLoginUrl("/login");

        return shiroFilterFactoryBean;
    }

}
