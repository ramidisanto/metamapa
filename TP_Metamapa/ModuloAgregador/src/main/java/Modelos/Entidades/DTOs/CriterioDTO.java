package Modelos.Entidades.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class CriterioDTO {
    public String titulo;
    public Boolean contenido_multimedia;
    public String categoria;
    public LocalDateTime  fechaCargaDesde;
    public LocalDateTime  fechaCargaHasta;
    public String localidad;
    public String provincia;
    public String pais;
    public LocalDateTime  fechaAcontecimientoDesde;
    public LocalDateTime  fechaAcontecimientoHasta;
    public String origen_carga;

    public CriterioDTO(String titulo, Boolean contenido_multimedia, String categoria, LocalDateTime  fechaCargaDesde, LocalDateTime  fechaCargaHasta, String localidad, String provincia, String pais, LocalDateTime  fechaAcontecimientoDesde, LocalDateTime  fechaAcontecimientoHasta, String origen_carga) {
        this.titulo = titulo;
        this.contenido_multimedia = contenido_multimedia;
        this.categoria = categoria;
        this.fechaCargaDesde = fechaCargaDesde;
        this.fechaCargaHasta = fechaCargaHasta;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
        this.fechaAcontecimientoDesde = fechaAcontecimientoDesde;
        this.fechaAcontecimientoHasta = fechaAcontecimientoHasta;
        this.origen_carga = origen_carga;
    }

    public CriterioDTO(){}
}
