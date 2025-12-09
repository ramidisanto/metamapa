package Modelos.Entidades; // O el paquete que prefieras

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HechoFilterInput {
    private Long idColeccion;
    private String categoria;
    private Boolean contenidoMultimedia;

    private LocalDateTime fechaCargaDesde;
    private LocalDateTime fechaCargaHasta;
    private LocalDateTime fechaHechoDesde;
    private LocalDateTime fechaHechoHasta;

    private String origenCarga;
    private String titulo;
    private String pais;
    private String provincia;
    private String localidad;
    private Boolean navegacionCurada;
}