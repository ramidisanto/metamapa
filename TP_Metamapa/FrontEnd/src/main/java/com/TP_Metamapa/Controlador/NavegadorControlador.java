package com.TP_Metamapa.Controlador;

import com.TP_Metamapa.DTOS.ColeccionDTO;
import com.TP_Metamapa.Modelos.OrigenCarga;
import com.TP_Metamapa.Servicio.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;

@Controller
public class NavegadorControlador {

    @Autowired ColeccionServicio coleccionServicio;
    @Autowired CategoriaServicio categoriaServicio;
    @Autowired LocalidadServicio localidadServicio;
    @Autowired ProvinciaServicio provinciaServicio;
    @Autowired PaisServicio paisServicio;

    @GetMapping("/navegar")
    public String navegar(Model model, HttpServletRequest request) {

        model.addAttribute("categorias", categoriaServicio.getCategoriasUnicas());
        model.addAttribute("paises", paisServicio.getPaisesUnicos());
        model.addAttribute("provincias", provinciaServicio.getProvinciasUnicas());
        model.addAttribute("localidades", localidadServicio.getLocalidadesUnicas());

        Map<String, String> origenesDeCargaMap = new LinkedHashMap<>();
        origenesDeCargaMap.put("FUENTE_ESTATICA", "Archivos de ONGs");
        origenesDeCargaMap.put("FUENTE_DINAMICA", "Usuarios");
        origenesDeCargaMap.put("FUENTE_PROXY", "Servicios Externos");
        model.addAttribute("origenesDeCarga", origenesDeCargaMap);


        model.addAttribute("colecciones", coleccionServicio.getColecciones());

        boolean isAdmin = request.isUserInRole("admin_client_role");
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("activePage", "navegar");

        // Apunta a tu archivo HTML
        return "navegarGraphQL";
    }
}