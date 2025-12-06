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
            @ModelAttribute("hechoForm") HechoFormDTO hechoFormData, // Recibe datos del form
            @RequestParam(value = "multimediaFile", required = false) MultipartFile multimediaFile, // Recibe el archivo
            Authentication authentication, // Obtiene el usuario logueado
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model // Para volver a mostrar el form si hay error
    ) {
        System.out.println("ENTRO A CRER HECHO POST");
        String imageUrl;
        try {
            // validar que el usuario este autenticado
            if (authentication == null || !authentication.isAuthenticated()) {
                model.addAttribute("errorMessage", "Debes iniciar sesión para crear un hecho.");
                return "redirect:/auth/login";
            }

            // extraer username desde el Authentication
            String username = authentication.getName();

            // recuperar el access token de la sesion
            String accessToken = (String) session.getAttribute("accessToken");
            String refreshToken = (String) session.getAttribute("refreshToken");

            if (accessToken == null) {
                model.addAttribute("errorMessage", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                return "redirect:/auth/login";
            }

            UserDataDTO userData = null;

            // obtener datos cuando el token se expiro
            try {
                userData = authService.getUserData(username, accessToken);

            } catch (TokenExpiredException e) {
                System.out.println("Token expirado. Intentando refrescar...");

                if (refreshToken == null) {
                    System.err.println("No hay refresh token disponible");
                    model.addAttribute("errorMessage", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                    return "redirect:/auth/login";
                }

                try {
                    // refrescar el token
                    KeycloakTokenDTO newTokens = authService.refreshAccessToken(refreshToken);

                    if (newTokens == null) {
                        throw new RuntimeException("No se pudo refrescar el token");
                    }

                    // actualizar tokens sesion
                    session.setAttribute("accessToken", newTokens.getAccess_token());
                    session.setAttribute("refreshToken", newTokens.getRefresh_token());

                    System.out.println("token refrescado exitosamente");

                    // reintentar obtener datos del usuario con el nuevo token
                    userData = authService.getUserData(username, newTokens.getAccess_token());

                } catch (Exception refreshError) {
                    System.err.println("Error al refrescar token: " + refreshError.getMessage());
                    model.addAttribute("errorMessage", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                    return "redirect:/auth/login";
                }
            }

            if (userData == null) {
                model.addAttribute("errorMessage", "No se pudo obtener la información del usuario.");
                List<String> categorias = categoriaServicio.getCategoriasUnicas();
                model.addAttribute("categorias", categorias);
                return "crearHecho";
            }

            String firstName = userData.getFirstName();
            String lastName = userData.getLastName();
            LocalDate birthdate = userData.getBirthdate();

            System.out.println("auth: " + authentication);
            System.out.println("usuario: " + username);
            System.out.println("nombre: " + firstName);
            System.out.println("apellido: " + lastName);
            System.out.println("fecha nacimiento: " + birthdate);

            // Validar que los datos del usuario estén completos
            if (username == null || firstName == null || lastName == null) {
                model.addAttribute("errorMessage", "Los datos del usuario son incompletos. Contacta al administrador.");
                List<String> categorias = categoriaServicio.getCategoriasUnicas();
                model.addAttribute("categorias", categorias);
                return "crearHecho";
            }

            imageUrl = hechoServicio.guardarMultimediaLocalmente(multimediaFile);

            HechoDTOInput hechoParaBackend = new HechoDTOInput();
            hechoParaBackend.setTitulo(hechoFormData.getTitulo());
            hechoParaBackend.setDescripcion(hechoFormData.getDescripcion());
            hechoParaBackend.setContenido(hechoFormData.getContenidoAdicional());
            hechoParaBackend.setContenido_multimedia(imageUrl);
            hechoParaBackend.setFechaAcontecimiento(hechoFormData.getFechaAcontecimiento().atStartOfDay());
            hechoParaBackend.setLocalidad(hechoFormData.getLocalidad());
            hechoParaBackend.setProvincia(hechoFormData.getProvincia());
            hechoParaBackend.setPais(hechoFormData.getPais());
            hechoParaBackend.setLatitud(hechoFormData.getLatitud());
            hechoParaBackend.setLongitud(hechoFormData.getLongitud());
            hechoParaBackend.setUsuario(username);
            hechoParaBackend.setNombre(firstName);
            hechoParaBackend.setApellido(lastName);
            hechoParaBackend.setFechaNacimiento(birthdate);
            hechoParaBackend.setAnonimo(hechoFormData.isAnonimo());

            if ("Otra".equals(hechoFormData.getCategoria()) && hechoFormData.getCustomCategoria() != null && !hechoFormData.getCustomCategoria().isEmpty()) {
                hechoParaBackend.setCategoria(hechoFormData.getCustomCategoria());
            } else {
                hechoParaBackend.setCategoria(hechoFormData.getCategoria());
            }
            System.out.println("hecho que va para el back: " + hechoParaBackend);
            System.out.println("VA A IR AL SERVICE DE EL FRINT PARA CREAR HECO");
            hechoServicio.enviarHechoAlBackend(hechoParaBackend);

            redirectAttributes.addFlashAttribute("successMessage", "¡Hecho creado con éxito!");
            return "redirect:/hechos-pendientes";

        } catch (Exception e) {
            System.err.println("Error procesando creación de hecho: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error al crear el hecho: " + e.getMessage());
            List<String> categorias = categoriaServicio.getCategoriasUnicas();
            model.addAttribute("categorias", categorias);
            // El 'hechoForm' ya está en el modelo gracias a @ModelAttribute, así que los campos se rellenan
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