package Modelos;

import lombok.Getter;

@Getter
public class Provincia {
    String nombre_provincia;
    Pais pais;

    public Provincia(String nombre_provincia, Pais pais) {
        this.nombre_provincia = nombre_provincia;
        this.pais = pais;
    }
}