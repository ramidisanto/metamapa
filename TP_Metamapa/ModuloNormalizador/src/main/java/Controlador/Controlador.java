package Controlador;

import Modelos.DTOs.HechoNormalizadoDTO;
import Modelos.DTOs.HechoNormalizarDTO;
import Modelos.DTOs.UbicacionDTO;
import Modelos.DTOs.UbicacionDTOoutput;
import servicios.ServicioCategoria;
import servicios.ServicioNormalizacion;
import servicios.ServicioTitulo;
import servicios.ServicioUbicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/normalizacion")
public class Controlador {

    @Autowired
    private ServicioCategoria servicioCategoria;

    @Autowired
    private ServicioUbicacion servicioUbicacion;

    @Autowired
    private ServicioTitulo servicioTitulo;

    @Autowired
    private ServicioNormalizacion servicioNormalizacion;

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

    @PostMapping("/hechos")
    public ResponseEntity<List<HechoNormalizadoDTO>> normalizarHechos(
            @RequestBody List<HechoNormalizarDTO> hechos
    ) {
        return ResponseEntity.ok(
                servicioNormalizacion.normalizarHechos(hechos)
        );
    }

    @PostMapping("/batch")
    public ResponseEntity<List<HechoNormalizadoDTO>> normalizarBatch(
            @RequestBody List<HechoNormalizarDTO> hechos) {

        List<HechoNormalizadoDTO> resultado =
                hechos.stream().map(h -> {

                    HechoNormalizadoDTO out = new HechoNormalizadoDTO();

                    out.setTitulo(servicioTitulo.normalizarTitulo(h.getTitulo()));
                    out.setCategoria(servicioCategoria.normalizarCategoria(h.getCategoria()));

                    var ubicacion = servicioUbicacion.normalizarUbicacion(
                            h.getLatitud(),
                            h.getLongitud()
                    );

                    out.setPais(ubicacion.getPais());
                    out.setProvincia(ubicacion.getProvincia());
                    out.setLocalidad(ubicacion.getLocalidad());
                    out.setLatitud(ubicacion.getLatitud());
                    out.setLongitud(ubicacion.getLongitud());

                    return out;
                }).toList();
        return ResponseEntity.ok(resultado);
    }


}
