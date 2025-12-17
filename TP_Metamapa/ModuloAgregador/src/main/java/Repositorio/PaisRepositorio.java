package Repositorio;

import Modelos.Entidades.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepositorio extends JpaRepository<Pais, Long> {
    Optional<Pais> findByPais(String nombre);


    default Pais buscarOCrear(String nombre) {
        if (nombre == null) return null;

        return findByPais(nombre)
                .orElseGet(() -> save(new Pais(nombre)));
    }
}
