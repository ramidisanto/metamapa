package servicios;

import Modelos.DTOs.UbicacionDTOoutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServicioUbicacion {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ubicacion.provider.locationiq.url}")
    private String locationIqUrl;

    @Value("${ubicacion.provider.locationiq.key}")
    private String apiKey;

    public ServicioUbicacion(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "ubicaciones", key = "#latitud + ',' + #longitud")
    public UbicacionDTOoutput normalizarUbicacion(Double latitud, Double longitud) {

        try {
            String url = locationIqUrl +
                    "?key={key}" +
                    "&lat={lat}" +
                    "&lon={lon}" +
                    "&format=json" +
                    "&accept-language=es";

            ResponseEntity<String> response = restTemplate.getForEntity(
                    url,
                    String.class,
                    apiKey,
                    latitud,
                    longitud
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode address = root.path("address");

            return new UbicacionDTOoutput(
                    address.path("country").asText(null),
                    address.path("state").asText(null),
                    address.path("city").asText(
                            address.path("town").asText(
                                    address.path("village").asText(null)
                            )
                    ),
                    latitud,
                    longitud
            );

        } catch (Exception e) {
            System.err.println("[WARN] LocationIQ fallÃ³: " + e.getMessage());
            return new UbicacionDTOoutput(null, null, null, latitud, longitud);
        }
    }
}