package Configuracion; 

import RateLimit.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final IpAccessInterceptor ipAccessInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public WebConfig(IpAccessInterceptor ipAccessInterceptor, RateLimitInterceptor rateLimitInterceptor) {
        this.ipAccessInterceptor = ipAccessInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        registry.addInterceptor(ipAccessInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**");
    }
}