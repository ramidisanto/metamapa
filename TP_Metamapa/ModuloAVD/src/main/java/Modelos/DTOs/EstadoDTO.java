package Modelos.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoDTO {
    private String estado;

    public EstadoDTO(String estado){
        this.estado = estado;
    }
    public EstadoDTO(){}
}