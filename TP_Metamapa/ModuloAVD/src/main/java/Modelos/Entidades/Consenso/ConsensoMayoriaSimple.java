package Modelos.Entidades.Consenso;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("MAYORIA_SIMPLE")
public class ConsensoMayoriaSimple extends Consenso {


}
