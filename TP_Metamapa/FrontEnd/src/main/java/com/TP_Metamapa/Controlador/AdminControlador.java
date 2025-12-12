package com.TP_Metamapa.Controlador;


import com.TP_Metamapa.DTOS.*;
import com.TP_Metamapa.Modelos.TipoFuente;
import com.TP_Metamapa.Servicio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminControlador {

    @Autowired
    ColeccionServicio coleccionServicio;
    @Autowired
    SolicitudServicio solicitudServicio;
    @Autowired
    FuenteProxyServicio fuenteProxyServicio;
    @Autowired
    HechoServicio hechoServicio;
    @Autowired
    EstaticaServicio estaticaServicio;



    @GetMapping("/admin")
    public String panelDeAdmin(
            @RequestParam(name = "tab", defaultValue = "collections") String activeTab,
            Model model
    ) {
        List<ColeccionDTO> colecciones = new ArrayList<>();
        List<SolicitudDTO> solicitudes = new ArrayList<>();
        List<FuentesDTO> fuentes = new ArrayList<>();
        TipoFuente[] tipoFuentes = TipoFuente.values();

        switch (activeTab) {
            case "collections":
                colecciones = coleccionServicio.getColecciones();

                break;
            case "requests":
                  solicitudes = solicitudServicio.obtenerPendientes();
                break;
            case "static":
                break;
            case "proxy":
                fuentes = fuenteProxyServicio.obtenerTodas();
                break;
            default:
                break;
        }
        model.addAttribute("fuentesProxy", fuentes);
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("tiposFuente", tipoFuentes);
        model.addAttribute("activeTab", activeTab);

        return "admin"; // Siempre renderiza la misma vista principal
    }

    // NUEVO MÉTODO para procesar la subida del archivo CSV
    @PostMapping("/admin/cargar-csv")
    public String cargarArchivoCsv(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        // Verificación 1: que el archivo no esté vacío
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, seleccione un archivo para cargar.");
            return "redirect:/admin?tab=static";
        }

        // Verificación 2: que sea un archivo CSV
        if (!"text/csv".equals(file.getContentType())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: El archivo debe ser de formato CSV.");
            return "redirect:/admin?tab=static";
        }

        try {
            // Aquí iría la lógica para leer y procesar el archivo
            estaticaServicio.crear(file); //TODO: HACER ESTO E IMPLEMENTARLO EN EL BACKEND
            redirectAttributes.addFlashAttribute("successMessage", "¡Archivo '" + file.getOriginalFilename() + "' cargado exitosamente!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",  e.getMessage());
        }
        // Redirigimos de vuelta a la misma pestaña del admin
        return "redirect:/admin?tab=static";
    }


    @PostMapping("/admin/crear-proxy")
    public String crearFuenteProxy(
            @RequestParam String url,
            @RequestParam TipoFuente tipoFuente,
            RedirectAttributes redirectAttributes
    ) {
        // Verificación simple para la URL
        if (url == null || url.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La URL no puede estar vacía.");
            return "redirect:/admin?tab=proxy";
        }

        try {
            fuenteProxyServicio.crear(url, tipoFuente);

            System.out.println("Nueva fuente recibida: URL=" + url + ", Tipo=" + tipoFuente);

            redirectAttributes.addFlashAttribute("successMessage", "¡Fuente Proxy '" + url + "' creada con éxito!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la fuente: " + e.getMessage());
        }

        return "redirect:/admin?tab=proxy";
    }

    @PostMapping("/admin/eliminar-coleccion/{id}")
    public String eliminarColeccion(
            @PathVariable Long id
    ) {
        coleccionServicio.eliminarColeccion(id);
        return "redirect:/admin?tab=collections";
    }

    @PostMapping("/admin/aceptar-solicitud/{id}")
    public String aceptarSolicitud(
            @PathVariable Long id
    ) {
        solicitudServicio.aceptarSolicitud(id);
        return "redirect:/admin?tab=requests";
    }

    @PostMapping("/admin/rechazar-solicitud/{id}")
    public String rechazarSolicitud(
            @PathVariable Long id
    ) {
        solicitudServicio.rechazarSolicitud(id);
        return "redirect:/admin?tab=requests";
    }

    @GetMapping("/admin/ver-coleccion/{id}")
    public String verColeccion(@PathVariable("id") Long id, Model model) {

        Optional<ColeccionDTO> coleccionOpt = coleccionServicio.obtenerColeccion(id);

        if (coleccionOpt.isPresent() ) {
            model.addAttribute("coleccion", coleccionOpt.get());
            return "verColeccion";
        }

        model.addAttribute("errorMessage", "La colección con ID " + id + " no fue encontrada.");
        return "error/404";
    }

    @GetMapping("/admin/editar-coleccion/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Optional<ColeccionDTO> coleccion = coleccionServicio.obtenerColeccion(id);
        ConsensoDTO consensoDTO = new ConsensoDTO();
        if (coleccion.isPresent() ) {
            model.addAttribute("consensoDTO", consensoDTO);
            model.addAttribute("coleccion", coleccion.get());
            return "editarColeccion";
        }else{
        model.addAttribute("errorMessage", "La colección con ID " + id + " no fue encontrada.");
        return "error/404";
        }
    }

    @PostMapping("/admin/editar-coleccion/{id}/consenso")
    public String actualizarConsenso(@PathVariable Long id,
                                     @ModelAttribute("consensoDTO") ConsensoDTO consensoDTO,
                                     RedirectAttributes redirectAttributes) {
        try {
            coleccionServicio.actualizarColeccion(id, consensoDTO.getEstrategia());
            redirectAttributes.addFlashAttribute("mensaje", "Criterio de consenso actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar");
        }
        return "redirect:/admin/ver-coleccion/" + id;
    }

    @PostMapping("/admin/eliminar-hecho/{idHecho}")
    public String eliminarHecho(@PathVariable Long idHecho ,RedirectAttributes redirectAttributes) {
        hechoServicio.eliminarHecho(idHecho);

        redirectAttributes.addFlashAttribute("successMessage", "Hecho quitado exitosamente.");

        return "redirect:/navegar" ;
    }
}