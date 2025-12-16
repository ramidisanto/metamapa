package Modelos.Entidades.DTOs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime ;
import java.time.LocalDateTime ;

@Getter
@Setter

public class HechoDTOoutput {
    public Long idHecho;
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
    public String usuario;
    public String nombre;
    public String apellido;
    public Double latitud;
    public Double longitud;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    public LocalDate fecha_nacimiento;
    public String origen_carga;
    private Boolean mostrarNombre;
    private Boolean mostrarApellido;
    private Boolean mostrarFechaNacimiento;

    public HechoDTOoutput(Long idHecho, String titulo, String descripcion, String contenido, String contenido_multimedia, String categoria, LocalDateTime fechaAcontecimiento, LocalDateTime  fechaCarga, String localidad, String provincia, String pais, Double latitud, Double longitud, String usuario, String nombre, String apellido, LocalDate fecha_nacimiento, String origen_carga,  Boolean mostrarNombre, Boolean mostrarApellido, 
                    Boolean mostrarFechaNacimiento) {
        this.idHecho = idHecho;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.contenido_multimedia = contenido_multimedia;
        this.categoria = categoria;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.fechaCarga = fechaCarga;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
        this.latitud= latitud;
        this.longitud = longitud;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha_nacimiento = fecha_nacimiento;
        this.origen_carga = origen_carga;
        this.mostrarNombre = mostrarNombre;
        this.mostrarApellido = mostrarApellido;
        this.mostrarFechaNacimiento = mostrarFechaNacimiento;
    }
}
