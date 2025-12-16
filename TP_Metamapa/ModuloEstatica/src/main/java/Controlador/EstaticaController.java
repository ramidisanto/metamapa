package Controlador;
import Modelos.DTOS.HechoDTO;
import Modelos.Exceptions.ValidacionError;
import Servicio.FuenteEstatica;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/fuenteEstatica")
public class EstaticaController {
    private final FuenteEstatica fuenteEstatica;

    @Autowired
    public EstaticaController(FuenteEstatica fuenteEstatica) {
        this.fuenteEstatica = fuenteEstatica;
    }

    @GetMapping("/hechos")
    public ResponseEntity<?> devolverHechos() {
        try {
            List<HechoDTO> hechos = new ArrayList<>();
            hechos = fuenteEstatica.getHechosNoEnviados();
            return ResponseEntity.ok(hechos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay hechos disponibles");
        }
    }

    @PostMapping("/CSV")
    public ResponseEntity<?> uploadCSV(@RequestParam("csv") MultipartFile file) {
        try {
            System.out.println("Nombre: " + file.getOriginalFilename());
            System.out.println("Tamaño: " + file.getSize());
            fuenteEstatica.cargarCSV(file);
            return ResponseEntity.ok("Archivo guardado correctamente.");
        } catch (Exception e) {
            String mensaje = e.getMessage();
            HttpStatus status;
            if( e instanceof ValidacionError){
                status = HttpStatus.BAD_REQUEST;
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            return ResponseEntity.status(status).body("Error al cargar el archivo. " + e.getMessage());
        }
    }
}