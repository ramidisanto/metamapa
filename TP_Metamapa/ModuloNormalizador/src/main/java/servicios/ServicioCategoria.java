package servicios;

import Repositorio.RepositorioCategoria;
import org.springframework.stereotype.Service;
import Modelos.Categoria;
import Utils.TextoUtils;

@Service
public class ServicioCategoria {

    private final RepositorioCategoria repositorioCategoria;

    public ServicioCategoria(RepositorioCategoria repositorioCategoria) {
        this.repositorioCategoria = repositorioCategoria;
    }

    public String normalizarCategoria(String nombreCategoria) {
        String normalizada = TextoUtils.capitalizarCadaPalabra(nombreCategoria);
        if(repositorioCategoria.findByNombre(normalizada)!=null){
            return normalizada;
        }
        Categoria categoria = repositorioCategoria.save(new Categoria(normalizada));
        return categoria.getNombre();
    }
}
