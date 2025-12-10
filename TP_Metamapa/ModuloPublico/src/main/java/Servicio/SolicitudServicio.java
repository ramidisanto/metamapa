package Servicio;

import Modelos.SolicitudDTOInput;
import Modelos.Entidades.Estado;
import Modelos.Entidades.Solicitud;
import Modelos.Entidades.Hecho;
import Repositorio.HechoRepositorio;
import Repositorio.SolicitudRepositorio;
import Servicio.Solicitudes.DetectorDeSpam;
import Servicio.Solicitudes.SolicitudInvalidaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime ;



@Service
public class SolicitudServicio {

    @Autowired
    SolicitudRepositorio solicitudRepositorio;
    @Autowired
    HechoRepositorio  hechoRepositorio;
    @Autowired
    DetectorDeSpam detectorDeSpam;

    public void crearSolicitud(SolicitudDTOInput solicituddto){
        LocalDateTime fechaSolicitud =  LocalDateTime.now();
        String motivo = solicituddto.getMotivo();
        Hecho hecho = hechoRepositorio.findById(solicituddto.getIdHecho()).orElse(null);
        if(hecho == null){
            throw new SolicitudInvalidaException("Hecho con id " + solicituddto.getIdHecho() + " no existe.");
        }

        if(!detectorDeSpam.esSpam(motivo)){
            Solicitud solicitud = new Solicitud(null, fechaSolicitud, motivo, hecho, Estado.PENDIENTE);
            solicitudRepositorio.save(solicitud);
        }
        else{
            Solicitud solicitud = new Solicitud(null, fechaSolicitud, null, hecho, Estado.SPAM); //Motivo en null para no guardar en BD motivo con más de 500 caracteres
            solicitudRepositorio.save(solicitud);
        }

    }



}
