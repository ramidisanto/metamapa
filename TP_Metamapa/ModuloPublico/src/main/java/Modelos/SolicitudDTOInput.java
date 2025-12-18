package Modelos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SolicitudDTOInput {
    private String motivo;
    private Long idHecho;

    public SolicitudDTOInput(String motivo, Long idHecho) {
        this.motivo = motivo;
        this.idHecho = idHecho;
    }

    public SolicitudDTOInput(){}
}