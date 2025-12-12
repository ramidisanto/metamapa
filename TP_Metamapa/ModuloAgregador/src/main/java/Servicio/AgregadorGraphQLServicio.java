package Servicio;

import Modelos.Entidades.DTOs.HechoDTOoutput;
import Modelos.Entidades.Hecho;
import Modelos.Entidades.HechoFilterInput; // Tu clase/record de input
import Modelos.Entidades.OrigenCarga;
import Repositorio.HechoRepositorio;
import Repositorio.ColeccionRepositorio; // Para buscar por ID de colección
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgregadorGraphQLServicio {

    @Autowired
    private HechoRepositorio hechoRepositorio;

    @Autowired
    private ColeccionRepositorio coleccionRepositorio;

    @Transactional(readOnly = true)
    public List<HechoDTOoutput> listarHechos(HechoFilterInput filtro) {
        List<Hecho> hechos;

        if (filtro.getIdColeccion() != null /*&& !filtro.getIdColeccion().isEmpty()*/) {
            // Caso A: Buscar dentro de una colección específica
            //Long idCol = Long.parseLong(filtro.getIdColeccion());
            Long idCol = Long.parseLong(filtro.getIdColeccion());
            hechos = new ArrayList<>(coleccionRepositorio.findById(idCol)
                    .map(c -> c.getHechos())
                    .orElse(new ArrayList<>()));
        } else {
            // Caso B: Búsqueda general en la base de datos usando filtros

            // Conversión segura de String a Enum
            OrigenCarga origenEnum = null;
            if (filtro.getOrigenCarga() != null && !filtro.getOrigenCarga().isBlank()) {
                try {
                    origenEnum = OrigenCarga.valueOf(filtro.getOrigenCarga().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Origen desconocido ignorado: " + filtro.getOrigenCarga());
                }
            }


            LocalDateTime cargaDesde = parseFecha(filtro.getFechaCargaDesde());
            LocalDateTime cargaHasta = parseFecha(filtro.getFechaCargaHasta());
            LocalDateTime hechoDesde = parseFecha(filtro.getFechaHechoDesde()); // O getFechaAcontecimientoDesde
            LocalDateTime hechoHasta = parseFecha(filtro.getFechaHechoHasta());



            hechos = hechoRepositorio.filtrarHechos(
                    filtro.getCategoria(),
                    filtro.getContenidoMultimedia(),
                    cargaDesde, cargaHasta,
                    hechoDesde, hechoHasta,
                    origenEnum,
                    filtro.getTitulo(),
                    filtro.getPais(),
                    filtro.getProvincia(),
                    filtro.getLocalidad()
            );
        }

        // 2. Filtrado en Memoria (Búsqueda de texto libre)
        // Esto cubre campos que quizas el repositorio no busca con LIKE (descripcion, contenido)
        if (filtro.getBusquedaGeneral() != null && !filtro.getBusquedaGeneral().isBlank()) {
            String query = filtro.getBusquedaGeneral().toLowerCase();
            hechos = hechos.stream()
                    .filter(h -> buscarEnTexto(h, query))
                    .collect(Collectors.toList());
        }

        // 3. Conversión a DTO
        return hechos.stream()
                .map(this::convertirAHechoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HechoDTOoutput obtenerHechoPorId(Long id) {
        return hechoRepositorio.findById(id)
                .map(this::convertirAHechoDTO)
                .orElse(null);
    }

    // --- Métodos Privados de Utilidad ---

    private LocalDateTime parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) return null;
        try {
            // Intenta parsear ISO-8601 (ej: 2023-10-05T14:30:00)
            return LocalDateTime.parse(fechaStr);
        } catch (Exception e) {
            // Si falla, podrías intentar LocalDate.parse(fechaStr).atStartOfDay()
            return null;
        }
    }

    private boolean buscarEnTexto(Hecho h, String query) {
        if (h.getTitulo() != null && h.getTitulo().toLowerCase().contains(query)) return true;
        if (h.getDescripcion() != null && h.getDescripcion().toLowerCase().contains(query)) return true;
        if (h.getContenido() != null && h.getContenido().getTexto() != null
                && h.getContenido().getTexto().toLowerCase().contains(query)) return true;
        return false;
    }

    // Método PÚBLICO para que el servicio de Colecciones pueda reusarlo
    public HechoDTOoutput convertirAHechoDTO(Hecho h) {
        String loc = null, prov = null, pais = null;
        Double lat = null, lon = null;

        if (h.getUbicacion() != null) {
            if (h.getUbicacion().getLocalidad() != null) loc = h.getUbicacion().getLocalidad().getLocalidad();
            if (h.getUbicacion().getProvincia() != null) prov = h.getUbicacion().getProvincia().getProvincia();
            if (h.getUbicacion().getPais() != null) pais = h.getUbicacion().getPais().getPais();
            lat = h.getUbicacion().getLatitud();
            lon = h.getUbicacion().getLongitud();
        }

        String contenidoTxt = null, contenidoImg = null;
        if (h.getContenido() != null) {
            contenidoTxt = h.getContenido().getTexto();
            contenidoImg = h.getContenido().getContenidoMultimedia();
        }

        String usuario = null, nombre = null, apellido = null;
        LocalDate nac = null;
        if (h.getContribuyente() != null) {
            usuario = h.getContribuyente().getUsuario();
            nombre = h.getContribuyente().getNombre();
            apellido = h.getContribuyente().getApellido();
            nac = h.getContribuyente().getFecha_nacimiento();
        }

        return new HechoDTOoutput(
                h.getId(),
                h.getTitulo(),
                h.getDescripcion(),
                contenidoTxt,
                contenidoImg,
                (h.getCategoria() != null) ? h.getCategoria().getNombre() : null,
                h.getFecha(),
                h.getFecha_carga(),
                loc, prov, pais,
                lat, lon,
                usuario, nombre, apellido, nac,
                (h.getOrigen() != null) ? h.getOrigen().name() : null
        );
    }
}