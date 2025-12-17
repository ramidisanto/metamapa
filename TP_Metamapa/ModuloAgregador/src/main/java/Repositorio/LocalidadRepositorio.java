package Repositorio;

import Modelos.Entidades.Localidad;
import Modelos.Entidades.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalidadRepositorio extends JpaRepository<Localidad, Long> {
    Optional<Localidad> findByLocalidadAndProvincia(String nombre, Provincia provincia);

    default Localidad buscarOCrear(String nombre, Provincia provincia) {
        if (nombre == null) return null;

        return findByLocalidadAndProvincia(nombre, provincia)
                .orElseGet(() -> save(new Localidad(nombre, provincia)));
    }
}
