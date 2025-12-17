package Repositorio;

import Modelos.Entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContenidoRepositorio extends JpaRepository<Contenido, Long> {

    Optional<Contenido> findByTextoAndContenidoMultimedia(String texto, String contenidoMultimedia);

    default Contenido buscarOCrear(String texto, String contenidoMultimedia) {
        // En tu lógica original permitías devolver el primero encontrado
        return findByTextoAndContenidoMultimedia(texto, contenidoMultimedia)
                .orElseGet(() -> save(new Contenido(texto, contenidoMultimedia)));
    }
}

