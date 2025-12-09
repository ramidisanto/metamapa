package com.TP_Metamapa.Servicio;

import com.TP_Metamapa.DTOS.FuentesDTO;
import com.TP_Metamapa.Modelos.TipoFuente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class EstaticaServicio {
    @Autowired
    RestTemplate restTemplate;
    @Value("${url.estatica}")
    private String urlBaseEstatica;

    public void crear(MultipartFile file) { // Ya no hace falta 'throws Exception' porque lo manejamos adentro

        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(urlBaseEstatica + "/fuenteEstatica/CSV");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            // Convertir MultipartFile a ByteArrayResource
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // Crear el body multipart
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("csv", fileResource);

            // Crear la request
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Enviar la petición
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toUriString(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            System.out.println(response.getBody());

        } catch (HttpClientErrorException e) {
            // Capturamos el error 400/4xx que manda el backend (ej: "CSV mal formado")
            String mensajeError = e.getResponseBodyAsString();
            throw new RuntimeException(mensajeError); // Reenviamos solo el mensaje útil

        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Error en el servidor de estática");
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar el archivo: " + e.getMessage());
        }
    }
}
