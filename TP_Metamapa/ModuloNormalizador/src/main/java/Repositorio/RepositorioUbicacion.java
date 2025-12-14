package Repositorio;

import Modelos.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUbicacion extends JpaRepository<Ubicacion, Long> {
    Optional<Ubicacion> findByLatitudAndLongitud(Double lat, Double lon);
}
