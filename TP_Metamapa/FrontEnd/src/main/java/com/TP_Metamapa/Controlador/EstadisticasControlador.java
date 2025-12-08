package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.ColeccionDTO;
import com.TP_Metamapa.DTOS.UltimasEstadisticasDTO;
import com.TP_Metamapa.Servicio.ColeccionServicio;
import com.TP_Metamapa.Servicio.EstadisticasServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class EstadisticasControlador {

    @Autowired
    private EstadisticasServicio estadisticasServicio;
    @Autowired
    private ColeccionServicio coleccionServicio;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(
            Model model
    ) {
        List<ColeccionDTO> colecciones = coleccionServicio.getColecciones();
        UltimasEstadisticasDTO estadisticas = estadisticasServicio.obtenerEstadisticas();

        Map<Long, String> mapaNombresColecciones = colecciones.stream()
                .collect(Collectors.toMap(
                        ColeccionDTO::getColeccionId,
                        ColeccionDTO::getTitulo
                ));


        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("mapaNombresColecciones", mapaNombresColecciones);

        return "estadisticas";
    }

    @GetMapping("/csv")
    public ResponseEntity<String> generarCSV() {
        String csv = estadisticasServicio.exportarCSV();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=estadisticas.csv")
                .body(csv);
    }
}