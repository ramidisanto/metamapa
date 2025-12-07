package com.keycloak.moduloauth.Config;

import com.keycloak.moduloauth.RateLimit.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private IpAccessInterceptor ipAccessInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor; // <--- Inyección directa

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registro explícito
        registry.addInterceptor(ipAccessInterceptor).addPathPatterns("/**");
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**");
    }
}