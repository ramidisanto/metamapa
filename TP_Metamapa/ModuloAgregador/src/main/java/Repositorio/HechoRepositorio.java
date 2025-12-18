package Repositorio;

import Modelos.Entidades.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface HechoRepositorio extends JpaRepository<Hecho, Long> {

    @Query("""
                SELECT COUNT(DISTINCT(CONCAT(h.idFuente, '-', h.origen)))
                FROM Hecho h
                WHERE h.titulo = :titulo
                  AND h.categoria = :categoria
                  AND h.fecha = :fecha
                  AND h.ubicacion = :ubicacion
            """)
    Long cantidadDeFuentesConHecho(
            @Param("titulo") String titulo,
            @Param("categoria") Categoria categoria,
            @Param("fecha") LocalDateTime fecha,
            @Param("ubicacion") Ubicacion ubicacion);

    @Query(value = """
                SELECT COUNT(DISTINCT CONCAT(h.idFuente, '-', h.origen))
                FROM Hecho h
            """, nativeQuery = true) //
    Long cantidadFuentes();

    @Query("""
                SELECT COUNT(DISTINCT(CONCAT(h.idFuente, '-', h.origen)))
                FROM Hecho h
                WHERE h.titulo = :titulo
                AND (
                     h.descripcion != :descripcion
                  OR h.categoria != :categoria
                  OR h.fecha != :fecha
                  OR h.ubicacion != :ubicacion
                  OR h.contribuyente != :contribuyente
                  OR h.contenido != :contenido)
            """)
    Long cantidadDeFuentesConMismoTituloDiferentesAtributos(
            @Param("titulo") String titulo,
            @Param("descripcion") String descripcion,
            @Param("categoria") Categoria categoria,
            @Param("fecha") LocalDateTime fecha,
            @Param("ubicacion") Ubicacion ubicacion,
            @Param("contribuyente") Contribuyente contribuyente,
            @Param("contenido") Contenido contenido);

    
    @Query("""
            SELECT h FROM Hecho h
            LEFT JOIN h.categoria cat
            LEFT JOIN h.contenido cont
            LEFT JOIN h.ubicacion ubi
            LEFT JOIN ubi.pais p
            LEFT JOIN ubi.provincia prov
            LEFT JOIN ubi.localidad loc
            WHERE (:categoria IS NULL OR cat.nombre = :categoria)
            AND (
                :contenidoMultimedia IS NULL
                OR (:contenidoMultimedia = TRUE AND cont.contenidoMultimedia IS NOT NULL AND cont.contenidoMultimedia <> '')
                OR (:contenidoMultimedia = FALSE AND (cont.contenidoMultimedia IS NULL OR cont.contenidoMultimedia = ''))
            )
            AND (:fechaCargaDesde IS NULL OR h.fecha_carga >= :fechaCargaDesde)
            AND (:fechaCargaHasta IS NULL OR h.fecha_carga <= :fechaCargaHasta)
            AND (:fechaHechoDesde IS NULL OR h.fecha >= :fechaHechoDesde)
            AND (:fechaHechoHasta IS NULL OR h.fecha <= :fechaHechoHasta)
            AND (:origenCarga IS NULL OR h.origen = :origenCarga)
            AND (:titulo IS NULL OR h.titulo LIKE %:titulo%)
            AND (:pais IS NULL OR p.pais = :pais)
            AND (:provincia IS NULL OR prov.provincia = :provincia)
            AND (:localidad IS NULL OR loc.localidad = :localidad)
            AND (h.visible = true)
            AND (h.estadoNormalizacion = 'NORMALIZADO')
            """)
    List<Hecho> buscarHechosPorFiltros(
            @Param("categoria") String categoria,
            @Param("contenidoMultimedia") Boolean contenidoMultimedia,
            @Param("fechaCargaDesde") LocalDateTime fechaCargaDesde,
            @Param("fechaCargaHasta") LocalDateTime fechaCargaHasta,
            @Param("fechaHechoDesde") LocalDateTime fechaHechoDesde,
            @Param("fechaHechoHasta") LocalDateTime fechaHechoHasta,
            @Param("origenCarga") OrigenCarga origenCarga,
            @Param("titulo") String titulo,
            @Param("pais") String pais,
            @Param("provincia") String provincia,
            @Param("localidad") String localidad);


   @Query("""
    SELECT DISTINCT h
    FROM Hecho h
    LEFT JOIN h.contenido c
    LEFT JOIN h.ubicacion u
    LEFT JOIN u.pais p
    LEFT JOIN u.provincia prov
    LEFT JOIN u.localidad l
    LEFT JOIN h.categoria cat
    WHERE
        (:idColeccion IS NULL
         OR EXISTS (
            SELECT 1 
            FROM Coleccion col 
            WHERE col.id = :idColeccion 
            AND h MEMBER OF col.hechos
         ))
    AND (:categoria IS NULL OR cat.nombre = :categoria)
    AND (
        :contenidoMultimedia IS NULL
        OR (:contenidoMultimedia = TRUE AND c.contenidoMultimedia IS NOT NULL AND c.contenidoMultimedia <> '')
        OR (:contenidoMultimedia = FALSE AND (c.contenidoMultimedia IS NULL OR c.contenidoMultimedia = ''))
    )
    AND (cast(:fechaCargaDesde as timestamp) IS NULL OR h.fecha_carga >= :fechaCargaDesde)
    AND (cast(:fechaCargaHasta as timestamp) IS NULL OR h.fecha_carga <= :fechaCargaHasta)
    AND (cast(:fechaHechoDesde as timestamp) IS NULL OR h.fecha >= :fechaHechoDesde)
    AND (cast(:fechaHechoHasta as timestamp) IS NULL OR h.fecha <= :fechaHechoHasta)
    AND (:origenCarga IS NULL OR h.origen = :origenCarga)
    AND (:titulo IS NULL OR LOWER(h.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
    AND (:pais IS NULL OR p.pais = :pais)
    AND (:provincia IS NULL OR prov.provincia = :provincia)
    AND (:localidad IS NULL OR l.localidad = :localidad)
    AND (
        :busquedaGeneral IS NULL
        OR LOWER(h.titulo) LIKE LOWER(CONCAT('%', :busquedaGeneral, '%'))
        OR LOWER(h.descripcion) LIKE LOWER(CONCAT('%', :busquedaGeneral, '%'))
        OR (c.texto IS NOT NULL AND LOWER(c.texto) LIKE LOWER(CONCAT('%', :busquedaGeneral, '%')))
    )
    AND (h.visible = true)
    AND (h.estadoNormalizacion = 'NORMALIZADO' OR :busquedaGeneral IS NOT NULL) 
""")
Page<Hecho> filtrarHechos(
        @Param("idColeccion") Long idColeccion,
        @Param("categoria") String categoria,
        @Param("contenidoMultimedia") Boolean contenidoMultimedia,
        @Param("fechaCargaDesde") LocalDateTime fechaCargaDesde,
        @Param("fechaCargaHasta") LocalDateTime fechaCargaHasta,
        @Param("fechaHechoDesde") LocalDateTime fechaHechoDesde,
        @Param("fechaHechoHasta") LocalDateTime fechaHechoHasta,
        @Param("origenCarga") OrigenCarga origenCarga,
        @Param("titulo") String titulo,
        @Param("pais") String pais,
        @Param("provincia") String provincia,
        @Param("localidad") String localidad,
        @Param("busquedaGeneral") String busquedaGeneral,
        Pageable pageable);


        @Query("""
        SELECT DISTINCT h
        FROM Hecho h
        LEFT JOIN h.contenido c
        LEFT JOIN h.ubicacion u
        LEFT JOIN u.pais p
        LEFT JOIN u.provincia prov
        LEFT JOIN u.localidad l
        LEFT JOIN h.categoria cat
        WHERE
            (:idColeccion IS NULL
             OR EXISTS (
                SELECT 1 
                FROM Coleccion col 
                WHERE col.id = :idColeccion 
                AND h MEMBER OF col.hechosConsensuados
             ))
        AND (:categoria IS NULL OR cat.nombre = :categoria)
        AND (
            :contenidoMultimedia IS NULL
            OR (:contenidoMultimedia = TRUE AND c.contenidoMultimedia IS NOT NULL AND c.contenidoMultimedia <> '')
            OR (:contenidoMultimedia = FALSE AND (c.contenidoMultimedia IS NULL OR c.contenidoMultimedia = ''))
        )
        AND (cast(:fechaCargaDesde as timestamp) IS NULL OR h.fecha_carga >= :fechaCargaDesde)
        AND (cast(:fechaCargaHasta as timestamp) IS NULL OR h.fecha_carga <= :fechaCargaHasta)
        AND (cast(:fechaHechoDesde as timestamp) IS NULL OR h.fecha >= :fechaHechoDesde)
        AND (cast(:fechaHechoHasta as timestamp) IS NULL OR h.fecha <= :fechaHechoHasta)
        AND (:origenCarga IS NULL OR h.origen = :origenCarga)
        AND (:titulo IS NULL OR LOWER(h.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
        AND (:pais IS NULL OR p.pais = :pais)
        AND (:provincia IS NULL OR prov.provincia = :provincia)
        AND (:localidad IS NULL OR l.localidad = :localidad)
        AND (
            :busquedaGeneral IS NULL
            OR LOWER(h.titulo) LIKE LOWER(CONCAT('%', :busquedaGeneral, '%'))
            OR LOWER(h.descripcion) LIKE LOWER(CONCAT('%', :busquedaGeneral, '%'))
            OR (c.texto IS NOT NULL AND LOWER(c.texto) LIKE LOWER(CONCAT('%', :busquedaGeneral, '%')))
        )
        AND (h.visible = true)
        AND (h.estadoNormalizacion = 'NORMALIZADO' OR :busquedaGeneral IS NOT NULL) 
    """)
        Page<Hecho> filtrarHechosCurados(
                @Param("idColeccion") Long idColeccion,
                @Param("categoria") String categoria,
                @Param("contenidoMultimedia") Boolean contenidoMultimedia,
                @Param("fechaCargaDesde") LocalDateTime fechaCargaDesde,
                @Param("fechaCargaHasta") LocalDateTime fechaCargaHasta,
                @Param("fechaHechoDesde") LocalDateTime fechaHechoDesde,
                @Param("fechaHechoHasta") LocalDateTime fechaHechoHasta,
                @Param("origenCarga") OrigenCarga origenCarga,
                @Param("titulo") String titulo,
                @Param("pais") String pais,
                @Param("provincia") String provincia,
                @Param("localidad") String localidad,
                @Param("busquedaGeneral") String busquedaGeneral,
                Pageable pageable);


}


