package Modelos.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbicacionDTOoutput {

    private String pais;
    private String provincia;
    private String localidad;
    private Double latitud;
    private Double longitud;


    public UbicacionDTOoutput(String pais, String provicia, String localidad, Double latitud, Double longitud) {
        this.pais = pais;
        this.provincia = provicia;
        this.localidad = localidad;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public UbicacionDTOoutput() {}
}
