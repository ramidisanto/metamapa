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

    private String pais;

    public Pais() {}
    public Pais(String pais) {
        this.pais = pais;
    }

}
