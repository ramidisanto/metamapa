package servicios;

import Modelos.DTOs.HechoNormalizadoDTO;
import Modelos.DTOs.HechoNormalizarDTO;
import Modelos.DTOs.UbicacionDTOoutput;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicioNormalizacion {

    private final ServicioCategoria servicioCategoria;
    private final ServicioTitulo servicioTitulo;
    private final ServicioUbicacion servicioUbicacion;

    public ServicioNormalizacion(
            ServicioCategoria servicioCategoria,
            ServicioTitulo servicioTitulo,
            ServicioUbicacion servicioUbicacion
    ) {
        this.servicioCategoria = servicioCategoria;
        this.servicioTitulo = servicioTitulo;
        this.servicioUbicacion = servicioUbicacion;
    }

    public List<HechoNormalizadoDTO> normalizarHechos(List<HechoNormalizarDTO> hechos) {

        // 🔹 cache local por batch (MUY importante)
        Map<String, UbicacionDTOoutput> cacheUbicaciones = new HashMap<>();

        return hechos.stream().map(h -> {
            System.out.println("ESTOY ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" );

            String categoria = servicioCategoria.normalizarCategoria(h.getCategoria());
            String titulo = servicioTitulo.normalizarTitulo(h.getTitulo());

            String key = h.getLatitud() + "," + h.getLongitud();

            System.out.println("ENTRE ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" );


            UbicacionDTOoutput ubicacion = cacheUbicaciones.computeIfAbsent(
                    key,
                    k -> servicioUbicacion.normalizarUbicacion(
                            h.getLatitud(),
                            h.getLongitud()
                    )

            );
            System.out.println("ME METI ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" );

            return new HechoNormalizadoDTO(
                    titulo,
                    categoria,
                    ubicacion.getPais(),
                    ubicacion.getProvincia(),
                    ubicacion.getLocalidad(),
                    h.getLatitud(),
                    h.getLongitud()
            );

        }).toList();
    }
}
