package com.keycloak.moduloauth.Services;

import com.keycloak.moduloauth.DTOs.LoginDTO;
import com.keycloak.moduloauth.DTOs.KeycloakToken;
import com.keycloak.moduloauth.DTOs.RegistroDTO;
import com.keycloak.moduloauth.Utils.KeycloackProvider;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.List;

@Service
@Slf4j
public class AuthService {

    private final String client_id;

    private final String client_secret;

    private final WebClient webClient;

    private final String URL_SERVER;

    @Autowired
    public KeycloackProvider keycloakProvider;

    public AuthService(@Value("${jwt.auth.converter.resource-id}") String client_id, @Value("${jwt.auth.converter.client-secret}")  String client_secret, @Value("${url.keycloak}") String url_server) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.URL_SERVER = url_server;
        this.webClient = WebClient.builder()
                .baseUrl(this.URL_SERVER)
                .build();
    }


    public List<UserRepresentation> findAllUsers(){
        return keycloakProvider.getRealmResource()
                .users()
                .list();
    }

    public List<UserRepresentation> searchUserByUsername(String username) {
        System.out.println("Entro al service");
        return keycloakProvider.getRealmResource()
                .users()
                .searchByUsername(username, true);
    }

    public KeycloakToken loginUser(@NonNull LoginDTO loginDTO) {
        System.out.println("entre al service login");
        try {
            KeycloakToken keycloakToken = webClient.post()
                    .uri("/realms/" + "spring-boot-realm-pr" + "/protocol/openid-connect/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData("client_id", client_id)
                            .with("grant_type", "password")
                            .with("username", loginDTO.getUsername())
                            .with("password", loginDTO.getPassword())
                            .with("client_secret", client_secret))
                    .retrieve()
                    .bodyToMono(KeycloakToken.class)
                    .block();

            if (keycloakToken == null || keycloakToken.getAccess_token() == null) {
                throw new RuntimeException("Error al iniciar sesión");
            }

            return keycloakToken;
        } catch (WebClientResponseException.Unauthorized e) {
            System.out.println("Datos inicio sesion incorrectos para usuario: " + loginDTO.getUsername());
            throw new RuntimeException("Usuario o contraseña incorrectos");
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("ERROR REAL DE KEYCLOAK: " + errorBody);

            throw new RuntimeException("Keycloak Error: " + errorBody);

        }
    }


    public String createUser(@NonNull RegistroDTO userDTO) {

        int status = 0;
        UsersResource usersResource = keycloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        userRepresentation.singleAttribute("birthdate", userDTO.getBirthdate().toString());

        Response response = usersResource.create(userRepresentation);

        status = response.getStatus();

        if (status == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = keycloakProvider.getRealmResource();

            List<RoleRepresentation> rolesRepresentation = null;

            // setteo siempre en user -> si quiero crear admins desde la interfaz cambiar -> ahora hacer en keycloak
            rolesRepresentation = List.of(realmResource.roles().get("user").toRepresentation());
            realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);

            return "User created successfully!!";

        } else if (status == 409) {
            String errorMessage = response.readEntity(String.class);

            if (errorMessage.contains("email") || errorMessage.contains("Email")) {
                return "Email already exists!";
            } else if (errorMessage.contains("username") || errorMessage.contains("User name")) {
                return "Username already exists!";
            } else {
                return "User exist already!";
            }
        } else {
            log.error("Error creating user, please contact with the administrator.");
            return "Error creating user, please contact with the administrator.";
        }
    }

}