package Controlador;

import Modelos.DTOs.HechoDTO;
import servicios.FuenteDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/demo")
public class FuenteDemoControler{

    @Autowired
    FuenteDemoService fuenteDemoService;


    @GetMapping("/hechos")
    public List<HechoDTO> obtenerHecho(){
        return fuenteDemoService.obtenerHecho();
    }
}