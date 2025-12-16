package Modelos;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime ;

@Getter
@Setter

public class HechoDTO {
    public Long idHecho;
    public Long idFuente;
    public String titulo;
    public String descripcion;
    public String contenido;
    public String contenido_multimedia;
    public String categoria;
    public LocalDateTime fechaAcontecimiento;
    public LocalDateTime fechaCarga;
    public String nombre_localidad;
    public String nombre_provincia;
    public String nombre_pais;
    public Double latitud;
    public Double longitud;
    public String usuario;
    public String nombre;
    public String apellido;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    public LocalDate fecha_nacimiento;
    public Boolean anonimo;
    public Boolean visible;
    public String origen_carga;

    public Boolean mostrarNombre;
    public Boolean mostrarApellido;
    public Boolean mostrarFechaNacimiento;

    public HechoDTO(Long idHecho, Long idFuente, String titulo, String descripcion, String contenido, String contenido_multimedia, String categoria, LocalDateTime fechaAcontecimiento, LocalDateTime fechaCarga, String nombre_localidad, String nombre_provincia, String nombre_pais, Double latitud, Double longitud, String usuario, String nombre, String apellido, LocalDate fecha_nacimiento, Boolean anonimo, Boolean visible, String origen_carga,  Boolean mostrarNombre, Boolean mostrarApellido, 
                    Boolean mostrarFechaNacimiento) {
        this.idHecho = idHecho;
        this.idFuente = idFuente;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.contenido_multimedia = contenido_multimedia;
        this.categoria = categoria;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.fechaCarga = fechaCarga;
        this.nombre_localidad = nombre_localidad;
        this.nombre_provincia = nombre_provincia;
        this.nombre_pais = nombre_pais;
        this.latitud = latitud;
        this.longitud = longitud;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha_nacimiento = fecha_nacimiento;
        this.anonimo = anonimo;
        this.visible = visible;
        this.origen_carga = origen_carga;
        this.mostrarNombre = mostrarNombre;
        this.mostrarApellido = mostrarApellido;
        this.mostrarFechaNacimiento = mostrarFechaNacimiento;
    }

    public HechoDTO(){}
}
