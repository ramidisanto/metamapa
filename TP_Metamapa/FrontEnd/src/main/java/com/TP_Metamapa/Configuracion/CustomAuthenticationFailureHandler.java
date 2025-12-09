package com.TP_Metamapa.Configuracion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = exception.getMessage();

        // Buscamos la "señal" que nos manda el CustomAuthProvider
        if (errorMessage != null && (
                errorMessage.contains("BLOQUEO_RATELIMIT") ||
                        errorMessage.contains("429") ||
                        errorMessage.contains("Too Many Requests")
        )) {
            // Redirigimos a la pantalla roja
            getRedirectStrategy().sendRedirect(request, response, "/rate-limit-error");
        } else {
            // Si es contraseña incorrecta, volvemos al login con error=true
            setDefaultFailureUrl("/auth/login?error=true");
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
