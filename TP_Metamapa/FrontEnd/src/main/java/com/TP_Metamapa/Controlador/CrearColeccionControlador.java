package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.ColeccionDTOInput;
import com.TP_Metamapa.DTOS.CriterioDTO;
import com.TP_Metamapa.DTOS.KeycloakTokenDTO;
import com.TP_Metamapa.Modelos.Consenso;
import com.TP_Metamapa.Modelos.CriterioDuplicadoException;
import com.TP_Metamapa.Modelos.OrigenCarga;
import com.TP_Metamapa.Modelos.TokenExpiredException;
import com.TP_Metamapa.Servicio.AuthService;
import com.TP_Metamapa.Servicio.CategoriaServicio;
import com.TP_Metamapa.Servicio.ColeccionServicio;
import com.TP_Metamapa.Servicio.PaisServicio;
import com.TP_Metamapa.Servicio.ProvinciaServicio;
import com.TP_Metamapa.Servicio.LocalidadServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CrearColeccionControlador {

    @Autowired
    CategoriaServicio categoriaServicio;
    @Autowired
    PaisServicio paisServicio;
    @Autowired
    ProvinciaServicio provinciaServicio;
    @Autowired
    LocalidadServicio localidadServicio;
    @Autowired
    ColeccionServicio coleccionServicio;
    @Autowired
    AuthService authService;


    @GetMapping("/admin/crear-coleccion")
    public String mostrarFormulario(Model model) {


        List<String> categorias = categoriaServicio.getCategoriasUnicas();
        List<String> paises = paisServicio.getPaisesUnicos();
        List<String> provincias = provinciaServicio.getProvinciasUnicas();
        List<String> localidades = localidadServicio.getLocalidadesUnicas();
        OrigenCarga[] origenes = OrigenCarga.values();
        Consenso[] criteriosConsenso = Consenso.values();

        ColeccionDTOInput coleccionForm = new ColeccionDTOInput();
        coleccionForm.setCriterio(new CriterioDTO());


        model.addAttribute("coleccionForm", coleccionForm);
        model.addAttribute("categorias", categorias);
        model.addAttribute("paises", paises);
        model.addAttribute("provincias", provincias);
        model.addAttribute("localidades", localidades);
        model.addAttribute("origenes", origenes);
        model.addAttribute("criteriosConsenso", criteriosConsenso);

        return "crearColeccion";
    }


    @PostMapping("/admin/crear-coleccion")
    public String procesarCrearColeccion(
            @ModelAttribute("coleccionForm") ColeccionDTOInput coleccionData,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication,
            HttpSession session
    ) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login";
            }
    
            String username = authentication.getName();
            
            String accessToken = (String) session.getAttribute("accessToken");
            String refreshToken = (String) session.getAttribute("refreshToken");
    
            if (accessToken == null) {
                return "redirect:/auth/login";
            }
            
            try {
                authService.getUserData(username, accessToken);
            } catch (TokenExpiredException e) {

                try {
                    KeycloakTokenDTO newTokens = authService.refreshAccessToken(refreshToken);
                    if (newTokens == null) {
                        throw new RuntimeException("No se pudo refrescar el token");
                    }
    
                    session.setAttribute("accessToken", newTokens.getAccess_token());
                    session.setAttribute("refreshToken", newTokens.getRefresh_token());
    
                    accessToken = newTokens.getAccess_token();
    
                } catch (Exception refreshError) {
                    return "redirect:/auth/login";
                }
            }
            
            coleccionServicio.crear(coleccionData, accessToken);
    
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "¡Colección creada con éxito!"
            );
            return "redirect:/admin?tab=collections";
    
        } catch (CriterioDuplicadoException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/crear-coleccion";
    
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al crear la colección: " + e.getMessage()
            );
            return "redirect:/admin/crear-coleccion";
        }
    }

}
