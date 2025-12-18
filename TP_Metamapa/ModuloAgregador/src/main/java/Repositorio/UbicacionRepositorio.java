package Repositorio;

import Modelos.Entidades.Localidad;
import Modelos.Entidades.Pais;
import Modelos.Entidades.Provincia;
import Modelos.Entidades.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Long> {
    Optional<Ubicacion> findByLatitudAndLongitud(Double latitud, Double longitud);

    default Ubicacion buscarOCrear(Double latitud, Double longitud, Localidad localidad, Provincia provincia, Pais pais) {
        if (latitud == null || longitud == null) return null;

        return findByLatitudAndLongitud(latitud, longitud)
                .orElseGet(() -> save(new Ubicacion(localidad, provincia, pais, latitud, longitud)));
    }
}
