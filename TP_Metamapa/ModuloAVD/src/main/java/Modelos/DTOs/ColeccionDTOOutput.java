package Modelos.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ColeccionDTOOutput {
    private Long coleccionId;
    private  String titulo;
    private  String descripcion;
    private  CriterioDTO criterio;
    private List<HechoDTO> hechos;
    private String consenso;
    private List<HechoDTO> hechosConsensuados ;

    public ColeccionDTOOutput(Long coleccionId, String titulo, String descripcion, CriterioDTO criterio, List<HechoDTO> hechos, String consenso, List<HechoDTO> hechosConsensuados) {
        this.coleccionId = coleccionId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.criterio = criterio;
        this.hechos = hechos;
        this.consenso = consenso;
        this.hechosConsensuados = hechosConsensuados;
    }
}