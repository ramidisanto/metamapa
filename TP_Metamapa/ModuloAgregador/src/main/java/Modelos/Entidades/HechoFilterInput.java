package Modelos.Entidades; // O el paquete que prefieras

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HechoFilterInput {
    private String idColeccion;
    private String categoria;
    private Boolean contenidoMultimedia;
    private String fechaCargaDesde;
    private String fechaCargaHasta;
    private String fechaHechoDesde;
    private String fechaHechoHasta;
    private Integer size;
    private Integer page;
    private String origenCarga;
    private String titulo;
    private String pais;
    private String provincia;
    private String localidad;
    private Boolean navegacionCurada;

    private String busquedaGeneral;
}