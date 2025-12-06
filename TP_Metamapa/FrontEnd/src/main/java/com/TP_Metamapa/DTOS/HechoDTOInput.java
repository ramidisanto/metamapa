package com.TP_Metamapa.DTOS;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class HechoDTOInput {
    // Campos que coinciden con el backend
    private String titulo;
    private String descripcion;
    private String contenido; // Asegúrate si es 'contenido' o 'contenidoAdicional' en el backend
    private String contenido_multimedia; // ¡Será la RUTA (String)!
    private String categoria;
    private LocalDateTime fechaAcontecimiento; // ¡LocalDateTime!
    private String localidad;
    private String provincia;
    private String pais;
    private Double latitud;
    private Double longitud;
    private String usuario;
    private String nombre;
    private String apellido;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;
    private Boolean anonimo;

    public HechoDTOInput() {

    }
}
