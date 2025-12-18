package Servicio;

import Modelos.DTOs.ColeccionDTO;
import Modelos.DTOs.ColeccionDTOOutput;
import Modelos.DTOs.CriterioDTO;
import Modelos.DTOs.HechoDTO;
import Modelos.Entidades.*;
import Modelos.Entidades.Consenso.Consenso;
import Modelos.Entidades.Consenso.ConsensoAbsoluta;
import Modelos.Entidades.Consenso.ConsensoMayoriaSimple;
import Modelos.Entidades.Consenso.ConsensoMultiplesMenciones;
import Modelos.Exceptions.CriterioDuplicadoException;
import Repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime ;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ColeccionServicio {

    @Autowired
    ColeccionRepositorio coleccionRepositorio;
    @Autowired
    HechoRepositorio hechoRepositorio;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CategoriaRepositorio categoriaRepositorio;
    @Autowired
    PaisRepositorio paisRepositorio;
    @Autowired
    ProvinciaRepositorio provinciaRepositorio;
    @Autowired
    LocalidadRepositorio localidadRepositorio;
    @Autowired
    UbicacionRepositorio ubicacionRepositorio;
    @Autowired
    CriterioPertenenciaRepositorio criterioPertenenciaRepositorio;

    @Value("${url.agregador}")
    private String urlBaseAgregador;


    public void crearColeccion(ColeccionDTO coleccionDTO) {
        Categoria categoria =  this.crearCategoria(coleccionDTO.getCriterio().getCategoria());
        String titulo = this.crearTitulo(coleccionDTO.getCriterio().getTitulo());
        Boolean multimedia = coleccionDTO.getCriterio().getContenido_multimedia();
        LocalDateTime  fechaCargaDesde = coleccionDTO.getCriterio().getFechaCargaDesde();
        LocalDateTime  fechaCargaHasta = coleccionDTO.getCriterio().getFechaCargaHasta();
        Pais pais = this.crearPais(coleccionDTO.getCriterio().getPais());
        Provincia provincia = this.crearProvincia(coleccionDTO.getCriterio().getProvincia(), pais);
        Localidad localidad = this.crearLocalidad(coleccionDTO.getCriterio().getLocalidad(), provincia);
        Ubicacion ubicacion = this.crearUbicacion(null, null, localidad, provincia, pais);
        LocalDateTime  fechaAcontecimientoDesde = coleccionDTO.getCriterio().getFechaAcontecimientoDesde();
        LocalDateTime  fechaAcontecimientoHasta = coleccionDTO.getCriterio().getFechaAcontecimientoHasta();
        OrigenCarga origen = this.crearOrigen(coleccionDTO.getCriterio().getOrigen_carga());
        Consenso consenso = this.obtenerEstrategiaPorNombre(coleccionDTO.getConsenso());
        this.validarCriterio(titulo,multimedia, categoria, fechaCargaDesde, fechaCargaHasta, ubicacion, fechaAcontecimientoDesde, fechaAcontecimientoHasta, origen);
        CriteriosDePertenencia criterio_pertenencia = new CriteriosDePertenencia(titulo,multimedia, categoria, fechaCargaDesde, fechaCargaHasta, ubicacion, fechaAcontecimientoDesde, fechaAcontecimientoHasta, origen);
        criterioPertenenciaRepositorio.save(criterio_pertenencia);
        Coleccion coleccion = new Coleccion(coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(),consenso,criterio_pertenencia);
        coleccionRepositorio.save(coleccion);
        this.avisarAgregador(coleccion.getId());


    }

    public void validarCriterio(String titulo, Boolean multimedia, Categoria categoria, LocalDateTime fechaCargaDesde, LocalDateTime fechaCargaHasta, Ubicacion ubicacion, LocalDateTime fechaAcontecimientoDesde, LocalDateTime fechaAcontecimientoHasta, OrigenCarga origen){
        CriteriosDePertenencia criterio = criterioPertenenciaRepositorio.findByTituloAndMultimediaAndCategoriaAndFechaCargaDesdeAndFechaCargaHastaAndUbicacionAndFechaAcontecimientoDesdeAndFechaAcontecimientoHastaAndOrigen(titulo, multimedia, categoria, fechaCargaDesde, fechaCargaHasta, ubicacion,fechaAcontecimientoDesde, fechaAcontecimientoHasta, origen);
        if(criterio != null){
            throw new CriterioDuplicadoException("Ya existe una colección con este criterio de pertenencia");
        }
    }

    public String crearTitulo(String titulo) {
        if (titulo == null || titulo.isEmpty()) {
            return null;
        }
        return titulo;
    }

    public Categoria crearCategoria(String nombre) {
        if (nombre == null || nombre.isEmpty()){
            return null;
        }
        Categoria categoria = categoriaRepositorio.findByNombre(nombre);
        if(categoria == null){
            categoria = new Categoria(nombre);
            categoriaRepositorio.save(categoria);
        }
        return categoria;
    }

    public Pais crearPais(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return null;
        }
        Pais pais = paisRepositorio.findByPais(nombre);
        if(pais == null){
            pais = new Pais(nombre);
            paisRepositorio.save(pais);
        }
        return pais;
    }

    public Provincia crearProvincia(String nombre, Pais pais) {
        if (nombre == null || nombre.isEmpty() ) {
            return null;
        }
        Provincia provincia = provinciaRepositorio.findByProvinciaAndPais(nombre, pais);
        if(provincia == null){
            provincia = new Provincia(nombre, pais);
            provinciaRepositorio.save(provincia);
        }
        return provincia;
    }

    public Localidad crearLocalidad(String nombre, Provincia provincia) {
        if (nombre == null || nombre.isEmpty()) {
            return null;
        }
        Localidad localidad = localidadRepositorio.findByLocalidadAndProvincia(nombre, provincia);
        if(localidad == null){
            localidad = new Localidad(nombre, provincia);
            localidadRepositorio.save(localidad);
        }
        return localidad;
    }

    public Ubicacion crearUbicacion(Double latitud, Double longitud, Localidad localidad, Provincia provincia, Pais pais) {
        if (pais == null && provincia == null && localidad == null) {
            return null;
        }
        Ubicacion ubicacion = ubicacionRepositorio.findByLocalidadAndProvinciaAndPais(localidad, provincia, pais);
        if(ubicacion == null){
            ubicacion = new Ubicacion(localidad, provincia, pais, latitud, longitud);
            ubicacionRepositorio.save(ubicacion);
        }
        return ubicacion;
    }

    public OrigenCarga crearOrigen(String origen){
        if (origen == null || origen.isEmpty()) {
            return null;
        }
        return OrigenCarga.valueOf(origen);
    }

    private void avisarAgregador (Long coleccionId) {
        String urlAgregadorFinal = urlBaseAgregador + "/agregador/colecciones/";
        UriComponentsBuilder urlAgregador = UriComponentsBuilder.fromHttpUrl(urlAgregadorFinal + coleccionId);
        ResponseEntity<String> respuestaAgregador =  restTemplate.exchange(
                urlAgregador.toUriString(),
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {
                }
                );
        if (respuestaAgregador.getStatusCode().isError()) {
            throw new HttpServerErrorException(respuestaAgregador.getStatusCode());
        }
    }

    public void eliminarColeccion(Long id) {
        coleccionRepositorio.deleteById(id);
        criterioPertenenciaRepositorio.deleteById(id);
    }

    public void eliminarHecho(Long id) {
        List<Coleccion> colecciones = coleccionRepositorio.findAll();
        for (Coleccion coleccion : colecciones) {
            this.eliminarHechoDeColeccion(coleccion.getId(), id);
        }
        Hecho hecho = hechoRepositorio.findById(id).orElseThrow();
        hecho.eliminarse();
        hechoRepositorio.save(hecho);
    }

    public void eliminarHechoDeColeccion(Long id_coleccion, Long id_hecho) {
        Coleccion coleccion = coleccionRepositorio.findById(id_coleccion).orElseThrow();
        coleccion.getHechos().removeIf(hecho -> hecho.getId().equals(id_hecho));
        coleccionRepositorio.save(coleccion);
    }

    public void modificarConsenso(Long id, String estrategia) {
        Coleccion coleccion = coleccionRepositorio.findById(id).orElse(null);
        if (coleccion == null) {
            throw new IllegalArgumentException("Coleccion no encontrada:");
        }
        Consenso consenso = this.obtenerEstrategiaPorNombre(estrategia);

        coleccion.setConsenso(consenso);
        coleccionRepositorio.save(coleccion);
    }
    private Consenso transformarAConsenso(String nombre) {
            if (nombre == null || nombre.isEmpty()) {
            return null;
        }
        switch (nombre) {
            case "ABSOLUTA": return new ConsensoAbsoluta();
            case "MULTIPLES_MENCIONES": return new ConsensoMultiplesMenciones();
            case "MAYORIA_SIMPLE": return new ConsensoMayoriaSimple();
            default:
                throw new IllegalArgumentException("Tipo de CONSENSO desconocido: " + nombre);
        }
    }
    private Consenso obtenerEstrategiaPorNombre(String nombre) {
        Consenso estrategia = transformarAConsenso(nombre);

        return estrategia;
    }


    public void agregarFuente(Long id_coleccion, Long id_fuente, String origen) {
        Coleccion  coleccion = coleccionRepositorio.findById(id_coleccion).orElse(null);

        if (coleccion == null) {
            throw new IllegalArgumentException("Coleccion no encontrada: " + id_coleccion);
        }
        List<Hecho> hechoFuente = hechoRepositorio.findByIdFuenteAndOrigen(id_fuente, OrigenCarga.valueOf(origen.toUpperCase()));
        if (hechoFuente.isEmpty()) {
            throw new IllegalArgumentException("No hay hechos de esa fuente");
        }
        for (Hecho hecho: hechoFuente) {
            coleccion.agregarHecho(hecho);
        }
        coleccionRepositorio.save(coleccion);
    }
    public void eliminarFuente(Long id, Long fuente, String origen) {
        Coleccion coleccion = coleccionRepositorio.findById(id).orElse(null);
        if (coleccion == null) {
            throw new IllegalArgumentException("Coleccion no encontrada:");
        }
        coleccion.getHechos().removeIf(hecho -> hecho.getIdFuente().equals(fuente) && hecho.getOrigen().equals(OrigenCarga.valueOf(origen)));
        coleccionRepositorio.save(coleccion);
    }

    public ColeccionDTOOutput obtenerColeccion(Long id) {
        Coleccion coleccion = coleccionRepositorio.findById(id).orElseThrow();
        return this.transformarColeccionADTO(coleccion);
    }

    public CriterioDTO transformarCriterioADTO(CriteriosDePertenencia criteriosDePertenencia) {
        return new CriterioDTO(
                criteriosDePertenencia.getTitulo(),
                criteriosDePertenencia.getMultimedia(),
                Optional.ofNullable(criteriosDePertenencia.getCategoria())
                        .map(c->c.getNombre())
                        .orElse(null),
                criteriosDePertenencia.getFechaCargaDesde(),
                criteriosDePertenencia.getFechaCargaHasta(),
                Optional.ofNullable(criteriosDePertenencia.getUbicacion())
                    .map(u -> u.getLocalidad())
                    .map(l -> l.getLocalidad())
                    .orElse(null),
                Optional.ofNullable(criteriosDePertenencia.getUbicacion())
                        .map(u -> u.getProvincia())
                        .map(p -> p.getProvincia())
                        .orElse(null),
                Optional.ofNullable(criteriosDePertenencia.getUbicacion())
                        .map(u -> u.getPais())
                        .map(p -> p.getPais())
                        .orElse(null),
                criteriosDePertenencia.getFechaAcontecimientoDesde(),
                criteriosDePertenencia.getFechaAcontecimientoHasta(),
                Optional.ofNullable(criteriosDePertenencia.getOrigen())
                        .map(o->o.name())
                        .orElse(null)
        );
    }

    public HechoDTO transformarHechoDTO(Hecho hecho) {

        HechoDTO dto = new HechoDTO(
                hecho.getId(),
                hecho.getIdFuente(),
                hecho.getTitulo(),
                hecho.getDescripcion(),
                hecho.getContenido().getTexto(),
                hecho.getContenido().getContenidoMultimedia(),
                hecho.getCategoria().getNombre(),
                hecho.getFecha(),
                hecho.getUbicacion().getPais().getPais(),
                hecho.getUbicacion().getProvincia().getProvincia(),
                hecho.getUbicacion().getLocalidad().getLocalidad(),
                null,
                null,
                null,
                null,
                hecho.getOrigen().name(),
                hecho.getMostrarNombre(),
                hecho.getMostrarApellido(),
                hecho.getMostrarFechaNacimiento());

        if(Boolean.FALSE.equals(hecho.getAnonimo())){
            dto.setUsuario(hecho.getContribuyente().getUsuario());
            dto.setApellido(hecho.getContribuyente().getApellido());
            dto.setNombre(hecho.getContribuyente().getNombre());
            dto.setFecha_nacimiento(hecho.getContribuyente().getFecha_nacimiento());
        }
        return dto;
    }

    public ColeccionDTOOutput transformarColeccionADTO(Coleccion coleccion) {
        CriterioDTO criterio = this.transformarCriterioADTO(coleccion.getCriterio_pertenencia());
        List<HechoDTO> hechosDTO = new ArrayList<>();
        for (Hecho hecho: coleccion.getHechos() ) {
            HechoDTO hechoDTO = this.transformarHechoDTO(hecho);
            hechosDTO.add(hechoDTO);
        }

        String consenso = this.convertirConsenso(coleccion.getConsenso());

        List<HechoDTO> hechosConsensuadosDTO = new ArrayList<>();
        for (Hecho hecho: coleccion.getHechosConsensuados() ) {
            HechoDTO hechoDTO = this.transformarHechoDTO(hecho);
            hechosConsensuadosDTO.add(hechoDTO);
        }
        return new ColeccionDTOOutput(coleccion.getId(), coleccion.getTitulo(), coleccion.getDescripcion(), criterio, hechosDTO, consenso,hechosConsensuadosDTO );
    }

    public String convertirConsenso(Consenso atributo) {
        if (atributo == null) {
            return null;
        }
        if (atributo instanceof ConsensoAbsoluta) {
            return "ABSOLUTA";
        }
        if (atributo instanceof ConsensoMultiplesMenciones) {
            return "MULTIPLES_MENCIONES";
        }
        if (atributo instanceof ConsensoMayoriaSimple) {
            return "MAYORIA_SIMPLE";
        }
        throw new IllegalArgumentException(
                "Tipo de Consenso no soportado: " + atributo.getClass().getName()
        );
    }


}

