package Modelos;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name= "Pais")
public class Pais {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPais;

    private String nombre_pais;

    public Pais() {}
    public Pais(String pais) {
        this.nombre_pais = pais;
    }

}
