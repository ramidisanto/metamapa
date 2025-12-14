package Modelos.Entidades.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HechoNormalizadoDTO {
    private String titulo;
    private String categoria;
    private String pais;
    private String provincia;
    private String localidad;
    private Double latitud;
    private Double longitud;

    public HechoNormalizadoDTO(String titulo, String categoria, String pais, String provincia, String localidad, Double latitud, Double longitud) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.pais = pais;
        this.provincia = provincia;
        this.localidad = localidad;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
