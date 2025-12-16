package com.TP_Metamapa.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HechoDTO {
        public Long idHecho;
        public Long idFuente;//VER COMO MANEJAR ESTO
        public String titulo;
        public String descripcion;
        public String contenido;
        public String contenido_multimedia;
        public String categoria;
        public LocalDateTime fechaAcontecimiento;
        public LocalDateTime  fechaCarga;
        public String localidad;
        public String provincia;
        public String pais;
        public Double latitud;
        public Double longitud;
        public String usuario;
        private String nombre; // Nombre del usuario
        private String apellido; // Apellido del usuario
        @DateTimeFormat(pattern = "dd/MM/yyyy")
        private LocalDate fecha_nacimiento;
        public Boolean anonimo;
        public Boolean visible;
        public String origen_carga;
        public Boolean mostrarNombre;
        public Boolean mostrarApellido;
        public Boolean mostrarFechaNacimiento;

}
