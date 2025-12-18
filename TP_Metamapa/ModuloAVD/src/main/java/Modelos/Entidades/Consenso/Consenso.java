package Modelos.Entidades.Consenso;
import Modelos.Entidades.Hecho;
import Repositorio.HechoRepositorio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_consenso")
public abstract class Consenso {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    protected Consenso() {
    }

}
