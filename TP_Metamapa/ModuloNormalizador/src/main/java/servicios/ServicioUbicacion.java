package servicios;

import Modelos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Utils.TextoUtils;

@Service
public class ServicioUbicacion {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String NOMINATIM_URL =
            "https://nominatim.openstreetmap.org/reverse?lat={lat}&lon={lon}&format=json";

    public ServicioUbicacion(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "ubicaciones", key = "#latitud + ',' + #longitud")
    public UbicacionDTOoutput normalizarUbicacion(Double latitud, Double longitud) {

        try {
            String response = restTemplate.getForObject(
                    NOMINATIM_URL,
                    String.class,
                    latitud,
                    longitud
            );

            JsonNode root = objectMapper.readTree(response);
            JsonNode address = root.path("address");

            Pais pais = new Pais(
                    TextoUtils.capitalizarCadaPalabra(address.path("country").asText())
            );

            Provincia provincia = new Provincia(
                    TextoUtils.capitalizarCadaPalabra(address.path("state").asText()),
                    pais
            );

            String ciudad =
                    address.path("city").asText(null);

            if (ciudad == null || ciudad.isBlank()) {
                ciudad = address.path("town").asText(null);
            }
            if (ciudad == null || ciudad.isBlank()) {
                ciudad = address.path("village").asText(null);
            }
            if (ciudad == null || ciudad.isBlank()) {
                ciudad = address.path("municipality").asText(null);
            }
            if (ciudad == null || ciudad.isBlank()) {
                ciudad = address.path("county").asText(null);
            }


            Localidad localidad = new Localidad(
                    TextoUtils.capitalizarCadaPalabra(ciudad),
                    provincia
            );

            return new UbicacionDTOoutput(
                    pais.getNombre_pais(),
                    provincia.getNombre_provincia(),
                    localidad.getNombre_localidad(),
                    latitud,
                    longitud
            );

        } catch (Exception e) {
            throw new RuntimeException("Error normalizando ubicación", e);
        }
    }
}
