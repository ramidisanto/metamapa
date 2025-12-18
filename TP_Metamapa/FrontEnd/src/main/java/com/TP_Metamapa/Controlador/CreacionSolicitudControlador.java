package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.*;
import com.TP_Metamapa.Modelos.*;
import com.TP_Metamapa.Servicio.*;
import com.TP_Metamapa.Servicio.NavegacionServicio;
import com.TP_Metamapa.Servicio.SolicitudServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class CreacionSolicitudControlador {

    @Autowired
    NavegacionServicio navegadorServicio;

    @Autowired
    SolicitudServicio solicitudServicio;


    @GetMapping("/solicitarEliminacion/{id}")
    public String mostrarFormularioSolicitud(@PathVariable("id") Long idHecho, Model model) {
        Optional<HechoDTO> hechoOpt = navegadorServicio.obtenerHechoPorId(idHecho);
        if(hechoOpt.isPresent()) {
            model.addAttribute("hecho", hechoOpt.get());
            model.addAttribute("solicitudDTO", new SolicitudDTOInput("", idHecho));

            return "crearSolicitud";
        }else{
            model.addAttribute("errorMessage", "El hecho con ID " + idHecho + " no fue encontrado.");
            return "error/404";
        }
    }

    @PostMapping("/crearSolicitud")
    public String crearSolicitud(@ModelAttribute("solicitudDTO") SolicitudDTOInput solicitudDTO, RedirectAttributes redirectAttributes, Authentication authentication, HttpSession session, Model model) {

        try {

            if (authentication == null || !authentication.isAuthenticated()) {
                model.addAttribute("errorMessage", "Debes iniciar sesión para crear un hecho.");
                return "redirect:/auth/login";
            }
            String accessToken = (String) session.getAttribute("accessToken");
            String refreshToken = (String) session.getAttribute("refreshToken");

            if (accessToken == null) {
                model.addAttribute("errorMessage", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                return "redirect:/auth/login";
            }

            solicitudServicio.crearSolicitud(solicitudDTO, accessToken);
            redirectAttributes.addFlashAttribute("successMessage", "Su solicitud de eliminación ha sido enviada con éxito.");

            return "redirect:/navegar";

        } catch (RuntimeException e) {
            String errorMessage = "Error al procesar su solicitud.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/solicitarEliminacion/" + solicitudDTO.getIdHecho();
        }
    }
}