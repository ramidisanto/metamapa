package Repositorio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import Modelos.Entidades.*;

import java.math.BigDecimal;
import java.time.LocalDateTime ;
import java.util.List;

@Repository
public interface HechosRepositorio extends JpaRepository<Hecho, Long> {

    List<Hecho> findAllByProcesadoFalse();

    @Query("""
    SELECT COUNT(*)
    FROM Hecho h
    WHERE h.archivo.id = :id
      AND h.titulo = :titulo
      AND h.descripcion = :descripcion
      AND h.categoria =:categoria
      AND h.fechaAcontecimiento = :fechaAcontecimiento
      AND TRIM(h.longitud) = TRIM(:longitud)
                AND TRIM(h.latitud) = TRIM(:latitud)
      
    """)

    Integer noExisteHecho(
            @Param("id") Long id,
            @Param("titulo") String titulo,
            @Param("descripcion") String descripcion,
            @Param("categoria") String categoria,
            @Param("latitud") String latitud,
           @Param("longitud") String longitud,
           @Param("fechaAcontecimiento") LocalDateTime fechaAcontecimiento
    );

}



