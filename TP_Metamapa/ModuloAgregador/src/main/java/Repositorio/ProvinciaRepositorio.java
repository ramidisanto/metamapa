package Repositorio;

import Modelos.Entidades.Pais;
import Modelos.Entidades.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinciaRepositorio extends JpaRepository<Provincia, Long>{
    Optional<Provincia> findByProvinciaAndPais(String nombre, Pais pais);

    default Provincia buscarOCrear(String nombre, Pais pais) {
        if (nombre == null) return null;

        return findByProvinciaAndPais(nombre, pais)
                .orElseGet(() -> save(new Provincia(nombre, pais)));
    }
}