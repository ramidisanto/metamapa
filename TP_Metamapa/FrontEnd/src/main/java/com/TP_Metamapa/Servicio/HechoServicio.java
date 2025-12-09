package com.TP_Metamapa.Servicio;

import com.TP_Metamapa.DTOS.HechoDTO;
import com.TP_Metamapa.DTOS.HechoDTOInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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

    @Value("${file.upload-dir}") // Directorio para guardar archivos
    private String uploadDir;

    @Value("${file.upload-url}") // URL base para acceder a los archivos
    private String uploadUrl;

    @Value("${url.publico}")
    private String urlPublico;
    @Value("${url.avd}")
    private String urlAVD;
    @Value("${url.dinamica}")
    private String urlDinamica;

    public List<HechoDTO> hechosRecientes(){
        UriComponentsBuilder urlHechos = UriComponentsBuilder.fromHttpUrl(urlPublico + "/publico/hechos");

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

    public void eliminarHecho(Long idHecho){
        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(urlAVD + "/hecho/" + idHecho);

        ResponseEntity<String> respuesta = restTemplate.exchange(
                url.toUriString(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {}
        );
    }

    public String guardarMultimediaLocalmente(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null; // No hay archivo para guardar
        }

        Path uploadPath = Paths.get(uploadDir);

        // Crear directorio si no existe
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generar nombre tipo MetaMapa_timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueFilename = "MetaMapa_" + timestamp + extension;

        // Guardar el archivo
        Path filePath = uploadPath.resolve(uniqueFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Devolver la URL relativa para usar en <img> y guardar en BD (vía backend)
        return uploadUrl + uniqueFilename;
    }

    public void enviarHechoAlBackend(HechoDTOInput hechoParaBackend) {

        String url = urlDinamica + "/dinamica/hechos";
        HttpEntity<HechoDTOInput> requestEntity = new HttpEntity<>(hechoParaBackend);

        try {
            ResponseEntity<String> respuesta = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<String>() {}
            );

        } catch (HttpClientErrorException e) {
            // Errores 4xx (400 Bad Request, 404 Not Found, etc.)
            // El backend suele enviar el mensaje de error en el cuerpo de la respuesta
            String mensajeDelBackend = e.getResponseBodyAsString();

            // Si el mensaje viene vacio, usamos el status code
            if (mensajeDelBackend == null || mensajeDelBackend.isEmpty()) {
                mensajeDelBackend = "Error en la validación de datos (" + e.getStatusCode() + ")";
            }

            // Lanzamos una excepcion con el mensaje LIMPIO para que el controlador lo muestre
            throw new RuntimeException(mensajeDelBackend);

        } catch (HttpServerErrorException e) {
            // Errores 5xx (500 Internal Server Error)
            throw new RuntimeException("El servidor backend tuvo un problema interno: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // Otros errores (conexión rechazada, timeout, etc.)
            throw new RuntimeException("Error inesperado al conectar con el backend: " + e.getMessage(), e);
        }
    }

    public List<HechoDTO> obtenerHechoPendiente(String username){
        UriComponentsBuilder urlHechos = UriComponentsBuilder
                .fromHttpUrl(urlDinamica + "/dinamica/hechos/pendientes")
                .queryParam("username", username);

        try {
            System.out.println("USERNAME: " + username);
            System.out.println("URL: " + urlHechos.toUriString());

            ResponseEntity<List<HechoDTO>> respuesta = restTemplate.exchange(
                    urlHechos.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<HechoDTO>>() {}
            );
            return respuesta.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el backend para obtener hechos pendientes: " + e.getMessage(), e);
        }
    }
}
