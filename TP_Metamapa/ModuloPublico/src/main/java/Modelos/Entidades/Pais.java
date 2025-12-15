package Modelos.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name= "Pais")
public class Pais {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPais;

    String pais;

    public Pais() {}
}
