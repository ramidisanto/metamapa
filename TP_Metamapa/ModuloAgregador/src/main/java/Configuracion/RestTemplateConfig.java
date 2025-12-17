package Configuracion;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Timeout de conexión: Si el servidor no acepta la conexión en 5s, corta.
                .setConnectTimeout(Duration.ofSeconds(5))
                // Timeout de lectura: Si conecta, pero tarda más de 5s en responder, corta.
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}