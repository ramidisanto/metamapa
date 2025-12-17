package servicios;

import Modelos.DTOs.UbicacionDTOoutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@Service
public class ServicioUbicacion {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // URL base de la API de Gobierno de Argentina
    private static final String GEOREF_URL = "https://apis.datos.gob.ar/georef/api/ubicacion?lat={lat}&lon={lon}";

    public ServicioUbicacion(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "ubicaciones", key = "#latitud + ',' + #longitud")
    public UbicacionDTOoutput normalizarUbicacion(Double latitud, Double longitud) {
        try {
            // Georef no necesita API Key, solo latitud y longitud
            ResponseEntity<String> response = restTemplate.getForEntity(
                    GEOREF_URL,
                    String.class,
                    latitud,
                    longitud
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode ubicacionNode = root.path("ubicacion");

            // Georef devuelve siempre "Argentina" implícitamente, pero extraemos los datos locales
            String provincia = ubicacionNode.path("provincia").path("nombre").asText(null);

            // Lógica para localidad: A veces es 'municipio', a veces 'departamento' (zonas rurales)
            String localidad = obtenerLocalidad(ubicacionNode);

            return new UbicacionDTOoutput(
                    "Argentina", // País fijo
                    provincia,
                    localidad,
                    latitud,
                    longitud
            );

        } catch (Exception e) {
            System.err.println("[WARN] Georef falló o timeout: " + e.getMessage());
            // En caso de error, devolvemos nulls pero mantenemos lat/lon
            return new UbicacionDTOoutput(null, null, null, latitud, longitud);
        }
    }

    private String obtenerLocalidad(JsonNode ubicacionNode) {
        // Intentar obtener municipio primero (ej. "Lomas de Zamora")
        JsonNode municipio = ubicacionNode.path("municipio");
        if (!municipio.isMissingNode() && !municipio.path("nombre").isNull()) {
            return municipio.path("nombre").asText();
        }

        // Si no hay municipio, intentar con departamento (ej. zonas menos pobladas)
        JsonNode departamento = ubicacionNode.path("departamento");
        if (!departamento.isMissingNode() && !departamento.path("nombre").isNull()) {
            return departamento.path("nombre").asText();
        }

        return null;
    }
}