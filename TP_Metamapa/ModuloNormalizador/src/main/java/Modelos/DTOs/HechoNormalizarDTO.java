package Modelos.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HechoNormalizarDTO {
    private String titulo;
    private String categoria;
    private Double latitud;
    private Double longitud;

    public HechoNormalizarDTO(String titulo, String categoria, Double latitud, Double longitud) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public  HechoNormalizarDTO() {
    }
}
