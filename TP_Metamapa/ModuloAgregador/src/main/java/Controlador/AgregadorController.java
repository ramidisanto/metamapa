package Controlador;

import Modelos.Exceptions.ColeccionNoEncontradaException;
import Servicio.AgregadorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agregador/")
public class AgregadorController {

    @Autowired
    AgregadorServicio agregadorServicio;
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
