package com.TP_Metamapa.Configuracion;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        RestTemplate rt = builder.build();

        // Restauramos el comportamiento normal (errores 4xx y 5xx lanzan excepción)
        rt.setErrorHandler(new DefaultResponseErrorHandler());

        return rt;
    }
}
