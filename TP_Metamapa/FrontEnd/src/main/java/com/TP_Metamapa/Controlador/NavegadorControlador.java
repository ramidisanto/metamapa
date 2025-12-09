package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.*;
import com.TP_Metamapa.Modelos.*;
import com.TP_Metamapa.Servicio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;


@Controller
public class NavegadorControlador {

    @Autowired
    HechoServicio hechoServicio;
    @Autowired
    ColeccionServicio coleccionServicio;
    @Autowired
    CategoriaServicio categoriaServicio;
    @Autowired
    LocalidadServicio localidadServicio;
    @Autowired
    ProvinciaServicio provinciaServicio;
    @Autowired
    PaisServicio paisServicio;
    @Autowired
    NavegacionServicio navegacionServicio;

    @GetMapping("/navegar")
    public String navegar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean contenidoMultimedia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCargaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  fechaCargaHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHechoDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHechoHasta,
            @RequestParam(required = false) Boolean navegacionCurada,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) Long coleccionId,
            @RequestParam(required = false) String textoLibre,
            @RequestParam(required = false) String accion,
            Model model) {

        List<HechoDTO> hechosFiltrados = new ArrayList<>();
        if ("limpiar".equals(accion)) {
            return "redirect:/navegar";
        }
        if ("buscarTexto".equals(accion) && textoLibre != null && !textoLibre.isBlank()) {
            hechosFiltrados = navegacionServicio.buscarPorTextoLibre(textoLibre);
        } else {
             hechosFiltrados = navegacionServicio.buscarConFiltros(categoria, contenidoMultimedia, fechaCargaDesde,fechaCargaHasta,
                     fechaHechoDesde, fechaHechoHasta, origen, titulo,pais, provincia, localidad, coleccionId, navegacionCurada);
        }

        List<String> listaCategorias = categoriaServicio.getCategoriasUnicas();
        List<String> listaPaises = paisServicio.getPaisesUnicos();
        List<String> listaProvincias = provinciaServicio.getProvinciasUnicas();
        List<String> listaLocalidades = localidadServicio.getLocalidadesUnicas();
        List<ColeccionDTO> colecciones = coleccionServicio.getColecciones();
        OrigenCarga[] origenesDeCarga = OrigenCarga.values();
//        List<String> listaCategorias = List.of("Cultura", "Historia", "Incendio Forestal");
//        List<String> listaPaises = List.of("Argentina", "Uruguay", "Chile");
//        List<String> listaProvincias = List.of("Buenos Aires", "Córdoba", "Santa Fe");
//        List<String> listaLocalidades = List.of("Lomas de Zamora", "La Plata", "Rosario");
//        HechoDTO hecho1 = new HechoDTO(1L, 101L, "Incendio en bosque", "Un gran incendio forestal afectó la zona sur.", "El incendio comenzó en horas de la tarde y se extendió rápidamente.", "imagen_incendio.jpg", "Desastre natural", LocalDateTime.of(2023, 5, 20, 16, 30), LocalDateTime.now(), "Bariloche", "Río Negro", "Argentina", -41.1335, -71.3103, "usuario123", "Juan", "Pérez", LocalDateTime.of(1990, 3, 15, 0, 0), false, true, "App móvil");
//        HechoDTO hecho2 = new HechoDTO(2L, 102L, "Marcha por los derechos laborales", "Manifestación pacífica en el centro.", "Cientos de personas se reunieron para reclamar mejoras salariales.", "video_marcha.mp4", "Protesta social", LocalDateTime.of(2024, 9, 1, 10, 0), LocalDateTime.now(), "Córdoba", "Córdoba", "Argentina", -31.4201, -64.1888, "usuario456", "María", "González", LocalDateTime.of(1985, 8, 22, 0, 0), true, true, "Sitio web");
//        HechoDTO hecho3 = new HechoDTO(3L, 103L, "Lanzamiento de satélite nacional", "Evento histórico en el ámbito tecnológico.", "Argentina lanzó su nuevo satélite de comunicaciones desde Cabo Cañaveral.", "foto_satélite.jpg", "Tecnología", LocalDateTime.of(2025, 4, 10, 14, 0), LocalDateTime.now(), "Cabo Cañaveral", "Florida", "Estados Unidos", 28.3922, -80.6077, "usuario789", "Lucía", "Martínez", LocalDateTime.of(1992, 12, 5, 0, 0), false, false, "Carga institucional");
//
//        hechosFiltrados.add(hecho1);
//        hechosFiltrados.add(hecho2);
//        hechosFiltrados.add(hecho3);

        // Lista fija desde nuestro Enum

//        List<ColeccionDTO> colecciones = new ArrayList<>();
//        ColeccionDTO coleccionDTO = new ColeccionDTO(1L, "Colección de Prueba", "Descripción de prueba",null, null, null, null);
//        colecciones.add(coleccionDTO);


        model.addAttribute("hechos", hechosFiltrados);
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("paises", listaPaises);
        model.addAttribute("provincias", listaProvincias);
        model.addAttribute("localidades", listaLocalidades);
        model.addAttribute("origenesDeCarga", origenesDeCarga);
        model.addAttribute("colecciones", colecciones);

        model.addAttribute("filtrosActuales", Map.ofEntries(
                entry("categoria", categoria != null ? categoria : ""),
                entry("contenidoMultimedia", contenidoMultimedia != null ? contenidoMultimedia : "todos"),
                entry("fechaCargaDesde", fechaCargaDesde != null ? fechaCargaDesde : ""),
                entry("fechaCargaHasta", fechaCargaHasta != null ? fechaCargaHasta : ""),
                entry("fechaHechoDesde", fechaHechoDesde != null ? fechaHechoDesde : ""),
                entry("fechaHechoHasta", fechaHechoHasta != null ? fechaHechoHasta : ""),
                entry("origen", origen != null ? origen : ""),
                entry("titulo", titulo != null ? titulo : ""),
                entry("pais", pais != null ? pais : ""),
                entry("provincia", provincia != null ? provincia : ""),
                entry("localidad", localidad != null ? localidad : ""),
                entry("coleccionId", coleccionId != null ? coleccionId : ""),
                entry("navegacionCurada", navegacionCurada != null ? navegacionCurada : false),
                entry("textoLibre", textoLibre != null ? textoLibre : "")
        ));

        model.addAttribute("activePage", "navegar");
        return "navegar";


    }


}