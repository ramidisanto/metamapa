package Repositorio;

import Modelos.Hecho;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HechoRepositorio extends JpaRepository<Hecho, Long> {

    @Query("""
        SELECT FUNCTION('HOUR', h.fecha) as hora
        FROM Hecho h
        WHERE h.categoria.nombre = :categoria
        GROUP BY hora
        ORDER BY COUNT(h.id) DESC
    """)
    List<Integer> getHoraConMasHechos(@Param("categoria") String categoria, Pageable pageable);




}