package com.TP_Metamapa.Servicio;

import com.TP_Metamapa.DTOS.HechoDTO;
import com.TP_Metamapa.DTOS.HechoDTOInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class HechoServicio {
    @Autowired
    RestTemplate restTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.upload-url}")
    private String uploadUrl;

    @Value("${url.publico}")
    private String urlPublico;
    @Value("${url.avd}")
    private String urlAVD;
    @Value("${url.dinamica}")
    private String urlDinamica;

    public List<HechoDTO> hechosRecientes(){
        UriComponentsBuilder urlHechos = UriComponentsBuilder.fromHttpUrl(urlPublico + "/publico/hechos/recientes");

        ResponseEntity<List<HechoDTO>> respuesta = restTemplate.exchange(
                urlHechos.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<HechoDTO>>() {}
        );
        return obtenerOchoMasRecientes(respuesta.getBody());

    }

    public static List<HechoDTO> obtenerOchoMasRecientes(List<HechoDTO> hechos) {
        return hechos.stream()
                .sorted(Comparator.comparing(HechoDTO:: getFechaCarga).reversed())
                .limit(8)
                .collect(Collectors.toList());
    }

    public void eliminarHecho(Long idHecho, String accessToken){
        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(urlAVD + "/hecho/" + idHecho);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> respuesta = restTemplate.exchange(
                url.toUriString(),
                HttpMethod.DELETE,
                requestEntity,
                new ParameterizedTypeReference<String>() {}
        );
    }

    public String guardarMultimediaLocalmente(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(uploadDir);


        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }


        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueFilename = "MetaMapa_" + timestamp + extension;


        Path filePath = uploadPath.resolve(uniqueFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }


        return uploadUrl + uniqueFilename;
    }

    public void enviarHechoAlBackend(HechoDTOInput hechoParaBackend, String accessToken) {

        String url = urlDinamica + "/dinamica/hechos";
        System.out.printf("Nombre: %b%n", hechoParaBackend.getMostrarNombre());
        System.out.printf("Mostrar apellido: %b%n", hechoParaBackend.getMostrarApellido());
        System.out.printf("Mostrar fecha nacimiento: %b%n", hechoParaBackend.getMostrarFechaNacimiento());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<HechoDTOInput> requestEntity = new HttpEntity<>(hechoParaBackend, headers);
            ResponseEntity<String> respuesta = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<String>() {}
            );

        } catch (HttpClientErrorException e) {

            String mensajeDelBackend = e.getResponseBodyAsString();


            if (mensajeDelBackend == null || mensajeDelBackend.isEmpty()) {
                mensajeDelBackend = "Error en la validación de datos (" + e.getStatusCode() + ")";
            }


            throw new RuntimeException(mensajeDelBackend);

        } catch (HttpServerErrorException e) {

            throw new RuntimeException(e.getResponseBodyAsString());

        } catch (Exception e) {

            throw new RuntimeException("Error inesperado al conectar con el servidor: " + e.getMessage(), e);
        }
    }

    public List<HechoDTO> obtenerHechoPendiente(String username,  String accessToken){
        UriComponentsBuilder urlHechos = UriComponentsBuilder
                .fromHttpUrl(urlDinamica + "/dinamica/hechos/pendientes")
                .queryParam("username", username);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
            System.out.println("USERNAME: " + username);
            System.out.println("URL: " + urlHechos.toUriString());

            ResponseEntity<List<HechoDTO>> respuesta = restTemplate.exchange(
                    urlHechos.toUriString(),
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<HechoDTO>>() {}
            );
            return respuesta.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el backend para obtener hechos pendientes: " + e.getMessage(), e);
        }
    }
}
