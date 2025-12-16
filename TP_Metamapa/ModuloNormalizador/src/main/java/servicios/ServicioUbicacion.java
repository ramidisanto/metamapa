package servicios;

import Modelos.*;
import Modelos.DTOs.UbicacionDTOoutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Utils.TextoUtils;
@Service
public class ServicioUbicacion {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String BDC_URL =
            "https://api.bigdatacloud.net/data/reverse-geocode-client" +
                    "?latitude={lat}&longitude={lon}&localityLanguage=es";

    public ServicioUbicacion(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "ubicaciones", key = "#latitud + ',' + #longitud")
    public UbicacionDTOoutput normalizarUbicacion(Double latitud, Double longitud) {

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BDC_URL,
                    String.class,
                    latitud,
                    longitud
            );

            JsonNode root = objectMapper.readTree(response.getBody());

            return new UbicacionDTOoutput(
                    root.path("countryName").asText(null),
                    root.path("principalSubdivision").asText(null),
                    root.path("city").asText(
                            root.path("locality").asText(null)
                    ),
                    latitud,
                    longitud
            );

        } catch (Exception e) {
            System.err.println("[WARN] BigDataCloud falló: " + e.getMessage());
            return new UbicacionDTOoutput(null, null, null, latitud, longitud);
        }
    }
}

