package Modelos.DTOs;

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
    public String pais;
    public String provincia;
    public String localidad;
    public String usuario;
    public String nombre;
    public String apellido;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    public LocalDate fecha_nacimiento;
    public String origen_carga;
    private Boolean mostrarNombre;
    private Boolean mostrarApellido;
    private Boolean mostrarFechaNacimiento;

    public HechoDTO(Long idHecho, Long idFuente,String titulo, String descripcion, String contenido, String contenido_multimedia, String categoria, LocalDateTime fechaAcontecimiento, String pais,String provincia, String localidad,  String usuario, String nombre, String apellido, LocalDate fecha_nacimiento, String origen_carga,  Boolean mostrarNombre, Boolean mostrarApellido, 
                    Boolean mostrarFechaNacimiento) {
        this.idHecho = idHecho;
        this.idFuente = idFuente;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.contenido_multimedia = contenido_multimedia;
        this.categoria = categoria;
        this.fechaAcontecimiento = fechaAcontecimiento;
        this.pais = pais;
        this.provincia = provincia;
        this.localidad = localidad;
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
