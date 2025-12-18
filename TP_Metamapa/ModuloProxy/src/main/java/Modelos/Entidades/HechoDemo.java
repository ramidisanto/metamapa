package Modelos.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime ;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "HechosDemo")
public class HechoDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHecho;
    @ManyToOne
    @JoinColumn()
    private Fuente fuente;
    @Column(length = 1000)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private String contenido;
    private String contenido_multimedia;
    private String categoria;
    private LocalDateTime fechaAcontecimiento;
    private LocalDateTime fechaCarga;
    private String localidad;
    private String provincia;
    private String pais;
    private Double latitud;
    private Double longitud;
    private Boolean publicado;


    public HechoDemo(){}
}