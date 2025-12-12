package Configuracion; 

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final IpAccessInterceptor ipAccessInterceptor;

    public WebConfig(IpAccessInterceptor ipAccessInterceptor) {
        this.ipAccessInterceptor = ipAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        registry.addInterceptor(ipAccessInterceptor)
                .addPathPatterns("/**"); 
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")  // ← Todas las rutas
//                .allowedOrigins("http://localhost:8088")
//                .allowedMethods("*")  // ← Todos los métodos
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }
}