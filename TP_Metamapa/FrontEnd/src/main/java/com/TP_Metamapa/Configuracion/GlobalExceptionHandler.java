package com.TP_Metamapa.Configuracion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores 4xx (como el 429 Too Many Requests) que vienen del RestTemplate
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) {

        // Si el error es un 429, devolvemos 429 al navegador también
        if (e.getStatusCode().value() == 429) {
            return ResponseEntity
                    .status(429)
                    .header("Retry-After", e.getResponseHeaders().getFirst("Retry-After")) // Pasamos el header de tiempo si existe
                    .body("<h1>Error 429: Has excedido el límite de solicitudes.</h1><p>Por favor, espera un momento y vuelve a intentarlo.</p>");
        }

        // Para otros errores (404, 400, etc), los dejamos pasar o los manejamos genéricamente
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
    }
}