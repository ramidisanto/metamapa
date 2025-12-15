package Modelos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ubicacion {
    Localidad localidad;
    Provincia provincia;
    Pais pais;
    Double latitud;
    Double longitud;

    public void ubicacion() {
    }
}