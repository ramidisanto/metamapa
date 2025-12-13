package com.TP_Metamapa.Servicio;

import com.TP_Metamapa.DTOS.EstadoDTO;
import com.TP_Metamapa.DTOS.SolicitudDTO;
import com.TP_Metamapa.DTOS.SolicitudDTOInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import org.springframework.http.HttpMethod;
@Service
public class SolicitudServicio {

    @Autowired
    RestTemplate restTemplate;

    @Value("${url.avd}")
    private String urlBaseAVD;

    @Value("${url.publico}")
    private String urlBasePublico;

    public List<SolicitudDTO> obtenerPendientes(){
        UriComponentsBuilder urlSolicitudes = UriComponentsBuilder.fromHttpUrl(urlBaseAVD + "/solicitudes/pendientes");
        ResponseEntity<List<SolicitudDTO>> respuesta = restTemplate.exchange(
                urlSolicitudes.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SolicitudDTO>>() {}
        );
        return respuesta.getBody();
    }

    public String crearSolicitud(SolicitudDTOInput solicitudDTO, String accessToken){
        UriComponentsBuilder urlSolicitudes = UriComponentsBuilder.fromHttpUrl(urlBasePublico + "/solicitudes");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<SolicitudDTOInput> requestEntity = new HttpEntity<>(solicitudDTO, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    urlSolicitudes.toUriString(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return response.getBody();

        } catch (HttpClientErrorException e) {
            String errorMessageFromApi = e.getResponseBodyAsString();
            throw new RuntimeException("Error 400 capturado. Mensaje: " + errorMessageFromApi);

        } catch (Exception e) {
            System.err.println("Error fatal en la llamada a la API: " + e.getMessage());
            throw new RuntimeException("No se pudo contactar con el servicio.", e);
        }
    }

    public void rechazarSolicitud(Long idSolicitud){
        UriComponentsBuilder urlSolicitudes = UriComponentsBuilder.fromHttpUrl(urlBaseAVD + "/solicitudes/pendientes/" + idSolicitud);

        HttpEntity<EstadoDTO> requestEntity = new HttpEntity<>(new EstadoDTO("RECHAZADA"));
        ResponseEntity<String> respuesta = restTemplate.exchange(
                urlSolicitudes.toUriString(),
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        // ver si hacer algo con la respuesta
    }

    public void aceptarSolicitud(Long idSolicitud){
        UriComponentsBuilder urlSolicitudes = UriComponentsBuilder.fromHttpUrl(urlBaseAVD +"/solicitudes/pendientes/" + idSolicitud);
       /* HttpEntity<String> requestEntity = new HttpEntity<>("ACEPTADA");
        ResponseEntity<String> respuesta = restTemplate.exchange(
                urlSolicitudes.toUriString(),
                HttpMethod.PUT,
                requestEntity,
                String.class
        );*/
        HttpEntity<EstadoDTO> requestEntity = new HttpEntity<>(new EstadoDTO("ACEPTADA"));
        restTemplate.exchange(
                urlSolicitudes.toUriString(),
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        // ver si hacer algo con la respuesta
    }
}
