package Modelos;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "Ubicacion")
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUbicacion;

    @ManyToOne
    @JoinColumn()
    Localidad localidad;

    @ManyToOne
    @JoinColumn()
    Provincia provincia;

    @ManyToOne
    @JoinColumn()
    Pais pais;

    Double latitud;
    Double longitud;

    public Ubicacion(Localidad localidad, Provincia provincia, Pais pais, Double latitud, Double longitud) {
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Ubicacion() {}
}
