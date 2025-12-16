package Modelos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime ;

@Getter
@Setter
public class HechoDTOInput {
    public String titulo;
    public String descripcion;
    public String contenido;
    public String contenido_multimedia;
    public String categoria;
    public LocalDateTime fechaAcontecimiento;
    public String localidad;
    public String provincia;
    public String pais ;
    public Double latitud;
    public Double longitud;
    public String usuario;
    public String nombre;
    public String apellido;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    public LocalDate fechaNacimiento;
    public Boolean anonimo;
    public Boolean mostrarNombre;
    public Boolean mostrarApellido;
    public Boolean mostrarFechaNacimiento;

    public HechoDTOInput() {

    }
}
