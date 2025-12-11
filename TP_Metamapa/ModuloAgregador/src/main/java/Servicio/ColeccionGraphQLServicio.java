package Servicio;

import Modelos.Entidades.Coleccion;
import Modelos.Entidades.CriteriosDePertenencia;
import Modelos.Entidades.DTOs.ColeccionDTO;
import Modelos.Entidades.DTOs.CriterioDTO;
import Modelos.Entidades.DTOs.HechoDTOoutput;
import Repositorio.ColeccionRepositorio;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColeccionGraphQLServicio {

    @Autowired
    private ColeccionRepositorio coleccionRepositorio;

    @Autowired
    private AgregadorGraphQLServicio agregadorGraphQLServicio;

    @Transactional(readOnly = true)
    public List<ColeccionDTO> listarColecciones() {
        List<Coleccion> colecciones = coleccionRepositorio.findAll();

        return colecciones.stream()
                .map(this::convertirAColeccionDTO)
                .collect(Collectors.toList());
    }

    private ColeccionDTO convertirAColeccionDTO(Coleccion c) {

        List<HechoDTOoutput> hechosDTO = new ArrayList<>();
        if (c.getHechos() != null) {
            Hibernate.initialize(c.getHechos()); // Evitar Lazy Init
            hechosDTO = c.getHechos().stream()
                    .map(h -> agregadorGraphQLServicio.convertirAHechoDTO(h))
                    .collect(Collectors.toList());
        }

         List<HechoDTOoutput> hechosConsensuadosDTO = new ArrayList<>();
        if (c.getHechosConsensuados() != null) {
            Hibernate.initialize(c.getHechosConsensuados());
            hechosConsensuadosDTO = c.getHechosConsensuados().stream()
                    .map(h -> agregadorGraphQLServicio.convertirAHechoDTO(h))
                    .collect(Collectors.toList());
        }

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
                loc, prov, pais,
                cp.getFechaAcontecimientoDesde(),
                cp.getFechaAcontecimientoHasta(),
                origenStr
        );
    }
}