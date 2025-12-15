package Repositorio;

import Modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCategoria extends JpaRepository <Categoria, Long> {
    Categoria findByNombre(String nombre);
}
