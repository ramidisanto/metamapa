package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.*;
import com.TP_Metamapa.Modelos.OrigenCarga;
import com.TP_Metamapa.Modelos.TokenExpiredException;
import com.TP_Metamapa.Servicio.AuthService;
import com.TP_Metamapa.Servicio.CategoriaServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import com.TP_Metamapa.Servicio.HechoServicio;
import com.TP_Metamapa.Servicio.NavegacionServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CrearHechoControlador {

    final
    CategoriaServicio categoriaServicio;
    final
    NavegacionServicio navegacionServicio;
    final
    HechoServicio hechoServicio;
    final
    AuthService authService;

    public CrearHechoControlador(CategoriaServicio categoriaServicio, NavegacionServicio navegacionServicio, HechoServicio hechoServicio, AuthService authService) {
        this.categoriaServicio = categoriaServicio;
        this.navegacionServicio = navegacionServicio;
        this.hechoServicio = hechoServicio;
        this.authService = authService;
    }

    @GetMapping("/crear-hecho")
    public String mostrarFormulario(Model model) {
        List<String> categorias = categoriaServicio.getCategoriasUnicas();
        model.addAttribute("hechoForm", new HechoFormDTO());
        model.addAttribute("categorias", categorias);
        return "crearHecho";
    }

    @PostMapping("/crear-hecho")
    public String procesarCrearHecho(
            @ModelAttribute("hechoForm") HechoFormDTO hechoFormData,
            @RequestParam(value = "multimediaFile", required = false) MultipartFile multimediaFile,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        System.out.println("ENTRO A CRER HECHO POST");
        String imageUrl;
        try {
            // 1. Validar autenticación
            if (authentication == null || !authentication.isAuthenticated()) {
                model.addAttribute("errorMessage", "Debes iniciar sesión para crear un hecho.");
                return "redirect:/auth/login";
            }
            String username = authentication.getName();

            // 2. Validar sesión/tokens
            String accessToken = (String) session.getAttribute("accessToken");
            String refreshToken = (String) session.getAttribute("refreshToken");

            if (accessToken == null) {
                model.addAttribute("errorMessage", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                return "redirect:/auth/login";
            }

            // 3. Obtener datos de usuario (con lógica de refresh token)
            UserDataDTO userData = null;
            try {
                userData = authService.getUserData(username, accessToken);
            } catch (TokenExpiredException e) {
                System.out.println("Token expirado. Intentando refrescar...");
                if (refreshToken == null) {
                    return "redirect:/auth/login";
                }
                try {
                    KeycloakTokenDTO newTokens = authService.refreshAccessToken(refreshToken);
                    if (newTokens == null) throw new RuntimeException("No se pudo refrescar el token");

                    session.setAttribute("accessToken", newTokens.getAccess_token());
                    session.setAttribute("refreshToken", newTokens.getRefresh_token());
                    userData = authService.getUserData(username, newTokens.getAccess_token());
                } catch (Exception refreshError) {
                    return "redirect:/auth/login";
                }
            }

            if (userData == null) {
                model.addAttribute("errorMessage", "No se pudo obtener la información del usuario.");
                model.addAttribute("categorias", categoriaServicio.getCategoriasUnicas());
                return "crearHecho";
            }

            if (hechoFormData.getFechaAcontecimiento().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("La fecha del acontecimiento no puede ser futura.");
            }

            imageUrl = hechoServicio.guardarMultimediaLocalmente(multimediaFile);

            HechoDTOInput hechoParaBackend = new HechoDTOInput();
            hechoParaBackend.setTitulo(hechoFormData.getTitulo());
            hechoParaBackend.setDescripcion(hechoFormData.getDescripcion());
            hechoParaBackend.setContenido(hechoFormData.getContenidoAdicional());
            hechoParaBackend.setContenido_multimedia(imageUrl);
            hechoParaBackend.setFechaAcontecimiento(hechoFormData.getFechaAcontecimiento());
            hechoParaBackend.setLocalidad(hechoFormData.getLocalidad());
            hechoParaBackend.setProvincia(hechoFormData.getProvincia());
            hechoParaBackend.setPais(hechoFormData.getPais());
            hechoParaBackend.setLatitud(hechoFormData.getLatitud());
            hechoParaBackend.setLongitud(hechoFormData.getLongitud());
            hechoParaBackend.setUsuario(username);
            hechoParaBackend.setNombre(userData.getFirstName());
            hechoParaBackend.setApellido(userData.getLastName());
            hechoParaBackend.setFechaNacimiento(userData.getBirthdate());
            hechoParaBackend.setAnonimo(hechoFormData.isAnonimo());

            if ("Otra".equals(hechoFormData.getCategoria()) && hechoFormData.getCustomCategoria() != null) {
                hechoParaBackend.setCategoria(hechoFormData.getCustomCategoria());
            } else {
                hechoParaBackend.setCategoria(hechoFormData.getCategoria());
            }

            // 5. Enviar al Backend (Aquí puede saltar la excepción del Servicio)
            hechoServicio.enviarHechoAlBackend(hechoParaBackend);

            redirectAttributes.addFlashAttribute("successMessage", "¡Hecho creado con éxito!");
            return "redirect:/hechos-pendientes";

        } catch (Exception e) {
            // ==================================================================
            // AQUÍ ESTÁ EL CAMBIO CLAVE:
            // Usamos e.getMessage() directo porque el Servicio ya se encargó
            // de ponerle el texto bonito que mandó el backend.
            // ==================================================================
            System.err.println("Error procesando creación de hecho: " + e.getMessage());

            // Pasamos el mensaje limpio a la vista
            model.addAttribute("errorMessage", e.getMessage());

            // Recargamos categorías para que el form se vea bien al reintentar
            List<String> categorias = categoriaServicio.getCategoriasUnicas();
            model.addAttribute("categorias", categorias);

            // Volvemos a la misma página (los datos del usuario se mantienen solos)
            return "crearHecho";
        }
    }
    @GetMapping("ver-hecho/{id}")
    public String verHecho(@PathVariable Long id, Model model) {
        // habría que hacer en lugar del service, una llamada al back
        Optional<HechoDTO> hechoOpt = navegacionServicio.obtenerHechoPorId(id);
        if ( hechoOpt.isPresent()){
            model.addAttribute("hecho", hechoOpt.get()
            );
            model.addAttribute("origenDinamica", OrigenCarga.FUENTE_DINAMICA
            );
            return "verHecho";
        }else{
            model.addAttribute("errorMessage", "El hecho con ID " + id + " no fue encontrado.");
            return "error/404";
        }
    }

    @GetMapping("/hechos-pendientes")
    public String verHechosPendientes( Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("errorMessage", "Debes iniciar sesión para crear un hecho.");
            return "redirect:/auth/login";
        }

        // extraer username desde el Authentication
        String username = authentication.getName();
        List<HechoDTO> hechosPendientes = hechoServicio.obtenerHechoPendiente(username);

        model.addAttribute("hechosPendientes", hechosPendientes);

        return "hechosPendientes";
    }
}