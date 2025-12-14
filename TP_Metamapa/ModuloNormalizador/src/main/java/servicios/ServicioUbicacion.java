package servicios;

import Modelos.*;
import Modelos.DTOs.UbicacionDTOoutput;
import Repositorio.RepositorioUbicacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Utils.TextoUtils;

import java.util.Optional;

@Service
public class ServicioUbicacion {

    @Autowired
    RepositorioUbicacion repositorioUbicacion;

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

        Optional<Ubicacion> existente =
                repositorioUbicacion.findByLatitudAndLongitud(latitud, longitud);

        if (existente.isPresent()) {
            Ubicacion u = existente.get();
            return new UbicacionDTOoutput(
                    u.getPais().getNombre_pais(),
                    u.getProvincia().getNombre_provincia(),
                    u.getLocalidad().getNombre_localidad(),
                    latitud,
                    longitud
            );
        }

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

            Ubicacion nueva = new Ubicacion(localidad, provincia, pais, latitud, longitud);
            repositorioUbicacion.save(nueva);

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
