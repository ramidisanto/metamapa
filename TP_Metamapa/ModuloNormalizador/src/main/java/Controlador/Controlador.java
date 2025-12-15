package Controlador;

import Modelos.DTOs.UbicacionDTO;
import Modelos.DTOs.UbicacionDTOoutput;
import servicios.ServicioCategoria;
import servicios.ServicioTitulo;
import servicios.ServicioUbicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/normalizacion")
public class Controlador {

    @Autowired
    private ServicioCategoria servicioCategoria;

    @Autowired
    private ServicioUbicacion servicioUbicacion;

    @Autowired
    private ServicioTitulo servicioTitulo;

    @PostMapping("/categorias")
    public ResponseEntity<String> normalizarCategoria(@RequestBody String categoria) {
        return ResponseEntity.ok(servicioCategoria.normalizarCategoria(categoria));
    }

    @PostMapping("/ubicaciones")
    public ResponseEntity<UbicacionDTOoutput> normalizarUbicacion(@RequestBody UbicacionDTO ubicacion) {
        return ResponseEntity.ok(
                servicioUbicacion.normalizarUbicacion(
                        ubicacion.getLatitud(),
                        ubicacion.getLongitud()
                )
        );
    }

    @PostMapping("/titulos")
    public ResponseEntity<String> normalizarTitulo(@RequestBody String titulo) {
        return ResponseEntity.ok(servicioTitulo.normalizarTitulo(titulo));
    }
}