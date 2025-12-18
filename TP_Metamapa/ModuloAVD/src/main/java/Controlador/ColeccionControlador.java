package Controlador;

import Modelos.DTOs.ColeccionDTO;
import Modelos.DTOs.ColeccionDTOOutput;
import Modelos.DTOs.FuenteDTO;
import Modelos.Exceptions.CriterioDuplicadoException;
import Servicio.ColeccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
public class ColeccionControlador {

    @Autowired
    private  ColeccionServicio coleccionServicio;

    @PreAuthorize("hasRole('admin_client_role')")
    @PostMapping("/coleccion")
    public ResponseEntity<String> crearColeccion(@RequestBody ColeccionDTO coleccionDTO) {
        try {
            coleccionServicio.crearColeccion(coleccionDTO);
            return ResponseEntity.status(200).body("Coleccion creada exitosamente");
        }catch(CriterioDuplicadoException e){
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(500).body("Error al crear la coleccion" + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('admin_client_role')")
    @DeleteMapping ("/coleccion/{id}")
    public ResponseEntity<String> eliminarColeccion (@PathVariable Long id) {
        try{
            coleccionServicio.eliminarColeccion(id);
            return ResponseEntity.status(200).body("Coleccion eliminada " +  id + " exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar la coleccion " + id + ":" + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping ("/coleccion/{id}")
    public ResponseEntity<?> obtenerColeccion (@PathVariable Long id) {
        try{
            return ResponseEntity.status(200).body(coleccionServicio.obtenerColeccion(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @DeleteMapping("/hecho/{id}")
    public ResponseEntity<String> eliminarHecho(@PathVariable Long id) {
        try {
            coleccionServicio.eliminarHecho(id);
            return ResponseEntity.status(200).body("Hecho eliminado exitosamente");
        } catch (Exception e){
            return ResponseEntity.status(500).body("Error al eliminar el hecho" + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @PutMapping ("/coleccion/{id}/Consenso/{estrategia}")
    public ResponseEntity<String> modificarAlgoritmoConsenso(@PathVariable Long id, @PathVariable String estrategia) {
        try{
            coleccionServicio.modificarConsenso(id, estrategia);
            return ResponseEntity.status(200).body("Consenso modificado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al modificar el consenso: " + e.getMessage());
        }
    }


}
