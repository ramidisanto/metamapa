package Servicio;

import Modelos.DTOs.HechoDTO;
import Modelos.DTOs.SolicitudDTOOutput;
import Modelos.Entidades.Estado;
import Modelos.Entidades.Hecho;
import Modelos.Entidades.Solicitud;
import Modelos.Exceptions.EstadoInvalidoException;
import Modelos.Exceptions.SolicitudInvalidaException;
import Repositorio.SolicitudRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;


@Service
public class SolicitudServicio {

    @Autowired
    SolicitudRepositorio solicitudRepositorio;
    @Autowired
    ColeccionServicio coleccionServicio;

    public List<SolicitudDTOOutput> solicitudesPendientes(){
        List<Solicitud> solicitudes = solicitudRepositorio.findByEstado(Estado.PENDIENTE);
        return solicitudes.stream().map(this::pasarADTO).toList();
    }
    private SolicitudDTOOutput pasarADTO(Solicitud solicitud){
        HechoDTO hecho = this.transformarHechoDTO(solicitud.getHecho());
        return new SolicitudDTOOutput(solicitud.getIdSolcitud().toString(), solicitud.getMotivo(), hecho , solicitud.getFecha_creacion());
    }


    public void actualizarEstadoSolicitud(Long idSolicitud, String nuevoEstado){
        Solicitud solicitud = solicitudRepositorio.findById(idSolicitud).orElse(null);
        if(solicitud == null){
            throw new SolicitudInvalidaException("La solicitud " + idSolicitud + "no existe.");
        }
        Estado estado = this.crearEstado(nuevoEstado);
        solicitud.setEstado(estado);
        if(estado == Estado.ACEPTADA){
            coleccionServicio.eliminarHecho(solicitud.getHecho().getId());
        }
        solicitudRepositorio.save(solicitud);
    }

    public Estado crearEstado(String estadoString){
        if(!Objects.equals(estadoString, "ACEPTADA") && !Objects.equals(estadoString, "RECHAZADA")){
            throw new EstadoInvalidoException("El estado no existe " + estadoString);
        }else{
            return Estado.valueOf(estadoString.toUpperCase());
        }
    }

    public HechoDTO transformarHechoDTO(Hecho hecho) {

        HechoDTO dto = new HechoDTO(
                hecho.getId(),
                hecho.getIdFuente(),
                hecho.getTitulo(),
                hecho.getDescripcion(),
                hecho.getContenido().getTexto(),
                hecho.getContenido().getContenidoMultimedia(),
                hecho.getCategoria().getNombre(),
                hecho.getFecha(),
                hecho.getUbicacion().getPais().getPais(),
                hecho.getUbicacion().getProvincia().getProvincia(),
                hecho.getUbicacion().getLocalidad().getLocalidad(),
                null,
                null,
                null,
                null,
                hecho.getOrigen().name(),
                hecho.getMostrarNombre(),
                hecho.getMostrarApellido(),
                hecho.getMostrarFechaNacimiento());

        if(Boolean.FALSE.equals(hecho.getAnonimo())){
            dto.setUsuario(hecho.getContribuyente().getUsuario());
            dto.setApellido(hecho.getContribuyente().getApellido());
            dto.setNombre(hecho.getContribuyente().getNombre());
            dto.setFecha_nacimiento(hecho.getContribuyente().getFecha_nacimiento());
        }
        return dto;
    }
}
