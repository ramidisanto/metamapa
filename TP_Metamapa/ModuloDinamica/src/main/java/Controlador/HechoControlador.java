package Controlador;


import Modelos.HechoDTO;
import Modelos.HechoDTOInput;
import servicios.HechoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;


@RestController
@RequestMapping("/dinamica")
public class HechoControlador {

    @Autowired
     HechoServicio hechoServicio;

    @PostMapping("/hechos")
    public ResponseEntity<String> crearHecho(@RequestBody HechoDTOInput hechoDTO) {
        System.out.println("ENTRO A EL CONTROLADOR DE CREAR HECHO");
        try {

            hechoServicio.crearHecho(hechoDTO);
            return ResponseEntity.ok("Hecho creado exitosamente.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/hechos")
    public List<HechoDTO> obtenerHechos() {
        return hechoServicio.obtenerHechos();
    }

    @GetMapping("/hechos/pendientes")
    public ResponseEntity<List<HechoDTO>> obtenerHechosPendientesDeUsuario(@RequestParam String username) {
        try {
            System.out.println("Username recibido: " + username);
            List<HechoDTO> hechosPendientes = hechoServicio.obtenerHechosPendientesDeUsuario(username);
            return ResponseEntity.ok(hechosPendientes);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}