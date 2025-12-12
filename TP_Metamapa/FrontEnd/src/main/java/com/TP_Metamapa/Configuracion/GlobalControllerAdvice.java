package com.TP_Metamapa.Configuracion;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void agregarCsrfAlModelo(HttpServletRequest request) {
        // Obtenemos el token del atributo de la request (donde Spring Security lo pone)
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            // ALERTA: Este es el truco.
            // Al llamar a .getToken(), forzamos a Spring a que calcule el token
            // y, si es necesario, cree la sesión AHORA, antes de que se renderice la vista.
            String token = csrfToken.getToken();
        }
    }
}