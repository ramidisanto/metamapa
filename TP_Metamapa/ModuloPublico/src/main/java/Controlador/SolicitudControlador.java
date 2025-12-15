package Controlador;

import Modelos.SolicitudDTOInput;
import Servicio.SolicitudServicio;
import Servicio.Solicitudes.SolicitudInvalidaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudControlador {

    @Autowired
    SolicitudServicio solicitudServicio;
    @PreAuthorize("hasRole('user_client_role')")
    @PostMapping()
    public ResponseEntity<String> crearSolicitud(@RequestBody SolicitudDTOInput solicitud){
        try{
            solicitudServicio.crearSolicitud(solicitud);
            return ResponseEntity.ok("Se ha creado la solicitud.");
        }catch(SolicitudInvalidaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}