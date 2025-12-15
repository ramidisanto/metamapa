package Modelos.Entidades;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime ;


@Getter
@Setter
@Entity
@Table(name = "Hecho")
public class Hecho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idHecho;
    public Long idfuente;
    @Column(length = 1000)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn()
    public Contenido contenido;
    @ManyToOne()
    @JoinColumn()
    public Categoria categoria;
    public LocalDateTime fecha;
    @ManyToOne()
    @JoinColumn()
    public Ubicacion ubicacion;
    @ManyToOne()
    @JoinColumn()
    public Contribuyente contribuyente;
    public Boolean anonimo;
    public Boolean visible;
    public Boolean publicado;

    public Hecho(Long idHecho, Long idfuente, String unTitulo, String unaDescripcion, Contenido unContenido, Categoria unaCategoria, LocalDateTime unaFechaOcurrencia,
                 Ubicacion unaUbicacion, Contribuyente contribuyente, Boolean anonimo, Boolean visible, Boolean publicado) {
        this.idHecho = idHecho;
        this.idfuente = idfuente;
        this.titulo = unTitulo;
        this.descripcion = unaDescripcion;
        this.contenido = unContenido;
        this.categoria = unaCategoria;
        this.fecha = unaFechaOcurrencia;
        this.ubicacion = unaUbicacion;
        this.visible = visible;
        this.contribuyente = contribuyente;
        this.anonimo = anonimo;
        this.publicado = publicado;
    }

    public Hecho() {}

    public void modificarVisibilidad(){
        visible = false;
    }

}