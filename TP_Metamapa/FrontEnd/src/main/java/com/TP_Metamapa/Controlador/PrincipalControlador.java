package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.HechoDTO;
import com.TP_Metamapa.Servicio.HechoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PrincipalControlador {

    @Autowired
    HechoServicio hechoService;

    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        List<HechoDTO> hechosDePrueba = hechoService.hechosRecientes();
//
        model.addAttribute("hechos", hechosDePrueba);

        model.addAttribute("activePage", "inicio");
        return "principal";
    }
    @GetMapping("/rate-limit-error")
    public String rateLimitError() {
        return "error/429";
    }

}