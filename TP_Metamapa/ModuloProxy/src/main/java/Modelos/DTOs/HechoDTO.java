
package Modelos.DTOs;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime ;
import java.time.LocalDate ;

@Getter
@Setter
public class HechoDTO {
    private Long idHecho = null;
    private Long idFuente;
    private String titulo;
    private String descripcion;
    private String contenido;
    private String contenido_multimedia;
    private String categoria;
    private LocalDateTime fechaAcontecimiento;
    private LocalDateTime  fechaCarga = null;
    private String localidad;
    private String provincia;
    private String pais;
    private Double latitud;
    private Double longitud;
    private String usuario = null;
    private String nombre = null;
    private String apellido = null;
    private LocalDate fecha_nacimiento = null;
    private Boolean anonimo = null;
    private Boolean visible = null;
    private String origen_carga = null;
    private Boolean mostrarNombre = null;
    private Boolean mostrarApellido = null;
    private Boolean mostrarFechaNacimiento = null;

    public HechoDTO(String titulo, String descripcion, String contenido, String contenido_multimedia, String categoria,
                    LocalDateTime fechaAcontecimiento, String localidad, String provincia, String pais,
                    Double latitud, Double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.contenido_multimedia = contenido_multimedia;
        this.categoria = categoria;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public HechoDTO() {
        // constructor vacío para usar con Jackson/serialización
    }
}
