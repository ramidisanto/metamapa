package com.TP_Metamapa.Configuracion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public Object handleHttpClientError(HttpClientErrorException e) {

        if (e.getStatusCode().value() == 429) {

            ModelAndView mav = new ModelAndView();
            mav.setViewName("error/429");
            mav.setStatus(HttpStatus.TOO_MANY_REQUESTS);

            String retryAfter = null;
            if (e.getResponseHeaders() != null) {
                retryAfter = e.getResponseHeaders().getFirst("Retry-After");
            }
            mav.addObject("retryAfter", retryAfter);

            return mav;
        }

        return ResponseEntity
                .status(e.getStatusCode())
                .body(e.getResponseBodyAsString());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "El archivo es demasiado grande. El tamaño máximo permitido es 10MB.");
        return "redirect:/crear-hecho";
    }
}