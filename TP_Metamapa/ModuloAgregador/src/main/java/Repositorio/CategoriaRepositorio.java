package Repositorio;

import Modelos.Entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepositorio extends JpaRepository<Categoria, Long>{
    Optional<Categoria> findByNombre(String nombre);

    default Categoria buscarOCrear(String nombre) {
        if (nombre == null) return null;

        return findByNombre(nombre)
                .orElseGet(() -> save(new Categoria(nombre)));
    }

}