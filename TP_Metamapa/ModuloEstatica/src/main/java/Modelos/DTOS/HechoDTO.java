package Modelos.DTOS;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime ;

@Getter
@Setter

public class HechoDTO {
    public Long idHecho = null;
    public Long idFuente ;
    public String titulo;
    public String descripcion;
    public String contenido = null;
    public String contenidoMultimedia = null;
    public String categoria;
    public LocalDateTime fechaAcontecimiento;
    public LocalDateTime fechaCarga = null;
    public String localidad = null;
    public String provincia = null;
    public String pais = null;
    public Double latitud;
    public Double longitud;
    public String usuario = null;
    public String nombre = null;
    public String apellido  = null;
    public LocalDateTime fecha_nacimiento = null;
    public Boolean anonimo = null;
    public Boolean visible = null;
    public String origen_carga = null;
    public Boolean mostrarNombre = null;
    public Boolean mostrarApellido = null;
    public Boolean mostrarFechaNacimiento = null;


    public HechoDTO(String titulo, String descripcion, Long id_fuente, String categoria, LocalDateTime fechaAcontecimiento, Double latitud, Double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idFuente = id_fuente;
        this.categoria = categoria;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}