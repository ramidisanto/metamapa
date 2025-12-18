package Servicio;

import Modelos.Entidades.DTOs.HechoDTOInput;
import Modelos.Entidades.DTOs.UbicacionDTOOutput;
import Modelos.Entidades.DTOs.UbicacionDTOInput;
import Modelos.Entidades.*;
import Modelos.Exceptions.ColeccionNoEncontradaException;
import Repositorio.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public class AgregadorServicio {

    @Autowired RestTemplate restTemplate;
    @Autowired HechoRepositorio hechoRepositorio;
    @Autowired ColeccionRepositorio coleccionRepositorio;
    @Autowired CategoriaRepositorio categoriaRepositorio;
    @Autowired ProvinciaRepositorio provinciaRepositorio;
    @Autowired PaisRepositorio paisRepositorio;
    @Autowired LocalidadRepositorio localidadRepositorio;
    @Autowired UbicacionRepositorio ubicacionRepositorio;
    @Autowired ContribuyenteRepositorio contribuyenteRepositorio;
    @Autowired ContenidoRepositorio contenidoRepositorio;

    @Value("${url.proxy}") private String urlProxy;
    @Value("${url.normalizador}") private String urlNormalizador;
    @Value("${url.dinamica}") private String urlBaseDinamica;
    @Value("${url.estatica}") private String urlBaseEstatica;


    private final Semaphore ubicacionLimiter = new Semaphore(2);


    public void actualizarHechos() {
        List<HechoDTOInput> hechosRaw = new ArrayList<>();
        try {
            System.out.println("--- Buscando hechos en fuentes externas... ---");
            hechosRaw.addAll(fetchSafe(urlProxy + "/demo/hechos", OrigenCarga.FUENTE_PROXY));
            hechosRaw.addAll(fetchSafe(urlBaseDinamica + "/dinamica/hechos", OrigenCarga.FUENTE_DINAMICA));
            hechosRaw.addAll(fetchSafe(urlBaseEstatica + "/fuenteEstatica/hechos", OrigenCarga.FUENTE_ESTATICA));
            hechosRaw.addAll(fetchSafe(urlProxy + "/metamapa/hechos", OrigenCarga.FUENTE_PROXY));

            System.out.println("--- Recolectados " + hechosRaw.size() + " hechos. Iniciando Async... ---");

            if (!hechosRaw.isEmpty()) {
                procesarYGuardarAsync(hechosRaw);
            }
        } catch (Exception e) {
            System.err.println("Error inicializando carga: " + e.getMessage());
        }
    }

    @Async("executorMetamapa")
    public void procesarYGuardarAsync(List<HechoDTOInput> listaCruda) {
        try {
            for (HechoDTOInput dto : listaCruda) {
                procesarUnHecho(dto);
            }
        } finally {
            actualizarColecciones();
        }
    }

    private void procesarUnHecho(HechoDTOInput dto) {
        try {

            normalizarDTO(dto);
            convertirYGuardar(dto);

        } catch (Exception e) {
            System.err.println(
                    "Error procesando hecho idFuente=" + dto.getIdFuente() +
                            " titulo=" + dto.getTitulo() +
                            " -> " + e.getMessage()
            );
        }
    }


    private void normalizarDTO(HechoDTOInput dto) {
        try {
            ubicacionLimiter.acquire();
            try {
                UbicacionDTOOutput inputUbi =
                        new UbicacionDTOOutput(dto.getLatitud(), dto.getLongitud());

                UbicacionDTOInput ubiRes =
                        restTemplate.postForObject(
                                urlNormalizador + "/normalizacion/ubicaciones",
                                inputUbi,
                                UbicacionDTOInput.class
                        );
                if (ubiRes != null) {
                    dto.setPais(ubiRes.getPais());
                    dto.setProvincia(ubiRes.getProvincia());
                    dto.setLocalidad(ubiRes.getLocalidad());
                }

                String categoriaNorm = restTemplate.postForObject(
                        urlNormalizador + "/normalizacion/categorias",
                        dto.getCategoria(),
                        String.class
                );
                if (categoriaNorm != null) dto.setCategoria(categoriaNorm);

                String tituloNorm = restTemplate.postForObject(
                        urlNormalizador + "/normalizacion/titulos",
                        dto.getTitulo(),
                        String.class
                );
                if (tituloNorm != null) dto.setTitulo(tituloNorm);


            } finally {
                ubicacionLimiter.release();
            }

        } catch (Exception e) {
            System.err.println("Fallo normalización externa");
        }
    }


    private Hecho convertirYGuardar(HechoDTOInput dto) {
        synchronized(this) {
            Pais pais = crearPais(dto.getPais());
            Provincia provincia = crearProvincia(dto.getProvincia(), pais);
            Localidad localidad = crearLocalidad(dto.getLocalidad(), provincia);
            Ubicacion ubicacion = crearUbicacion(dto.getLatitud(), dto.getLongitud(), localidad, provincia, pais);

            Categoria categoria = crearCategoria(dto.getCategoria());
            Contribuyente contribuyente = crearContribuyente(dto.getUsuario(), dto.getNombre(), dto.getApellido(), dto.getFecha_nacimiento());
            Contenido contenido = crearContenido(dto.getContenido(), dto.getContenido_multimedia());

            Hecho hecho = new Hecho(
                    dto.getIdFuente(),
                    dto.getTitulo(),
                    dto.getDescripcion(),
                    contenido,
                    categoria,
                    dto.getFechaAcontecimiento(),
                    ubicacion,
                    LocalDateTime.now(),
                    OrigenCarga.valueOf(dto.getOrigen_carga().toUpperCase()),
                    true,
                    contribuyente,
                    dto.getAnonimo(),
                    dto.getMostrarNombre(),
                    dto.getMostrarApellido(),
                    dto.getMostrarFechaNacimiento()
            );

            hecho.setEstadoNormalizacion(EstadoNormalizacion.NORMALIZADO);

            return hechoRepositorio.save(hecho);
        }
    }


    private List<HechoDTOInput> fetchSafe(String url, OrigenCarga origen) {
        try {
            ResponseEntity<List<HechoDTOInput>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            List<HechoDTOInput> lista = response.getBody() != null ? response.getBody() : new ArrayList<>();
            lista.forEach(h -> h.setOrigen_carga(origen.name()));
            return lista;
        } catch (Exception e) {
            System.err.println("Fuente caída: " + url);
            return new ArrayList<>();
        }
    }


    public Pais crearPais(String nombre) {

        return paisRepositorio.buscarOCrear(nombre);
    }

    public  Provincia crearProvincia(String nombre, Pais pais) {
        return provinciaRepositorio.buscarOCrear(nombre, pais);
    }

    public  Localidad crearLocalidad(String nombre, Provincia provincia) {
        return localidadRepositorio.buscarOCrear(nombre, provincia);
    }

    public  Ubicacion crearUbicacion(Double latitud, Double longitud, Localidad localidad, Provincia provincia, Pais pais) {

        return ubicacionRepositorio.buscarOCrear(latitud, longitud, localidad, provincia, pais);
    }

    public  Categoria crearCategoria(String nombre) {

        return categoriaRepositorio.buscarOCrear(nombre);

    }

    public  Contribuyente crearContribuyente(String usuario, String nombre, String apellido, LocalDate fechaNacimiento) {

        return contribuyenteRepositorio.buscarOCrear(usuario, nombre, apellido, fechaNacimiento);
    }

    public  Contenido crearContenido(String texto, String contenidoMultimedia) {

        return contenidoRepositorio.buscarOCrear(texto, contenidoMultimedia);

    }

    public void actualizarColecciones() {
        List<Coleccion> colecciones = coleccionRepositorio.findAllWithRelations();
        for (Coleccion c : colecciones) {
            actualizarColeccion(c);
        }
    }

    public void actualizarColeccion(Coleccion coleccion) {
        System.out.printf("Procesando colección: {}", coleccion.getTitulo());

        CriteriosDePertenencia criterio = coleccion.getCriterio_pertenencia();
        if (criterio == null) {
            return;
        }

        List<Hecho> hechosCumplenCriterio = hechoRepositorio.buscarHechosPorFiltros(
                Optional.ofNullable(criterio.getCategoria())
                        .map(c -> c.getNombre())
                        .orElse(null),
                criterio.getMultimedia(),
                criterio.getFechaCargaDesde(),
                criterio.getFechaCargaHasta(),
                criterio.getFechaAcontecimientoDesde(),
                criterio.getFechaAcontecimientoHasta(),
                criterio.getOrigen(),
                criterio.getTitulo(),
                Optional.ofNullable(criterio.getUbicacion())
                        .map(u -> u.getPais())
                        .map(p -> p.getPais())
                        .orElse(null),
                Optional.ofNullable(criterio.getUbicacion())
                        .map(u -> u.getProvincia())
                        .map(p -> p.getProvincia())
                        .orElse(null),
                Optional.ofNullable(criterio.getUbicacion())
                        .map(u -> u.getLocalidad())
                        .map(l -> l.getLocalidad())
                        .orElse(null));

        if (coleccion.getHechos() != null) {
            Set<Long> idsExistentes = coleccion.getHechos()
                    .stream()
                    .map(Hecho::getId)
                    .collect(Collectors.toSet());

            List<Hecho> nuevosHechos = hechosCumplenCriterio.stream()
                    .filter(h -> !idsExistentes.contains(h.getId()) && h.getVisible())
                    .toList();

            coleccion.agregarHechos(nuevosHechos);
            coleccionRepositorio.save(coleccion);
        } else {
            coleccion.agregarHechos(hechosCumplenCriterio);
            coleccionRepositorio.save(coleccion);
        }
    }

    public void cargarColeccionConHechos(Long coleccionId) throws ColeccionNoEncontradaException {

        Coleccion coleccion = coleccionRepositorio.findById(coleccionId)
                .orElseThrow(ColeccionNoEncontradaException::new);
        actualizarColeccion(coleccion);
    }
}
