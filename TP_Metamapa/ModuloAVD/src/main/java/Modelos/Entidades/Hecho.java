package Modelos.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime ;
import java.time.LocalDateTime ;


@Getter
@Setter
@Entity
@Table(name = "Hecho")
public class Hecho{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Cambiar a long

    private Long idFuente; // Cambiar a long
    @Column(length = 1000)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn()
    private Contenido contenido;

    @ManyToOne
    @JoinColumn()
    private Categoria categoria;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn()
    private Ubicacion ubicacion;

    private LocalDateTime  fecha_carga;

    @Enumerated(EnumType.STRING)
    @Column()
    private OrigenCarga origen; //enum

    private Boolean visible ;

    @ManyToOne
    @JoinColumn()
    private Contribuyente contribuyente;

    private Boolean anonimo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_normalizacion")
    private EstadoNormalizacion estadoNormalizacion = EstadoNormalizacion.PENDIENTE;

    private Boolean mostrarNombre;
    private Boolean mostrarApellido;
    private Boolean mostrarFechaNacimiento;



    public Hecho() {}

    public Hecho(Long id, Long idFuente, String unTitulo, String unaDescripcion, Contenido unContenido, Categoria unaCategoria, LocalDateTime unaFechaOcurrencia,
                 Ubicacion unaUbicacion, LocalDateTime  unaFechaCarga, OrigenCarga unOrigen, Boolean estaVisible, Contribuyente contribuyente, Boolean anonimo,  Boolean mostrarNombre, Boolean mostrarApellido, 
                    Boolean mostrarFechaNacimiento){ //Lista etiquetas
        this.id = id;
        this.idFuente = idFuente;
        this.titulo = unTitulo;
        this.descripcion = unaDescripcion;
        this.contenido = unContenido;
        this.categoria = unaCategoria;
        this.fecha = unaFechaOcurrencia;
        this.ubicacion = unaUbicacion;
        this.fecha_carga = unaFechaCarga;
        this.origen = unOrigen;
        this.visible = estaVisible;
        this.contribuyente = contribuyente;
        this.anonimo = anonimo;
        //this.estadoNormalizacion = EstadoNormalizacion.PENDIENTE;
        this.mostrarNombre = mostrarNombre;
        this.mostrarApellido = mostrarApellido;
        this.mostrarFechaNacimiento = mostrarFechaNacimiento;
    }


    public void eliminarse(){
        visible = false;
    }


}
