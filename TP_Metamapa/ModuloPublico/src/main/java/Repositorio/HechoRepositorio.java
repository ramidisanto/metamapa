package Repositorio;
import Modelos.Entidades.OrigenCarga;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import Modelos.Entidades.Hecho;

import java.time.LocalDateTime ;
import java.util.List;


@Repository
public interface HechoRepositorio extends JpaRepository<Hecho, Long> {

    @Query("SELECT h FROM Hecho h " +
            "WHERE (:categoria IS NULL OR :categoria = '' OR h.categoria.nombre = :categoria) " +
            "AND (" +
            "    :contenidoMultimedia IS NULL" +
            "    OR (:contenidoMultimedia = TRUE AND h.contenido.contenidoMultimedia IS NOT NULL AND h.contenido.contenidoMultimedia <> '')" +
            "    OR (:contenidoMultimedia = FALSE AND (h.contenido.contenidoMultimedia IS NULL OR h.contenido.contenidoMultimedia = ''))" +
            ") " +
            "AND (:fechaCargaDesde IS NULL OR h.fecha_carga >= :fechaCargaDesde) " +
            "AND (:fechaCargaHasta IS NULL OR h.fecha_carga<= :fechaCargaHasta) " +
            "AND (:fechaHechoDesde IS NULL OR h.fecha>= :fechaHechoDesde) " +
            "AND (:fechaHechoHasta IS NULL OR h.fecha <= :fechaHechoHasta) " +
            "AND (:origenCarga IS NULL  OR :origenCarga = '' OR h.origen = :origenCarga) " +
            "AND (:titulo IS NULL OR h.titulo LIKE %:titulo%) " +
            "AND (:pais IS NULL OR :pais = '' OR h.ubicacion.pais.pais = :pais) " +
            "AND (:provincia IS NULL OR :provincia = '' OR h.ubicacion.provincia.provincia = :provincia) " +
            "AND (:localidad IS NULL OR :localidad = '' OR h.ubicacion.localidad.localidad = :localidad)"+
            "AND (h.visible = true)")
    List<Hecho> filtrarHechos(
            @Param("categoria") String categoria,
            @Param("contenidoMultimedia") Boolean contenidoMultimedia,
            @Param("fechaCargaDesde") LocalDateTime  fechaCargaDesde,
            @Param("fechaCargaHasta") LocalDateTime  fechaCargaHasta,
            @Param("fechaHechoDesde") LocalDateTime fechaHechoDesde,
            @Param("fechaHechoHasta") LocalDateTime fechaHechoHasta,
            @Param("origenCarga") OrigenCarga origenCarga,
            @Param("titulo") String titulo,
            @Param("pais") String pais,
            @Param("provincia") String provincia,
            @Param("localidad") String localidad
    );

    @Query("SELECT h FROM Hecho h WHERE h.visible = true") //para lo del texto libre
    List<Hecho> buscarTodosVisibles();

   @Query("SELECT h FROM Hecho h WHERE h.visible = true ORDER BY h.fecha_carga DESC")
   List<Hecho> findUltimos(Pageable pageable);
}
