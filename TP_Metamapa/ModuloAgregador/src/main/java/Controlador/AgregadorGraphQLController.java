package Controlador;
import Modelos.Entidades.DTOs.ColeccionDTO;
import Modelos.Entidades.DTOs.HechoDTOoutput;
import Modelos.Entidades.HechoFilterInput;
import Modelos.Entidades.PagedHechoResponse;
import Repositorio.CategoriaRepositorio;
import Repositorio.UbicacionRepositorio;
import Servicio.AgregadorGraphQLServicio;
import Servicio.AgregadorServicio;
import Servicio.ColeccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import Servicio.AgregadorGraphQLServicio;
import Servicio.ColeccionGraphQLServicio;

import java.util.List;
@Controller
public class AgregadorGraphQLController {
    @Autowired
    private AgregadorGraphQLServicio agregadorServicio;
    @Autowired
    private ColeccionGraphQLServicio coleccionServicio;
    @QueryMapping
    public PagedHechoResponse listarHechos(@Argument HechoFilterInput filtro) {
        if (filtro == null) filtro = new HechoFilterInput();

        return agregadorServicio.listarHechos(filtro);
    }

    @QueryMapping
    public List<ColeccionDTO> listarColecciones() {
        return coleccionServicio.listarColecciones();
    }

    @QueryMapping
    public HechoDTOoutput obtenerHecho(@Argument String id) {
        return agregadorServicio.obtenerHechoPorId(Long.parseLong(id));
    }
}