package Controlador;

import Servicio.EstadisticasServicio;
import Modelos.UltimasEstadisticasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/estadisticas")
public class EstadisticasControlador{

    @Autowired
    EstadisticasServicio estadisticasServicio;

    @GetMapping
    public ResponseEntity<UltimasEstadisticasDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticasServicio.obtenerEstadisticas());
    }

    @GetMapping("/csv")
    public ResponseEntity<String> exportarCSV() {
        String csv = estadisticasServicio.exportarCSV();
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=estadisticas.csv").body(csv);
    }


}
