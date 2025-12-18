package com.TP_Metamapa.Servicio;

import ch.qos.logback.classic.Logger;
import com.TP_Metamapa.DTOS.*;
import com.TP_Metamapa.DTOS.UserDataDTO;
import com.TP_Metamapa.Modelos.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    public AuthService(@Value("${base-url.auth}") String url, RestTemplate restTemplate) {
        this.baseUrl = url;
        this.restTemplate = restTemplate;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public KeycloakTokenDTO login(LoginDTO loginDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginDTO> requestEntity = new HttpEntity<>(loginDTO, headers);
            String urlCompleta = baseUrl.concat("/auth/iniciar-sesion");

            ResponseEntity<KeycloakTokenDTO> respuesta = restTemplate.exchange(
                    urlCompleta,
                    HttpMethod.POST,
                    requestEntity,
                    KeycloakTokenDTO.class
            );

            if (respuesta.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RuntimeException("BLOQUEO_RATELIMIT");
            }

            if (!respuesta.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error de backend: " + respuesta.getStatusCode());
            }

            return respuesta.getBody();

        } catch (Exception ex) {
            // LOG CRUDO – fundamental para ver si el 429 se convierte en 401
            log.error(">>> EXCEPCIÓN CRUDA RECIBIDA DESDE RESTTEMPLATE:", ex);
            throw ex;
        }
    }

    public KeycloakTokenDTO refreshAccessToken(String refreshToken) {
        String urlCompleta = baseUrl.concat("/auth/refresh-token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(refreshRequest, headers);

        System.out.println("Intentando refrescar token...");

        try {
            ResponseEntity<KeycloakTokenDTO> respuesta = restTemplate.exchange(
                    urlCompleta,
                    HttpMethod.POST,
                    requestEntity,
                    KeycloakTokenDTO.class
            );
            System.out.println("Token refrescado exitosamente");
            return respuesta.getBody();
        } catch (Exception e) {
            System.err.println("Error al refrescar token: " + e.getMessage());
            throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
        }
    }

    public RoleDTO getRole(String accessToken) {
        String urlCompleta = baseUrl.concat("/auth/role");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);


        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        System.out.println("Solicitando roles...");
        try {
            System.out.println("ENTRE AL TRY");
            String urlRole = baseUrl.concat("/auth/role");
            ResponseEntity<RoleDTO> respuesta = restTemplate.exchange(
                    urlRole,
                    HttpMethod.GET,
                    requestEntity,
                    RoleDTO.class
            );
            System.out.println("roles recibidos");
            return respuesta.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error de conexión al crear hecho: " + e.getMessage(), e);
        }
    }

    public String register(RegisterDTO registerDTO) {
        try {
            Map<String, String> response = webClient.post()
                    .uri("/auth/create")
                    .bodyValue(registerDTO)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("message")) {
                return response.get("message");
            }
            return "Usuario creado exitosamente";
        } catch (WebClientResponseException.Conflict e) {
            System.err.println("Error HTTP: " + e.getStatusCode());
            System.err.println("Respuesta del servidor: " + e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 409) {
                try {
                    String responseBody = e.getResponseBodyAsString();
                    System.out.println("Respuesta de conflicto: " + responseBody);

                    if (responseBody.contains("Email already exists")) {
                        return "Email already exists!";
                    } else if (responseBody.contains("Username already exists")) {
                        return "Username already exists!";
                    } else {
                        return "Usuario ya existente";
                    }
                } catch (Exception ex) {
                    return "Usuario ya existente";
                }
            }

            return "Error creando al usuario";
        } catch (Exception e) {
            return "Error creando al usuario";
        }
    }

    public UserDataDTO getUserData(String username, String accessToken) {
        String urlCompleta = baseUrl.concat("/auth/search/" + username);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> respuesta = restTemplate.exchange(
                    urlCompleta,
                    HttpMethod.GET,
                    requestEntity,
                    List.class
            );

            List<Map<String, Object>> users = respuesta.getBody();
            System.out.println("USER-------" + users);
            if (users != null && !users.isEmpty()) {
                Map<String, Object> userMap = users.get(0);
                UserDataDTO userData = new UserDataDTO();
                userData.setUsername((String) userMap.get("username"));
                userData.setFirstName((String) userMap.get("firstName"));
                userData.setLastName((String) userMap.get("lastName"));
                userData.setEmail((String) userMap.get("email"));
                userData.setAttributes((Map<String, Object>) userMap.get("attributes"));

                return userData;
            }
            return null;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.err.println("Token expirado (401). Se necesita refresh.");
                throw new TokenExpiredException("El token de acceso ha expirado");
            }
            System.err.println("Error HTTP: " + e.getStatusCode());
            throw e;
        } catch (Exception e) {
            System.err.println("Tipo de excepción: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener datos del usuario: " + e.getMessage(), e);
        }
    }

}
