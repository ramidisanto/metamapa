package Modelos.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime ;


@Getter
@Setter
@Entity
@Table(name="Hecho")
public class Hecho{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long idFuente;
    @Column(length = 1000)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn()
    public Contenido contenido;

    @ManyToOne
    @JoinColumn()
    public Categoria categoria;

    public LocalDateTime fecha;

    @ManyToOne
    @JoinColumn()
    public Ubicacion ubicacion;

    public LocalDateTime  fecha_carga;

    @Enumerated(EnumType.STRING)
    @Column()
    public OrigenCarga origen;

    public Boolean visible;

    @ManyToOne
    @JoinColumn()
    private Contribuyente contribuyente;

    public Boolean anonimo ;

    private Boolean mostrarNombre;
    private Boolean mostrarApellido;
    private Boolean mostrarFechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_normalizacion")
    private EstadoNormalizacion estadoNormalizacion = EstadoNormalizacion.PENDIENTE;


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
        this.mostrarNombre = mostrarNombre;
        this.mostrarApellido = mostrarApellido;
        this.mostrarFechaNacimiento = mostrarFechaNacimiento;
        //this.estadoNormalizacion = EstadoNormalizacion.PENDIENTE;
    }
    public Hecho(){}

}
