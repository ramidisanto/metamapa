package Repositorio;

import Modelos.Entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ColeccionRepositorio extends JpaRepository<Coleccion, Long> {

    @Query("SELECT DISTINCT c FROM Coleccion c " +
            "LEFT JOIN FETCH c.criterio_pertenencia " +
            "LEFT JOIN FETCH c.hechos")
    List<Coleccion> findAllWithRelations();

}
