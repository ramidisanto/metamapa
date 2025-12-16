package Servicio;


import Modelos.Entidades.*;
import Modelos.Entidades.DTOs.*;
import Modelos.Entidades.Consenso.Consenso;
import Repositorio.ColeccionRepositorio;
import Repositorio.HechoRepositorio;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColeccionServicio {


    ColeccionRepositorio coleccionRepositorio;
    @Autowired
    HechoRepositorio hechoRepositorio;


    public ColeccionServicio(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Transactional
    public void actualizarHechosConsensuados() {
       for (Coleccion coleccion : coleccionRepositorio.findAll()) {
           Hibernate.initialize(coleccion.getHechos());
           Consenso consenso = coleccion.getConsenso();
           List<Hecho> hechos;
           if (consenso != null) {
               hechos = coleccion.getHechos().stream()
                       .filter(h -> consenso.tieneConsenso(h, hechoRepositorio))
                       .collect(Collectors.toCollection(ArrayList::new));
           } else {
               hechos = new ArrayList<>(coleccion.getHechos());
           }
            coleccion.setHechosConsensuados(hechos);
            coleccionRepositorio.save(coleccion);

       }
    }
    @Transactional(readOnly = true)
    public List<ColeccionDTO> obtenerColecciones() {
        List<Coleccion> colecciones = coleccionRepositorio.findAll();

        return colecciones.stream()
                .map(this::convertirAColeccionDTO)
                .collect(Collectors.toList());
    }

    private ColeccionDTO convertirAColeccionDTO(Coleccion c) {

        // 1. Convertir lista de hechos
        List<HechoDTOoutput> hechosDTO = new ArrayList<>();
        if (c.getHechos() != null) {
            // Inicializar proxy si es lazy (opcional si usas Transactional)
            Hibernate.initialize(c.getHechos());

            hechosDTO = c.getHechos().stream()
                    .map(this::convertirAHechoDTO)
                    .collect(Collectors.toList());
        }

        // 2. Convertir lista de hechos consensuados
        List<HechoDTOoutput> hechosConsensuadosDTO = new ArrayList<>();
        if (c.getHechosConsensuados() != null) {
            Hibernate.initialize(c.getHechosConsensuados());

            hechosConsensuadosDTO = c.getHechosConsensuados().stream()
                    .map(this::convertirAHechoDTO)
                    .collect(Collectors.toList());
        }

        // 3. Convertir Criterio
        CriterioDTO criterioDTO = null;
        if (c.getCriterio_pertenencia() != null) {
            criterioDTO = convertirACriterioDTO(c.getCriterio_pertenencia());
        }

        return new ColeccionDTO(
                c.getId(),
                c.getTitulo(),
                c.getDescripcion(),
                hechosDTO,
                criterioDTO,
                (c.getConsenso() != null) ? c.getConsenso().getClass().getSimpleName() : "Ninguno",
                hechosConsensuadosDTO
        );
    }

    private CriterioDTO convertirACriterioDTO(CriteriosDePertenencia cp) {
        String loc = null, prov = null, pais = null;

        // Asumiendo que CriteriosDePertenencia tiene un objeto Ubicacion anidado o campos planos
        // Ajusta esto según tu entidad CriteriosDePertenencia real.
        // Ejemplo asumiendo objeto Ubicacion:
        if (cp.getUbicacion() != null) {
            if(cp.getUbicacion().getLocalidad() != null) loc = cp.getUbicacion().getLocalidad().getLocalidad();
            if(cp.getUbicacion().getProvincia() != null) prov = cp.getUbicacion().getProvincia().getProvincia();
            if(cp.getUbicacion().getPais() != null) pais = cp.getUbicacion().getPais().getPais();
        }

        String origenStr = (cp.getOrigen() != null) ? cp.getOrigen().name() : null;

        return new CriterioDTO(
                cp.getTitulo(),
                cp.getMultimedia(),
                (cp.getCategoria() != null) ? cp.getCategoria().getNombre() : null,
                cp.getFechaCargaDesde(),
                cp.getFechaCargaHasta(),
                loc,
                prov,
                pais,
                cp.getFechaAcontecimientoDesde(),
                cp.getFechaAcontecimientoHasta(),
                origenStr
        );
    }
    public HechoDTOoutput convertirAHechoDTO(Hecho h) {

        // Manejo de nulos para Ubicación
        String  loc = null, prov = null, pais = null;
        Double lat = null, lon = null;
        if (h.getUbicacion() != null) {
            // calle = h.getUbicacion().getCalle(); // Si tu entidad Ubicacion tiene calle
            if (h.getUbicacion().getLocalidad() != null) loc = h.getUbicacion().getLocalidad().getLocalidad();
            if (h.getUbicacion().getProvincia() != null) prov = h.getUbicacion().getProvincia().getProvincia();
            if (h.getUbicacion().getPais() != null) pais = h.getUbicacion().getPais().getPais();
            if (h.getUbicacion().getLatitud() != null) lat = h.getUbicacion().getLatitud();
            if (h.getUbicacion().getLatitud() != null) lon = h.getUbicacion().getLongitud();
        }

        // Manejo de nulos para Contenido
        String textoContenido = null;
        String urlMultimedia = null;
        if (h.getContenido() != null) {
            textoContenido = h.getContenido().getTexto();
            urlMultimedia = h.getContenido().getContenidoMultimedia();
        }

        // Manejo de nulos para Contribuyente (Tu entidad usa 'contribuyente', no 'autor')
        String usuario = null, nombre = null, apellido = null;
        LocalDate fechaNac = null;
        if (h.getContribuyente() != null) { // <--- CORREGIDO: getContribuyente()
            usuario = h.getContribuyente().getUsuario();
            nombre = h.getContribuyente().getNombre();
            apellido = h.getContribuyente().getApellido();
            fechaNac = h.getContribuyente().getFecha_nacimiento();
        }

        // Construcción del DTO output
        return new HechoDTOoutput(
                h.getId(),
                h.getTitulo(),
                h.getDescripcion(),
                textoContenido,
                urlMultimedia,
                (h.getCategoria() != null) ? h.getCategoria().getNombre() : null,
                h.getFecha(),
                h.getFecha_carga(),
                loc,
                prov,
                pais,
                lat,
                lon,
                usuario,
                nombre,
                apellido,
                fechaNac,
                (h.getOrigen() != null) ? h.getOrigen().name() : null,
                h.getMostrarNombre(),
                h.getMostrarApellido(),
                h.getMostrarFechaNacimiento()
        );
    }
}




