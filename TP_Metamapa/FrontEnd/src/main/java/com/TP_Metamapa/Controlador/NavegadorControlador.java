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
    /*
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


    */
    @GetMapping("/navegar")
    public String navegar(Model model) {

        // 1. Cargar datos auxiliares para los filtros (Dropdowns / Selects)
        // Mantenemos esto aquí para que Thymeleaf renderice las opciones del menú
        // y el usuario vea algo apenas carga la página.
        List<String> listaCategorias = categoriaServicio.getCategoriasUnicas();
        List<String> listaPaises = paisServicio.getPaisesUnicos();
        List<String> listaProvincias = provinciaServicio.getProvinciasUnicas();
        List<String> listaLocalidades = localidadServicio.getLocalidadesUnicas();
        List<ColeccionDTO> colecciones = coleccionServicio.getColecciones();
        OrigenCarga[] origenesDeCarga = OrigenCarga.values();

        // 2. Pasamos las listas al modelo
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("paises", listaPaises);
        model.addAttribute("provincias", listaProvincias);
        model.addAttribute("localidades", listaLocalidades);
        model.addAttribute("origenesDeCarga", origenesDeCarga);
        model.addAttribute("colecciones", colecciones);

        // 3. Configuración visual
        model.addAttribute("activePage", "navegar");

        // 4. ¡IMPORTANTE! YA NO enviamos la lista de "hechos".
        // El HTML debe tener un contenedor vacío (ej: <div id="listaHechos">)
        // y el JavaScript que hicimos antes se encargará de llenarlo llamando a GraphQL.

        return "navegar";
    }

}