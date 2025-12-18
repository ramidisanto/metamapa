package Modelos.Entidades.Consenso;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("ABSOLUTA")
public class ConsensoAbsoluta extends Consenso {


}

