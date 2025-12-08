package com.TP_Metamapa.DTOS;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

public class ColeccionDTO {
    public  Long coleccionId;
    public  String titulo;
    public  String descripcion;
    public  List<HechoDTO> hechos;
    public  CriterioDTO criterio;
    public  String consenso;
    public  List<HechoDTO> hechosConsensuados;

    public ColeccionDTO(Long coleccionId,String titulo, String descripcion, List<HechoDTO> hechos,CriterioDTO criterio, String consenso, List<HechoDTO> hechosConsensuados) {
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