package Controlador;

import Modelos.Exceptions.ColeccionNoEncontradaException;
import Servicio.AgregadorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/agregador")
public class AgregadorController {

    @Autowired
    private AgregadorServicio agregadorServicio;

    @PostMapping("/colecciones/{id}")
    public ResponseEntity<String> cargarHechosAColeccion(@PathVariable Long id) {
        try {
            agregadorServicio.cargarColeccionConHechos(id);
            return ResponseEntity.status(200).body("Hechos agregados a la coleccion");
        } catch ( ColeccionNoEncontradaException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }
}
