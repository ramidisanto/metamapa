package com.TP_Metamapa.Servicio;

import com.TP_Metamapa.DTOS.ColeccionDTO;
import com.TP_Metamapa.DTOS.ColeccionDTOInput;
import com.TP_Metamapa.DTOS.SolicitudDTOInput;
import com.TP_Metamapa.Modelos.CriterioDuplicadoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class ColeccionServicio {

    @Autowired
    RestTemplate restTemplate;
    @Value("${url.publico}")
    private String urlPublico;
    @Value("${url.avd}")
    private String urlAvd;

    public List<ColeccionDTO> getColecciones(){
        UriComponentsBuilder urlColeccion = UriComponentsBuilder.fromHttpUrl( urlPublico + "/publico/colecciones");

        ResponseEntity<List<ColeccionDTO>> respuesta = restTemplate.exchange(
                urlColeccion.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ColeccionDTO>>() {}
        );
        return respuesta.getBody();

    }

    public void eliminarColeccion(Long idColeccion, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        UriComponentsBuilder urlEliminar = UriComponentsBuilder.fromHttpUrl(urlAvd + "/coleccion/" + idColeccion);

        ResponseEntity<String> respuesta = restTemplate.exchange(
                urlEliminar.toUriString(),
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
    }



    public Optional<ColeccionDTO> obtenerColeccion(Long id, String accessToken){
        UriComponentsBuilder urlColeccion = UriComponentsBuilder.fromHttpUrl(urlAvd + "/coleccion/" + id);
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
                ResponseEntity<ColeccionDTO> respuesta = restTemplate.exchange(
                        urlColeccion.toUriString(),
                        HttpMethod.GET,
                        requestEntity,
                        ColeccionDTO.class
                );
                return Optional.ofNullable(respuesta.getBody());

            } catch (HttpClientErrorException.NotFound e) {
                System.out.println("Colección con ID " + id + " no encontrada. Status: " + e.getStatusCode());
                return Optional.empty();

            }
    }

    public void actualizarColeccion(Long id, String consenso, String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        UriComponentsBuilder urlColeccion = UriComponentsBuilder.fromHttpUrl(urlAvd + "/coleccion/" + id + "/Consenso/" + consenso);
        ResponseEntity<ColeccionDTO> respuesta = restTemplate.exchange(
                urlColeccion.toUriString(),
                HttpMethod.PUT,
                requestEntity,
                ColeccionDTO.class
        );
    }

    public void crear(ColeccionDTOInput coleccionData, String accessToken){
        UriComponentsBuilder urlColeccion = UriComponentsBuilder.fromHttpUrl(urlAvd + "/coleccion");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<ColeccionDTOInput> requestEntity = new HttpEntity<>(coleccionData, headers);
            ResponseEntity<String> respuesta = restTemplate.exchange(
                    urlColeccion.toUriString(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

        }catch(HttpClientErrorException.Conflict e){
            throw new CriterioDuplicadoException(e.getResponseBodyAsString());
        }
    }
}