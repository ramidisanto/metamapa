package com.TP_Metamapa.Servicio;


import com.TP_Metamapa.DTOS.FuentesDTO;
import com.TP_Metamapa.Modelos.TipoFuente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class FuenteProxyServicio {

    @Autowired
    RestTemplate restTemplate;
    @Value("${url.proxy}")
    private String urlBaseProxy;

    public List<FuentesDTO> obtenerTodas(){
        UriComponentsBuilder urlProxy = UriComponentsBuilder.fromHttpUrl(urlBaseProxy + "/fuentes");

        ResponseEntity<List<FuentesDTO>> respuesta = restTemplate.exchange(
                urlProxy.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FuentesDTO>>() {}
        );
        return respuesta.getBody();

    }

    public void crear(String url, TipoFuente tipo, String accessToken){
        FuentesDTO fuente = new FuentesDTO(url, tipo);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        UriComponentsBuilder urlProxy = UriComponentsBuilder.fromHttpUrl(urlBaseProxy + "/fuentes");
        HttpEntity<FuentesDTO> requestEntity = new HttpEntity<>(fuente, headers);
        ResponseEntity<String> respuesta = restTemplate.exchange(
                urlProxy.toUriString(),
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {}
        );

    }
}
