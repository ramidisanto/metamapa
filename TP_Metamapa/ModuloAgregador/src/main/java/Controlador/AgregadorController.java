package Controlador;

import Servicio.AgregadorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/agregador")
public class AgregadorController {

    @Autowired
    private AgregadorServicio agregadorServicio;

    @PostMapping("/actualizar-todo")
    public ResponseEntity<?> actualizarBaseDeDatos() {
        // Llama al método que dispara el proceso async
        agregadorServicio.actualizarHechos();

        // Retorna INMEDIATAMENTE. El usuario ve este mensaje en milisegundos.
        return ResponseEntity.accepted().body(Map.of(
                "mensaje", "Actualización iniciada en segundo plano. Los hechos aparecerán progresivamente.",
                "estado", "PROCESANDO"
        ));
    }
}
