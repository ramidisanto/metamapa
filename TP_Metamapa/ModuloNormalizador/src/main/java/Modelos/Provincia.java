package Modelos;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="Provincia")
public class Provincia {
    String provincia;
    @ManyToOne
    @JoinColumn()
    Pais pais;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProvincia;

    public Provincia() {}


    public Provincia(String nombre_provincia, Pais pais) {
        this.provincia = nombre_provincia;
        this.pais = pais;
    }

}


