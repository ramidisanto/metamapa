package Servicio;

import Modelos.Entidades.DTOs.HechoNormalizarDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NormalizadorClient {

    @Value("${url.normalizador}")
    private String urlNormalizador;

    private final RestTemplate restTemplate;

    public NormalizadorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<HechoNormalizarDTO> normalizarBatch(List<HechoNormalizarDTO> batch) {

        ResponseEntity<List<HechoNormalizarDTO>> response =
                restTemplate.exchange(
                        urlNormalizador + "/normalizacion/batch",
                        HttpMethod.POST,
                        new HttpEntity<>(batch),
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody();
    }
}
