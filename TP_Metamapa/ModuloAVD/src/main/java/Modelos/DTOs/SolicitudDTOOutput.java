package Modelos.DTOs;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime ;


@Getter
@Setter
public class SolicitudDTOOutput {
    private String idSolicitud;
    private String motivo;
    private HechoDTO hecho;
    private LocalDateTime fechaCreacion;

    public SolicitudDTOOutput(String idSolicitud, String motivo, HechoDTO hecho, LocalDateTime fechaCreacion){
        this.idSolicitud = idSolicitud;
        this.motivo = motivo;
        this.hecho = hecho;
        this.fechaCreacion = fechaCreacion;
    }
}