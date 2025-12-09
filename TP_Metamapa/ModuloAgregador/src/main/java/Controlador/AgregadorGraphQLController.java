package Controlador;
import Modelos.Entidades.DTOs.ColeccionDTO;
import Modelos.Entidades.DTOs.HechoDTOoutput;
import Modelos.Entidades.HechoFilterInput;
import Servicio.AgregadorServicio;
import Servicio.ColeccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
@Controller
public class AgregadorGraphQLController {
    @Autowired
    private AgregadorServicio agregadorServicio;
    @Autowired
    private ColeccionServicio coleccionServicio;

    @QueryMapping
    public List<HechoDTOoutput> listarHechos(@Argument HechoFilterInput filtro) {

        if (filtro == null) {
            filtro = new HechoFilterInput();
        }

        return agregadorServicio.filtrarHechos(
                filtro.getCategoria(),
                filtro.getContenidoMultimedia(),
                filtro.getFechaCargaDesde(),
                filtro.getFechaCargaHasta(),
                filtro.getFechaHechoDesde(),
                filtro.getFechaHechoHasta(),
                filtro.getOrigenCarga(),
                filtro.getTitulo(),
                filtro.getPais(),
                filtro.getProvincia(),
                filtro.getLocalidad()
                );
    }

    @QueryMapping
    public List<ColeccionDTO> listarColecciones() {
        return coleccionServicio.obtenerColecciones();
    }
}
