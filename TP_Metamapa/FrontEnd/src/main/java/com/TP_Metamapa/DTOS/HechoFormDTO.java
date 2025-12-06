package com.TP_Metamapa.DTOS;

import lombok.Data;
import java.time.LocalDate; // Para el input type="date"
import java.time.LocalDateTime;

@Data // Genera getters, setters, etc.
public class HechoFormDTO {
    private String titulo;
    private String descripcion;
    private String contenidoAdicional;
    private String categoria;
    private LocalDate fechaAcontecimiento; // Recibe 'yyyy-MM-dd' del form
    private Double latitud;
    private Double longitud;
    private String pais;
    private String provincia;
    private String localidad;
    private boolean anonimo;

    private String customCategoria;
}
