package com.TP_Metamapa.Configuracion;

import com.TP_Metamapa.DTOS.KeycloakTokenDTO;
import com.TP_Metamapa.DTOS.LoginDTO;
import com.TP_Metamapa.DTOS.RoleDTO;
import com.TP_Metamapa.Servicio.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(CustomAuthProvider.class);
    private final AuthService authService;
    private final JwtAuthenticationConverterKC jwtAuthenticationConverterKC;

    public CustomAuthProvider(AuthService externalAuthService, JwtAuthenticationConverterKC jwtAuthenticationConverterKC) {
        this.authService = externalAuthService;
        this.jwtAuthenticationConverterKC = jwtAuthenticationConverterKC;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        try {
            KeycloakTokenDTO authResponse = authService.login(loginRequest); //rta con token
            if (authResponse == null) {
                throw new BadCredentialsException("Usuario o contraseña inválidos");
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            log.info("guardando accessToken y el refresh...");
            request.getSession().setAttribute("accessToken", authResponse.getAccess_token());
            request.getSession().setAttribute("refreshToken", authResponse.getRefresh_token());

            log.info("Buscando rol");
            RoleDTO roleDTO = authService.getRole(authResponse.getAccess_token());


            log.info("Cargando roles...");
            List<GrantedAuthority> authorities = roleDTO.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            log.info("Roles guardados en sesión");
            request.getSession().setAttribute("roles", roleDTO.getRoles());


            return new UsernamePasswordAuthenticationToken(username, password, authorities);

        } catch (RuntimeException e) {
            String errorMsg = (e.getMessage() != null) ? e.getMessage() : "";

            log.error(">>> ERROR EN AUTH PROVIDER: {}", errorMsg);

            String lowerMsg = errorMsg.toLowerCase();


            if (lowerMsg.contains("429") ||
                    lowerMsg.contains("too many requests") ||
                    lowerMsg.contains("rate") ||
                    lowerMsg.contains("límite") ||
                    lowerMsg.contains("limit")||
                    lowerMsg.contains("bloqueo_ratelimit")) {

                log.warn(">>> BLOQUEO DETECTADO (429) – enviando código al Handler");
                throw new BadCredentialsException("BLOQUEO_RATELIMIT");
            }

            if (lowerMsg.contains("401") ||
                    lowerMsg.contains("unauthorized") ||
                    lowerMsg.contains("usuario") && lowerMsg.contains("contraseña")) {

                throw new BadCredentialsException("Usuario o contraseña incorrectos");
            }

            throw new BadCredentialsException("Error en el sistema de autenticación: " + errorMsg);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}