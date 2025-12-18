package Controlador;

import Modelos.DTOs.HechoDTO;

import servicios.FuenteMetaMapaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metamapa")
public class MetaMapaControler {

    @Autowired
    FuenteMetaMapaService metaMapaServicio;


    @GetMapping("/hechos")
    public List<HechoDTO> obtenerHechos() {
        return metaMapaServicio.obtenerHechos();
    }


}