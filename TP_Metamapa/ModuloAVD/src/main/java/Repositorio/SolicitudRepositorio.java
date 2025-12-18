package Repositorio;

import Modelos.Entidades.Estado;
import Modelos.Entidades.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SolicitudRepositorio extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByEstado(Estado estado);
}
