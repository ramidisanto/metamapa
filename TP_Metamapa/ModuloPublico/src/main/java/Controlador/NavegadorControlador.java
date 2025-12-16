package Controlador;

import Modelos.ColeccionDTO;
import Modelos.Entidades.Excepciones.ColeccionNotFoundException;
import Modelos.Entidades.Excepciones.HechosNoEncontradosException;
import Modelos.HechoDTO;
import Servicio.NavegadorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime ;
import java.util.List;


@RestController
@RequestMapping("publico/")

public class NavegadorControlador {

    @Autowired
    NavegadorServicio navegadorServicio;


    @GetMapping("colecciones/{id}/hechos")
    public ResponseEntity<?> coleccionFiltrada(@PathVariable Long id,
                                               @RequestParam(required = false) String categoria,
                                               @RequestParam(required = false) Boolean contenidoMultimedia,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaCargaDesde,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaCargaHasta,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fechaHechoDesde,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fechaHechoHasta,
                                               @RequestParam(required = false) String origen,
                                               @RequestParam(required = false) String titulo,
                                               @RequestParam(required = false) String pais,
                                               @RequestParam(required = false) String provincia,
                                               @RequestParam(required = false) String localidad,
                                               @RequestParam(required = false) Boolean navegacionCurada) {

        try {

            if (titulo != null) {
                titulo = URLDecoder.decode(titulo, StandardCharsets.UTF_8.toString());
            }
            if (origen != null) {
                origen = URLDecoder.decode(origen, StandardCharsets.UTF_8.toString());
            }
            if (pais != null) {
                pais = URLDecoder.decode(pais, StandardCharsets.UTF_8.toString());
            }
            if (localidad != null) {
                localidad = URLDecoder.decode(localidad, StandardCharsets.UTF_8.toString());
            }
            if ( provincia != null) {
                provincia = URLDecoder.decode(provincia, StandardCharsets.UTF_8.toString());
            }
            if (categoria != null) {
                categoria = URLDecoder.decode(categoria, StandardCharsets.UTF_8.toString());
            }

            List<HechoDTO> hechos = navegadorServicio.filtrarHechos(id, categoria, contenidoMultimedia,
                    fechaCargaDesde, fechaCargaHasta, fechaHechoDesde, fechaHechoHasta,
                    origen, titulo, pais, provincia, localidad, navegacionCurada);

            return ResponseEntity.ok(hechos);
        } catch (ColeccionNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("hechos/{id}")
    public  ResponseEntity<?> obtenerHechoPorId(@PathVariable Long id){
        try {
            HechoDTO hecho = navegadorServicio.obtenerHechoPorId(id);
            return ResponseEntity.ok(hecho);
        } catch (HechosNoEncontradosException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("hechos/recientes")
    public ResponseEntity<?> obtenerOchoMasRecientes(){
        List<HechoDTO> hechos = navegadorServicio.obtenerHechosRecientes();
        return ResponseEntity.ok(hechos);
    }

    @GetMapping("hechos")
    public ResponseEntity<?> hechosFiltrados(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean contenidoMultimedia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaCargaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  fechaCargaHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHechoDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHechoHasta,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String localidad) {

        try {
            if (titulo != null) {
                titulo = URLDecoder.decode(titulo, StandardCharsets.UTF_8.toString());
            }
            if (origen != null) {
                origen = URLDecoder.decode(origen, StandardCharsets.UTF_8.toString());
            }
            if (pais != null) {
                pais = URLDecoder.decode(pais, StandardCharsets.UTF_8.toString());
            }
            if (localidad != null) {
                localidad = URLDecoder.decode(localidad, StandardCharsets.UTF_8.toString());
            }
            if ( provincia != null) {
                provincia = URLDecoder.decode(provincia, StandardCharsets.UTF_8.toString());
            }
            if (categoria != null) {
                categoria = URLDecoder.decode(categoria, StandardCharsets.UTF_8.toString());
            }
            List<HechoDTO> hechos = navegadorServicio.filtrarHechos(null, categoria, contenidoMultimedia,
                    fechaCargaDesde, fechaCargaHasta, fechaHechoDesde, fechaHechoHasta,
                    origen, titulo, pais, provincia, localidad, null);

            return ResponseEntity.ok(hechos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al filtrar hechos");
        }
    }
    

    @GetMapping("/buscar/{texto}")
    public ResponseEntity<?> buscarPorTextoLibre(@PathVariable String texto){
        try {
            List<HechoDTO> hechos = navegadorServicio.buscarPorTextoLibre(texto);
            return ResponseEntity.ok(hechos);
        } catch (HechosNoEncontradosException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar hechos");
        }
    }

    @GetMapping("/paises")
    public ResponseEntity<List<String>> listaDePaises(){
            List<String> paises = navegadorServicio.obtenerPaises();
            return ResponseEntity.ok(paises);
    }
    @GetMapping("/provincias")
    public ResponseEntity<List<String>> listaDeProvincias(){
        List<String> provincias = navegadorServicio.obtenerProvincias();
        return ResponseEntity.ok(provincias);
    }
    @GetMapping("/localidades")
    public ResponseEntity<List<String>> listaDeLocalidades(){
        List<String> localidades = navegadorServicio.obtenerLocalidades();
        return ResponseEntity.ok(localidades);
    }
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listaDeCategorias(){
        List<String> categorias = navegadorServicio.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/colecciones")
    public ResponseEntity<List<ColeccionDTO>> listaDeColecciones(){
        List<ColeccionDTO> colecciones = navegadorServicio.obtenerColecciones();
        return ResponseEntity.ok(colecciones);
    }

}