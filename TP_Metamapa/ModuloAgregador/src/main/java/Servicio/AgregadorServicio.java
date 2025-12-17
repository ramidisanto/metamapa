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
            hechosRaw.addAll(fetchSafe(urlProxy + "/demo/hechos", OrigenCarga.FUENTE_PROXY));
            hechosRaw.addAll(fetchSafe(urlBaseDinamica + "/dinamica/hechos", OrigenCarga.FUENTE_DINAMICA));
            hechosRaw.addAll(fetchSafe(urlBaseEstatica + "/fuenteEstatica/hechos", OrigenCarga.FUENTE_ESTATICA));
            hechosRaw.addAll(fetchSafe(urlProxy + "/metamapa/hechos", OrigenCarga.FUENTE_PROXY));


            // Disparar proceso asíncrono "Fire and Forget"
            if (!hechosRaw.isEmpty()) {
                procesarYGuardarAsync(hechosRaw);
            }
        } catch (Exception e) {
            System.err.println("Error inicializando carga: " + e.getMessage());
        }
    }

    @Async("executorMetamapa")
    public void procesarYGuardarAsync(List<HechoDTOInput> listaCruda) {

            for (HechoDTOInput dto : listaCruda) {
                procesarUnHecho(dto);
            }

            actualizarColecciones();

    }

    private void procesarUnHecho(HechoDTOInput dto) {
        try {

            normalizarUbicacion(dto);

            // 2. Normalizar Categoría (Nueva llamada)
            String categoriaNorm = restTemplate.postForObject(
                    urlNormalizador + "/normalizacion/categorias",
                    dto.getCategoria(),
                    String.class
            );
            if (categoriaNorm != null) dto.setCategoria(categoriaNorm);

            // 3. Normalizar Título (Nueva llamada)
            String tituloNorm = restTemplate.postForObject(
                    urlNormalizador + "/normalizacion/titulos",
                    dto.getTitulo(),
                    String.class
            );
            if (tituloNorm != null) dto.setTitulo(tituloNorm);

            // 4. Guardar
            convertirYGuardar(dto);

        } catch (Exception e) {
            System.err.println("Error procesando hecho id=" + dto.getIdFuente() + ": " + e.getMessage());
        }
    }


    private void normalizarUbicacion(HechoDTOInput dto) {
        try {

            UbicacionDTOOutput inputUbi = new UbicacionDTOOutput(dto.getLatitud(), dto.getLongitud());

            UbicacionDTOInput ubiRes = restTemplate.postForObject(
                    urlNormalizador + "/normalizacion/ubicaciones",
                    inputUbi,
                    UbicacionDTOInput.class
            );

            if (ubiRes != null) {
                dto.setPais(ubiRes.getPais());
                dto.setProvincia(ubiRes.getProvincia());
                dto.setLocalidad(ubiRes.getLocalidad());
            }
        } catch (Exception e) {
            System.err.println("Fallo normalización ubicación: " + e.getMessage());
        }
    }



    private void convertirYGuardar(HechoDTOInput dto) {

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

             hechoRepositorio.save(hecho);

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


    private synchronized Pais crearPais(String nombre) {
//        if (nombre == null) return null;
//        Pais pais = paisRepositorio.findByPais(nombre);
//        if (pais == null) {
//            pais = new Pais(nombre);
//            paisRepositorio.save(pais);
//        }
//        return pais;
        return paisRepositorio.buscarOCrear(nombre);
    }

    private synchronized Provincia crearProvincia(String nombre, Pais pais) {
//        if (nombre == null) return null;
//        Provincia provincia = provinciaRepositorio.findByProvinciaAndPais(nombre, pais);
//        if (provincia == null) {
//            provincia = new Provincia(nombre, pais);
//            provinciaRepositorio.save(provincia);
//        }
//        return provincia;
        return provinciaRepositorio.buscarOCrear(nombre, pais);
    }

    private synchronized Localidad crearLocalidad(String nombre, Provincia provincia) {
//        if (nombre == null) return null;
//        Localidad localidad = localidadRepositorio.findByLocalidadAndProvincia(nombre, provincia);
//        if (localidad == null) {
//            localidad = new Localidad(nombre, provincia);
//            localidadRepositorio.save(localidad);
//        }
//        return localidad;
        return localidadRepositorio.buscarOCrear(nombre, provincia);
    }

    private synchronized Ubicacion crearUbicacion(Double latitud, Double longitud, Localidad localidad, Provincia provincia, Pais pais) {
        // Valida nulos si es necesario
//        Ubicacion ubicacion = ubicacionRepositorio.findByLatitudAndLongitud(latitud, longitud);
//        if (ubicacion == null) {
//            ubicacion = new Ubicacion(localidad, provincia, pais, latitud, longitud);
//            ubicacionRepositorio.save(ubicacion);
//        }
//        return ubicacion;
        return ubicacionRepositorio.buscarOCrear(latitud, longitud, localidad, provincia, pais);
    }

    private synchronized Categoria crearCategoria(String nombre) {
//        if (nombre == null) return null;
//        Categoria categoria = categoriaRepositorio.findByNombre(nombre);
//        if (categoria == null) {
//            categoria = new Categoria(nombre);
//            categoriaRepositorio.save(categoria);
//        }
//        return categoria;
        return categoriaRepositorio.buscarOCrear(nombre);

    }

    private synchronized Contribuyente crearContribuyente(String usuario, String nombre, String apellido, LocalDate fechaNacimiento) {
//        if (usuario == null) return null;
//        Contribuyente contribuyente = contribuyenteRepositorio.findByUsuario(usuario);
//        if (contribuyente == null) {
//            contribuyente = new Contribuyente(usuario, nombre, apellido, fechaNacimiento);
//            contribuyenteRepositorio.save(contribuyente);
//        }
//        return contribuyente;
        return contribuyenteRepositorio.buscarOCrear(usuario, nombre, apellido, fechaNacimiento);
    }

    private synchronized Contenido crearContenido(String texto, String contenidoMultimedia) {
//        List<Contenido> contenido = contenidoRepositorio.findByTextoAndContenidoMultimedia(texto, contenidoMultimedia);
//        if (contenido == null || contenido.isEmpty()) {
//            Contenido contenido2 = new Contenido(texto, contenidoMultimedia);
//            contenidoRepositorio.save(contenido2);
//            return contenido2;
//        }
//        return contenido.get(0);
        return contenidoRepositorio.buscarOCrear(texto, contenidoMultimedia);

    }

    public void actualizarColecciones() {
        List<Coleccion> colecciones = coleccionRepositorio.findAllWithRelations();
        for (Coleccion c : colecciones) {
            actualizarColeccion(c); // Tu método existente
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
