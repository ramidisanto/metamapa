package Configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 1. Configurar Timeouts más largos (10 segundos)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10000 ms = 10 segundos
        factory.setReadTimeout(10000);    // 10000 ms = 10 segundos

        RestTemplate restTemplate = new RestTemplate(factory);

        // 2. Agregar INTERCEPTOR para inyectar el User-Agent automáticamente
        // Esto soluciona el bloqueo de Nominatim sin tocar tu Servicio
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            // CAMBIA ESTO por un nombre real y tu email
            request.getHeaders().add("User-Agent", "ModuloNormalizador/1.0 (ramirodisanto05@gmail.com)");
            return execution.execute(request, body);
        });

        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}