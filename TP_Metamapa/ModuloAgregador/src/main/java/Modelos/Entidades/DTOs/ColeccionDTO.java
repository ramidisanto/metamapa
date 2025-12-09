package Modelos.Entidades.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ColeccionDTO {
    private  Long coleccionId;
    private  String titulo;
    private  String descripcion;
    private  List<HechoDTOoutput> hechos;
    private  CriterioDTO criterio;
    private  String consenso;
    private  List<HechoDTOoutput> hechosConsensuados;

    public ColeccionDTO(Long coleccionId, String titulo, String descripcion, List<HechoDTOoutput> hechos, CriterioDTO criterio, String consenso, List<HechoDTOoutput> hechosConsensuados) {
        this.coleccionId = coleccionId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.hechos = hechos;
        this.criterio = criterio;
        this.consenso = consenso;
        this.hechosConsensuados = hechosConsensuados;
    }

    public ColeccionDTO() {}
}