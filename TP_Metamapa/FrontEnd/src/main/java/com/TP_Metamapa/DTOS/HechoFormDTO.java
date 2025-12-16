package com.TP_Metamapa.DTOS;

import lombok.Data;
import java.time.LocalDate; // Para el input type="date"
import java.time.LocalDateTime;

@Data
public class HechoFormDTO {
    private String titulo;
    private String descripcion;
    private String contenidoAdicional;
    private String categoria;
    private LocalDateTime fechaAcontecimiento;
    private Double latitud;
    private Double longitud;
    private String pais;
    private String provincia;
    private String localidad;
    private Boolean anonimo = false;

    private String customCategoria;
    private Boolean mostrarNombre = false;
    private Boolean mostrarApellido = false;
    private Boolean mostrarFechaNacimiento = false;
}
