package Modelos.Entidades;

import java.time.LocalDateTime ;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CriteriosDePertenencia")
public class CriteriosDePertenencia {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private Boolean multimedia;

    @ManyToOne
    @JoinColumn()
    private Categoria categoria;
    private LocalDateTime  fechaCargaDesde;
    private LocalDateTime  fechaCargaHasta;
    @ManyToOne
    @JoinColumn()
    private Ubicacion ubicacion;
    private LocalDateTime  fechaAcontecimientoDesde;
    private LocalDateTime  fechaAcontecimientoHasta;
    @Enumerated(EnumType.STRING)
    @JoinColumn()
    private OrigenCarga origen;

    public CriteriosDePertenencia(String titulo, Boolean multimedia, Categoria categoria, LocalDateTime  fechaCargaDesde, LocalDateTime  fechaCargaHasta, Ubicacion ubicacion, LocalDateTime  fechaAcontecimientoDesde, LocalDateTime  fechaAcontecimientoHasta, OrigenCarga origen_carga) {
        this.titulo = titulo;
        this.multimedia = multimedia;
        this.categoria = categoria;
        this.fechaCargaDesde = fechaCargaDesde;
        this.fechaCargaHasta = fechaCargaHasta;
        this.ubicacion = ubicacion;
        this.fechaAcontecimientoDesde = fechaAcontecimientoDesde;
        this.fechaAcontecimientoHasta = fechaAcontecimientoHasta;
        this.origen = origen_carga;
    }
    public  CriteriosDePertenencia(){}

}