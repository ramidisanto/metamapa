package Controlador;

import Modelos.DTOs.EstadoDTO;
import Modelos.DTOs.SolicitudDTOOutput;
import Modelos.Exceptions.EstadoInvalidoException;
import Modelos.Exceptions.HechoInvalidoException;
import Modelos.Exceptions.SolicitudInvalidaException;
import Servicio.SolicitudServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudControlador {

    @Autowired
    private SolicitudServicio solicitudServicio;

    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudDTOOutput>> obtenerSolicitudesPendientes(){
        try {
            return ResponseEntity.status(200).body(solicitudServicio.solicitudesPendientes());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(solicitudServicio.solicitudesPendientes());
        }
    }
    @PreAuthorize("hasRole('admin_client_role')")
    @PutMapping("/pendientes/{id}")
    public ResponseEntity<String> actualizarEstado(@PathVariable Long id, @RequestBody EstadoDTO estadoDTO){
        try {
            solicitudServicio.actualizarEstadoSolicitud(id, estadoDTO.getEstado());
            return ResponseEntity.status(200).body("Estado actualizado correctamente.");
        }catch (SolicitudInvalidaException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch(HechoInvalidoException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch(EstadoInvalidoException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }



}