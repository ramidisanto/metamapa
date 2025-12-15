package Modelos.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionDTO
{
    Double latitud;
    Double longitud;

    public UbicacionDTO(){}

    public UbicacionDTO(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

}
