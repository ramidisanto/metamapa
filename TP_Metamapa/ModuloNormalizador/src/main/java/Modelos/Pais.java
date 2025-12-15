package Modelos;

import lombok.Getter;

@Getter
public class Pais {
    String nombre_pais;

    public Pais(String nombre_pais) {
        this.nombre_pais = nombre_pais;
    }
}