package servicios;

import Modelos.*;
import Modelos.DTOs.UbicacionDTOoutput;
import Repositorio.RepositorioLocalidad;
import Repositorio.RepositorioPais;
import Repositorio.RepositorioProvincia;
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
    @Autowired
    RepositorioLocalidad repositorioLocalidad;
    @Autowired
    RepositorioProvincia repositorioProvincia;
    @Autowired
    RepositorioPais repositorioPais;

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

        Ubicacion existente =
                repositorioUbicacion.findByLatitudAndLongitud(latitud, longitud);

        if (existente != null) {
            return new UbicacionDTOoutput(
                    existente.getPais().getPais(),
                    existente.getProvincia().getProvincia(),
                    existente.getLocalidad().getLocalidad(),
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

            Pais pais = this.crearPais(TextoUtils.capitalizarCadaPalabra(address.path("country").asText()));



            Provincia provincia = this.crearProvincia(TextoUtils.capitalizarCadaPalabra(address.path("state").asText()),
                    pais);


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
            if (ciudad == null || ciudad.isBlank()) {
                ciudad = "Sin localidad";
            }


            Localidad localidad = this.crearLocalidad(TextoUtils.capitalizarCadaPalabra(ciudad),
                    provincia);

            Ubicacion nueva = this.crearUbicacion(latitud, longitud, localidad, provincia, pais);

            return new UbicacionDTOoutput(
                    pais.getPais(),
                    provincia.getProvincia(),
                    localidad.getLocalidad(),
                    latitud,
                    longitud
            );

        } catch (Exception e) {
            throw new RuntimeException("Error normalizando ubicación", e);
        }
    }

    public Pais crearPais(String nombre) {
        Pais pais = repositorioPais.findByPais(nombre);
        if (pais == null) {
            pais = new Pais(nombre);
            repositorioPais.save(pais);
        }
        return pais;
    }

    public Provincia crearProvincia(String nombre, Pais pais) {
        Provincia provincia = repositorioProvincia.findByProvinciaAndPais(nombre, pais);
        if (provincia == null) {
            provincia = new Provincia(nombre, pais);
            repositorioProvincia.save(provincia);
        }
        return provincia;
    }

    public Localidad crearLocalidad(String nombre, Provincia provincia) {
        Localidad localidad = repositorioLocalidad.findByLocalidadAndProvincia(nombre, provincia);
        if (localidad == null) {
            localidad = new Localidad(nombre, provincia);
            repositorioLocalidad.save(localidad);
        }
        return localidad;
    }

    public Ubicacion crearUbicacion(Double latitud, Double longitud, Localidad localidad, Provincia provincia,
                                    Pais pais) {
        Ubicacion ubicacion = repositorioUbicacion.findByLatitudAndLongitud(latitud, longitud);
        if (ubicacion == null) {
            ubicacion = new Ubicacion(localidad, provincia, pais, latitud, longitud);
            repositorioUbicacion.save(ubicacion);
        }
        return ubicacion;
    }
}
