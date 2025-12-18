package Configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GraphQLCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8088");
        config.addAllowedOrigin("http://165.232.153.148:8088");
        config.addAllowedOrigin("http://metamapa.app:8088");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/graphql", config);
//        source.registerCorsConfiguration("/graphql/**", config);
          source.registerCorsConfiguration("/**", config);  // ← CAMBIO: /** para todas las rutas


        return new CorsFilter(source);
    }
}
