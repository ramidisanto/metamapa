package Servicio;

import Modelos.Entidades.DTOs.HechoDTOInput;
import Modelos.Entidades.*;
import Modelos.Entidades.DTOs.UbicacionDTOInput;
import Modelos.Entidades.DTOs.UbicacionDTOOutput;
import Modelos.Entidades.DTOs.*;
import Modelos.Exceptions.ColeccionNoEncontradaException;
import Repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgregadorServicio {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    HechoRepositorio hechoRepositorio;
    @Autowired
    ColeccionRepositorio coleccionRepositorio;
    @Autowired
    CategoriaRepositorio categoriaRepositorio;
    @Autowired
    ProvinciaRepositorio provinciaRepositorio;
    @Autowired
    PaisRepositorio paisRepositorio;
    @Autowired
    LocalidadRepositorio localidadRepositorio;
    @Autowired
    UbicacionRepositorio ubicacionRepositorio;
    @Autowired
    ContribuyenteRepositorio contribuyenteRepositorio;
    @Autowired
    ContenidoRepositorio contenidoRepositorio;
    @Autowired
    NormalizadorClient normalizadorClient;
    @Value("${url.proxy}")
    private String urlProxy;
    @Value("${url.normalizador}")
    private String urlNormalizador;
    @Value("${url.dinamica}")
    private String urlBaseDinamica;
    @Value("${url.estatica}")
    private String urlBaseEstatica;

    private static final int BATCH_SIZE = 300;

    @Transactional
    public void actualizarHechos() {

        String urlDemo = urlProxy + "/demo/hechos";
        String urlMetamapa = urlProxy + "/metamapa/hechos";
        String urlDinamica = urlBaseDinamica + "/dinamica/hechos";
        String urlEstatica = urlBaseEstatica + "/fuenteEstatica/hechos";

        ResponseEntity<List<HechoDTOInput>> respuestaDinamica = restTemplate.exchange(
                urlDinamica,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        ResponseEntity<List<HechoDTOInput>> respuestaDemo = restTemplate.exchange(
                urlDemo,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        ResponseEntity<List<HechoDTOInput>> respuestaMetamapa = restTemplate.exchange(
                urlMetamapa,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        ResponseEntity<List<HechoDTOInput>> respuestaEstatica = restTemplate.exchange(
                urlEstatica,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        List<HechoDTOInput> hechosDTOTotales = new ArrayList<>();

        if (!respuestaDemo.getBody().isEmpty()) {
            List<HechoDTOInput> hechosDemo = this.setearOrigenCarga(respuestaDemo.getBody(), OrigenCarga.FUENTE_PROXY);
            hechosDTOTotales.addAll(hechosDemo);
        }

        if (!respuestaDinamica.getBody().isEmpty()) {
            List<HechoDTOInput> hechosDinamica = this.setearOrigenCarga(respuestaDinamica.getBody(),
                    OrigenCarga.FUENTE_DINAMICA);
            hechosDTOTotales.addAll(hechosDinamica);
        }

        if (!respuestaEstatica.getBody().isEmpty()) {
            List<HechoDTOInput> hechosEstatica = this.setearOrigenCarga(respuestaEstatica.getBody(),
                    OrigenCarga.FUENTE_ESTATICA);
            hechosDTOTotales.addAll(hechosEstatica);
        }

        if (!respuestaMetamapa.getBody().isEmpty()) {
            List<HechoDTOInput> hechosMetamapa = this.setearOrigenCarga(respuestaMetamapa.getBody(),
                    OrigenCarga.FUENTE_PROXY);
            hechosDTOTotales.addAll(hechosMetamapa);
        }

        UriComponentsBuilder urlCategoria = UriComponentsBuilder
                .fromHttpUrl(urlNormalizador + "/normalizacion/categorias");
        UriComponentsBuilder urlUbicacion = UriComponentsBuilder
                .fromHttpUrl(urlNormalizador + "/normalizacion/ubicaciones");
        UriComponentsBuilder urlTitulo = UriComponentsBuilder.fromHttpUrl(urlNormalizador + "/normalizacion/titulos");
    /*
        for (HechoDTOInput hechoDTO : hechosDTOTotales) {

            // CATEEGORIAAAAAA

            String categoriaRequest = hechoDTO.getCategoria();

            ResponseEntity<String> categoriaResponse = restTemplate.exchange(
                    urlCategoria.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(categoriaRequest),
                    String.class);
            String categoriaNormalizada = categoriaResponse.getBody();

            if (categoriaNormalizada != null) {
                hechoDTO.setCategoria(categoriaNormalizada); // solo guardamos el nombre
            }

            // UBICACIOOOOONN
            UbicacionDTOOutput ubicacionDTOOutput = new UbicacionDTOOutput(hechoDTO.getLatitud(),
                    hechoDTO.getLongitud());

            ResponseEntity<UbicacionDTOInput> UbicacionNormalizada = restTemplate.exchange(
                    urlUbicacion.toUriString(), // URL de tu API
                    HttpMethod.POST,
                    new HttpEntity<>(ubicacionDTOOutput),
                    UbicacionDTOInput.class);

            hechoDTO.setPais(UbicacionNormalizada.getBody().getPais());
            hechoDTO.setProvincia(UbicacionNormalizada.getBody().getProvincia());
            hechoDTO.setLocalidad(UbicacionNormalizada.getBody().getLocalidad());

            // TITULOOO
            String tituloRequest = hechoDTO.getTitulo();
            ResponseEntity<String> tituloResponse = restTemplate.exchange(
                    urlTitulo.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(tituloRequest),
                    String.class);
            String tituloNormalizado = tituloResponse.getBody();
            if (tituloNormalizado != null) {
                hechoDTO.setTitulo(tituloNormalizado);
            }
        }

     */
        List<HechoNormalizarDTO> normalizarDTOs =
                hechosDTOTotales.stream()
                        .map(this::transformarANormalizarDTO)
                        .toList();

        for (int i = 0; i < normalizarDTOs.size(); i += BATCH_SIZE) {

            List<HechoNormalizarDTO> batch =
                    normalizarDTOs.subList(
                            i,
                            Math.min(i + BATCH_SIZE, normalizarDTOs.size())
                    );

            List<HechoNormalizarDTO> normalizados =
                    normalizadorClient.normalizarBatch(batch);

            aplicarResultadoNormalizacion(normalizados, hechosDTOTotales);
        }

        if (!hechosDTOTotales.isEmpty()) {
            List<Hecho> hechos = this.transaformarAHecho(hechosDTOTotales);
            hechoRepositorio.saveAll(hechos);
            this.actualizarColecciones();
        }
    }

    private void aplicarResultadoNormalizacion(
            List<HechoNormalizarDTO> normalizados,
            List<HechoDTOInput> originales) {

        Map<String, HechoDTOInput> index =
                originales.stream()
                        .collect(Collectors.toMap(
                                h -> h.getLatitud() + "," + h.getLongitud() + "," + h.getTitulo(),
                                h -> h
                        ));

        for (HechoNormalizarDTO n : normalizados) {
            String key = n.getLatitud() + "," + n.getLongitud() + "," + n.getTitulo();
            HechoDTOInput original = index.get(key);

            if (original != null) {
                original.setTitulo(n.getTitulo());
                original.setCategoria(n.getCategoria());
            }
        }
    }


    private HechoNormalizarDTO transformarANormalizarDTO(HechoDTOInput h) {
        HechoNormalizarDTO dto = new HechoNormalizarDTO();
        dto.setTitulo(h.getTitulo());
        dto.setCategoria(h.getCategoria());
        dto.setLatitud(h.getLatitud());
        dto.setLongitud(h.getLongitud());
        return dto;
    }


    public List<Hecho> transaformarAHecho(List<HechoDTOInput> hechosDTO) {
        List<Hecho> hechos = new ArrayList<>();
        for (HechoDTOInput hechoDTO : hechosDTO) {
            Pais pais = this.crearPais(hechoDTO.getPais());
            Provincia provincia = this.crearProvincia(hechoDTO.getProvincia(), pais);
            Localidad localidad = this.crearLocalidad(hechoDTO.getLocalidad(), provincia);
            Categoria categoria = this.crearCategoria(hechoDTO.getCategoria());
            Ubicacion ubicacion = this.crearUbicacion(hechoDTO.getLatitud(), hechoDTO.getLongitud(), localidad,
                    provincia, pais);
            Contribuyente contribuyente = this.crearContribuyente(hechoDTO.getUsuario(), hechoDTO.getNombre(),
                    hechoDTO.getApellido(), hechoDTO.getFecha_nacimiento());
            Contenido contenido = this.crearContenido(hechoDTO.getContenido(), hechoDTO.getContenido_multimedia());
            Hecho hecho = new Hecho(
                    hechoDTO.getIdFuente(),
                    hechoDTO.getTitulo(),
                    hechoDTO.getDescripcion(),
                    contenido,
                    categoria,
                    hechoDTO.getFechaAcontecimiento(),
                    ubicacion,
                    LocalDateTime.now(),
                    OrigenCarga.valueOf(hechoDTO.getOrigen_carga().toUpperCase()),
                    true,
                    contribuyente,
                    hechoDTO.getAnonimo()
            );

            hechos.add(hecho);

        }
        return hechos;
    }

    public Contenido crearContenido(String texto, String contenidoMultimedia) {

        List<Contenido> contenido = contenidoRepositorio.findByTextoAndContenidoMultimedia(texto, contenidoMultimedia);
        if (contenido == null || contenido.isEmpty()) {
            Contenido contenido2 = new Contenido(texto, contenidoMultimedia);
            contenidoRepositorio.save(contenido2);
            return contenido2;
        }
        return contenido.get(0);
    }

    public Contribuyente crearContribuyente(String usuario, String nombre, String apellido, LocalDate fechaNacimiento) {
        if (usuario == null) {
            return null;
        }
        Contribuyente contribuyente = contribuyenteRepositorio.findByUsuario(usuario);
        if (contribuyente == null) {
            contribuyente = new Contribuyente(usuario, nombre, apellido, fechaNacimiento);
            contribuyenteRepositorio.save(contribuyente);
        }
        return contribuyente;
    }

    public Categoria crearCategoria(String nombre) {
        Categoria categoria = categoriaRepositorio.findByNombre(nombre);
        if (categoria == null) {
            categoria = new Categoria(nombre);
            categoriaRepositorio.save(categoria);
        }
        return categoria;
    }

    public Pais crearPais(String nombre) {
        Pais pais = paisRepositorio.findByPais(nombre);
        if (pais == null) {
            pais = new Pais(nombre);
            paisRepositorio.save(pais);
        }
        return pais;
    }

    public Provincia crearProvincia(String nombre, Pais pais) {
        Provincia provincia = provinciaRepositorio.findByProvinciaAndPais(nombre, pais);
        if (provincia == null) {
            provincia = new Provincia(nombre, pais);
            provinciaRepositorio.save(provincia);
        }
        return provincia;
    }

    public Localidad crearLocalidad(String nombre, Provincia provincia) {
        Localidad localidad = localidadRepositorio.findByLocalidadAndProvincia(nombre, provincia);
        if (localidad == null) {
            localidad = new Localidad(nombre, provincia);
            localidadRepositorio.save(localidad);
        }
        return localidad;
    }

    public Ubicacion crearUbicacion(Double latitud, Double longitud, Localidad localidad, Provincia provincia,
            Pais pais) {
        Ubicacion ubicacion = ubicacionRepositorio.findByLatitudAndLongitud(latitud, longitud);
        if (ubicacion == null) {
            ubicacion = new Ubicacion(localidad, provincia, pais, latitud, longitud);
            ubicacionRepositorio.save(ubicacion);
        }
        return ubicacion;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public void actualizarColecciones() {
        try {
            System.out.print("Iniciando actualización de colecciones");
            List<Coleccion> colecciones = coleccionRepositorio.findAllWithRelations();
            System.out.printf("Se encontraron {} colecciones para actualizar", colecciones.size());

            if (colecciones.isEmpty()) {
                System.out.print("No hay colecciones para actualizar, finalizando proceso");
                return;
            }

            for (Coleccion coleccion : colecciones) {
                System.out.printf("Actualizando colección ID: {} - Título: {}", coleccion.getId(), coleccion.getTitulo());
                this.actualizarColeccion(coleccion);
            }
            System.out.print("Actualización de colecciones completada exitosamente");
        } catch (org.springframework.transaction.TransactionTimedOutException e) {
            System.out.printf("Timeout al actualizar colecciones - La transacción excedió los 30 segundos", e);
            throw new RuntimeException("Timeout al actualizar colecciones: " + e.getMessage(), e);
        } catch (org.springframework.dao.DataAccessException e) {
            System.out.printf("Error de acceso a datos al actualizar colecciones", e);
            throw new RuntimeException("Error de base de datos al actualizar colecciones: " + e.getMessage(), e);
        } catch (Exception e) {
            System.out.printf("Error inesperado al actualizar colecciones", e);
            throw new RuntimeException("Error al actualizar colecciones: " + e.getMessage(), e);
        }
    }

    public void actualizarColeccion(Coleccion coleccion) {
        System.out.printf("Procesando colección: {}", coleccion.getTitulo());

        CriteriosDePertenencia criterio = coleccion.getCriterio_pertenencia();
        if (criterio == null) {
            System.out.printf("Colección {} no tiene criterios de pertenencia definidos", coleccion.getId());
            return;
        }

        List<Hecho> hechosCumplenCriterio = hechoRepositorio.filtrarHechos(
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

        System.out.printf("Se encontraron {} hechos que cumplen el criterio para la colección {}",
                hechosCumplenCriterio.size(), coleccion.getId());
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

    public List<HechoDTOInput> setearOrigenCarga(List<HechoDTOInput> hechosDTO, OrigenCarga origenCarga) {
        for (HechoDTOInput hechoDTO : hechosDTO) {
            hechoDTO.setOrigen_carga(origenCarga.name());
        }

        return hechosDTO;
    }

    @Transactional
    public void cargarColeccionConHechos(Long coleccionId) throws ColeccionNoEncontradaException {

        Coleccion coleccion = coleccionRepositorio.findById(coleccionId)
                .orElseThrow(ColeccionNoEncontradaException::new);
        actualizarColeccion(coleccion);
    }


    @Transactional(readOnly = true)
    public List<HechoDTOoutput> filtrarHechos(

            String categoria,
            Boolean multimedia,
            LocalDateTime fechaCargaDesde,
            LocalDateTime fechaCargaHasta,
            LocalDateTime fechaAcontecimientoDesde,
            LocalDateTime fechaAcontecimientoHasta,
            String origen,
            String titulo,
            String pais,
            String provincia,
            String localidad
    ){

        OrigenCarga origenEnum = null;
        if(origen != null && !origen.isEmpty()) {
            try {
                origenEnum = OrigenCarga.valueOf(origen.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Origen de carga inválido: " + origen);
            }
        }

            List<Hecho> hechosEntidad = hechoRepositorio.filtrarHechos(
                    categoria,
                    multimedia,
                    fechaCargaDesde,
                    fechaCargaHasta,
                    fechaAcontecimientoDesde,
                    fechaAcontecimientoHasta,
                    origenEnum,
                    titulo,
                    pais,
                    provincia,
                    localidad
            );

            return hechosEntidad.stream()
                    .map(this::convertirAHechoDTO)
                    .collect(Collectors.toList());
        }

    public HechoDTOoutput convertirAHechoDTO(Hecho h) {

        // Manejo de nulos para Ubicación
        String loc = null, prov = null, pais = null;
        Double latitud = null, longitud = null;
        if (h.getUbicacion() != null) {
            if (h.getUbicacion().getLocalidad() != null) loc = h.getUbicacion().getLocalidad().getLocalidad();
            if (h.getUbicacion().getProvincia() != null) prov = h.getUbicacion().getProvincia().getProvincia();
            if (h.getUbicacion().getPais() != null) pais = h.getUbicacion().getPais().getPais();
            if (h.getUbicacion().getLatitud() != null) latitud = h.getUbicacion().getLatitud();
            if (h.getUbicacion().getLongitud() != null) longitud = h.getUbicacion().getLongitud();
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
                latitud,
                longitud,
                usuario,
                nombre,
                apellido,
                fechaNac,
                (h.getOrigen() != null) ? h.getOrigen().name() : null
        );
    }

}
