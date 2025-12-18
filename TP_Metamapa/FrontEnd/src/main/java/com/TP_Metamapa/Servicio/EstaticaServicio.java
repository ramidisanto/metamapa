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

    public void crear(MultipartFile file, String accessToken) {

        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(urlBaseEstatica + "/fuenteEstatica/CSV");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(accessToken);
        try {
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("csv", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url.toUriString(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            System.out.println(response.getBody());

        } catch (HttpClientErrorException e) {
            String mensajeError = e.getResponseBodyAsString();
            throw new RuntimeException(mensajeError); // Reenviamos solo el mensaje útil

        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Error en el servidor de estática");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
