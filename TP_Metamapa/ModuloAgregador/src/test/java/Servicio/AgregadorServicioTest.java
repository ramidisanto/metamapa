package Servicio;

import Modelos.Entidades.*;
import Modelos.Entidades.DTOs.HechoDTOInput;
import Modelos.Entidades.DTOs.UbicacionDTOInput;
import Modelos.Entidades.DTOs.UbicacionDTOOutput;
import Modelos.Exceptions.ColeccionNoEncontradaException;
import Repositorio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgregadorServicioTest {

    @Mock private RestTemplate restTemplate;
    @Mock private HechoRepositorio hechoRepositorio;
    @Mock private ColeccionRepositorio coleccionRepositorio;
    @Mock private CategoriaRepositorio categoriaRepositorio;
    @Mock private ProvinciaRepositorio provinciaRepositorio;
    @Mock private PaisRepositorio paisRepositorio;
    @Mock private LocalidadRepositorio localidadRepositorio;
    @Mock private UbicacionRepositorio ubicacionRepositorio;
    @Mock private ContribuyenteRepositorio contribuyenteRepositorio;
    @Mock private ContenidoRepositorio contenidoRepositorio;

    @InjectMocks
    private AgregadorServicio agregadorServicio;

    private HechoDTOInput hechoDTOEjemplo;
    private Pais paisEjemplo;
    private Provincia provinciaEjemplo;
    private Localidad localidadEjemplo;
    private Categoria categoriaEjemplo;
    private Ubicacion ubicacionEjemplo;
    private Contribuyente contribuyenteEjemplo;
    private Contenido contenidoEjemplo;
    private Coleccion coleccionEjemplo;

    @BeforeEach
    void setUp() {
        // Configurar URLs usando ReflectionTestUtils
        ReflectionTestUtils.setField(agregadorServicio, "urlProxy", "http://localhost:8086");
        ReflectionTestUtils.setField(agregadorServicio, "urlNormalizador", "http://localhost:8085");
        ReflectionTestUtils.setField(agregadorServicio, "urlBaseDinamica", "http://localhost:8082");
        ReflectionTestUtils.setField(agregadorServicio, "urlBaseEstatica", "http://localhost:8084");

        // Crear DTO de ejemplo
        hechoDTOEjemplo = new HechoDTOInput();
        hechoDTOEjemplo.setIdFuente(1L);
        hechoDTOEjemplo.setTitulo("Titulo prueba");
        hechoDTOEjemplo.setDescripcion("Descripcion prueba");
        hechoDTOEjemplo.setCategoria("Deportes");
        hechoDTOEjemplo.setPais("Argentina");
        hechoDTOEjemplo.setProvincia("Buenos Aires");
        hechoDTOEjemplo.setLocalidad("CABA");
        hechoDTOEjemplo.setLatitud(-34.6037);
        hechoDTOEjemplo.setLongitud(-58.3816);
        hechoDTOEjemplo.setFechaAcontecimiento(LocalDateTime.now());
        hechoDTOEjemplo.setOrigen_carga("FUENTE_DINAMICA");
        hechoDTOEjemplo.setUsuario("usuario1");
        hechoDTOEjemplo.setNombre("Juan");
        hechoDTOEjemplo.setApellido("Perez");
        hechoDTOEjemplo.setFecha_nacimiento(LocalDate.of(1990, 1, 1));
        hechoDTOEjemplo.setContenido("Contenido de prueba");
        hechoDTOEjemplo.setContenido_multimedia("url/imagen.jpg");
        hechoDTOEjemplo.setAnonimo(false);
        hechoDTOEjemplo.setMostrarNombre(true);
        hechoDTOEjemplo.setMostrarApellido(true);
        hechoDTOEjemplo.setMostrarFechaNacimiento(false);

        // Crear entidades de ejemplo
        paisEjemplo = new Pais("Argentina");
        provinciaEjemplo = new Provincia("Buenos Aires", paisEjemplo);
        localidadEjemplo = new Localidad("CABA", provinciaEjemplo);
        categoriaEjemplo = new Categoria("Deportes");
        ubicacionEjemplo = new Ubicacion(localidadEjemplo, provinciaEjemplo, paisEjemplo, -34.6037, -58.3816);
        contribuyenteEjemplo = new Contribuyente("usuario1", "Juan", "Perez", LocalDate.of(1990, 1, 1));
        contenidoEjemplo = new Contenido("Contenido de prueba", "url/imagen.jpg");

        // Crear colección de ejemplo
        coleccionEjemplo = new Coleccion();
        coleccionEjemplo.setId(1L);
        coleccionEjemplo.setTitulo("Colección Test");
        CriteriosDePertenencia criterio = new CriteriosDePertenencia();
        criterio.setCategoria(categoriaEjemplo);
        criterio.setUbicacion(ubicacionEjemplo);
        coleccionEjemplo.setCriterio_pertenencia(criterio);
    }


    @Test
    void actualizarHechos_deberiaConsultarTodasLasFuentes() {
        // Arrange
        List<HechoDTOInput> listaVacia = new ArrayList<>();
        ResponseEntity<List<HechoDTOInput>> respuestaVacia = ResponseEntity.ok(listaVacia);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(respuestaVacia);

        // Act
        agregadorServicio.actualizarHechos();

        verify(restTemplate, times(4)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void actualizarHechos_cuandoFuenteFalla_deberiaContinuarConOtrasFuentes() {

        List<HechoDTOInput> listaVacia = new ArrayList<>();
        ResponseEntity<List<HechoDTOInput>> respuestaVacia = ResponseEntity.ok(listaVacia);

        when(restTemplate.exchange(
                contains("/demo/hechos"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Fuente caída"));


        agregadorServicio.actualizarHechos();

        verify(restTemplate, times(4)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }




    @Test
    void crearPais_cuandoNoExiste_deberiaCrearYGuardar() {
        when(paisRepositorio.buscarOCrear("Argentina")).thenReturn(paisEjemplo);

        Pais resultado = agregadorServicio.crearPais("Argentina");

        assertNotNull(resultado);
        assertEquals("Argentina", resultado.getPais());
        verify(paisRepositorio).buscarOCrear("Argentina");
    }

    @Test
    void crearPais_conNombreNull_deberiaRetornarNull() {
        // Arrange
        when(paisRepositorio.buscarOCrear(null)).thenReturn(null);


        Pais resultado = agregadorServicio.crearPais(null);

        assertNull(resultado);
    }

    @Test
    void crearProvincia_cuandoNoExiste_deberiaCrearYGuardar() {

        when(provinciaRepositorio.buscarOCrear("Buenos Aires", paisEjemplo))
                .thenReturn(provinciaEjemplo);


        Provincia resultado = agregadorServicio.crearProvincia("Buenos Aires", paisEjemplo);

        assertNotNull(resultado);
        assertEquals("Buenos Aires", resultado.getProvincia());
        verify(provinciaRepositorio).buscarOCrear("Buenos Aires", paisEjemplo);
    }

    @Test
    void crearLocalidad_cuandoNoExiste_deberiaCrearYGuardar() {
        when(localidadRepositorio.buscarOCrear("CABA", provinciaEjemplo))
                .thenReturn(localidadEjemplo);

        Localidad resultado = agregadorServicio.crearLocalidad("CABA", provinciaEjemplo);

        // Assert
        assertNotNull(resultado);
        assertEquals("CABA", resultado.getLocalidad());
        verify(localidadRepositorio).buscarOCrear("CABA", provinciaEjemplo);
    }

    @Test
    void crearUbicacion_conCoordenadasValidas_deberiaCrearYGuardar() {
        when(ubicacionRepositorio.buscarOCrear(-34.6037, -58.3816,
                localidadEjemplo, provinciaEjemplo, paisEjemplo))
                .thenReturn(ubicacionEjemplo);

        Ubicacion resultado = agregadorServicio.crearUbicacion(
                -34.6037, -58.3816, localidadEjemplo, provinciaEjemplo, paisEjemplo
        );

        assertNotNull(resultado);
        assertEquals(-34.6037, resultado.getLatitud());
        assertEquals(-58.3816, resultado.getLongitud());
    }

    @Test
    void crearCategoria_cuandoNoExiste_deberiaCrearYGuardar() {
        // Arrange
        when(categoriaRepositorio.buscarOCrear("Deportes")).thenReturn(categoriaEjemplo);

        // Act
        Categoria resultado = agregadorServicio.crearCategoria("Deportes");

        // Assert
        assertNotNull(resultado);
        assertEquals("Deportes", resultado.getNombre());
        verify(categoriaRepositorio).buscarOCrear("Deportes");
    }

    @Test
    void crearContribuyente_cuandoNoExiste_deberiaCrearYGuardar() {
        // Arrange
        when(contribuyenteRepositorio.buscarOCrear(
                "usuario1", "Juan", "Perez", LocalDate.of(1990, 1, 1)
        )).thenReturn(contribuyenteEjemplo);

        // Act
        Contribuyente resultado = agregadorServicio.crearContribuyente(
                "usuario1", "Juan", "Perez", LocalDate.of(1990, 1, 1)
        );

        // Assert
        assertNotNull(resultado);
        assertEquals("usuario1", resultado.getUsuario());
    }

    @Test
    void crearContribuyente_conUsuarioNull_deberiaRetornarNull() {
        // Arrange
        when(contribuyenteRepositorio.buscarOCrear(null, "Juan", "Perez",
                LocalDate.of(1990, 1, 1))).thenReturn(null);

        // Act
        Contribuyente resultado = agregadorServicio.crearContribuyente(
                null, "Juan", "Perez", LocalDate.of(1990, 1, 1)
        );

        // Assert
        assertNull(resultado);
    }

    @Test
    void crearContenido_cuandoNoExiste_deberiaCrearYGuardar() {
        // Arrange
        when(contenidoRepositorio.buscarOCrear("Texto prueba", "url/imagen.jpg"))
                .thenReturn(contenidoEjemplo);

        // Act
        Contenido resultado = agregadorServicio.crearContenido("Texto prueba", "url/imagen.jpg");

        // Assert
        assertNotNull(resultado);
        assertEquals("Texto prueba", resultado.getTexto());
        verify(contenidoRepositorio).buscarOCrear("Texto prueba", "url/imagen.jpg");
    }

    // ==================== TESTS ACTUALIZAR COLECCIONES ====================

    @Test
    void actualizarColecciones_deberiaProcesarTodasLasColecciones() {
        // Arrange
        Coleccion col1 = new Coleccion();
        col1.setId(1L);
        CriteriosDePertenencia crit1 = new CriteriosDePertenencia();
        col1.setCriterio_pertenencia(crit1);

        Coleccion col2 = new Coleccion();
        col2.setId(2L);
        CriteriosDePertenencia crit2 = new CriteriosDePertenencia();
        col2.setCriterio_pertenencia(crit2);

        List<Coleccion> colecciones = Arrays.asList(col1, col2);

        when(coleccionRepositorio.findAllWithRelations()).thenReturn(colecciones);
        when(hechoRepositorio.buscarHechosPorFiltros(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(new ArrayList<>());

        // Act
        agregadorServicio.actualizarColecciones();

        // Assert
        verify(coleccionRepositorio, times(2)).save(any(Coleccion.class));
    }

    @Test
    void actualizarColeccion_conCriterioNull_deberiaRetornarSinProcesar() {
        // Arrange
        Coleccion coleccion = new Coleccion();
        coleccion.setCriterio_pertenencia(null);

        // Act
        agregadorServicio.actualizarColeccion(coleccion);

        // Assert
        verify(hechoRepositorio, never()).buscarHechosPorFiltros(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    void actualizarColeccion_deberiaAgregarSoloHechosNuevosYVisibles() {
        // Arrange
        Hecho hechoExistente = new Hecho();
        hechoExistente.setId(1L);
        hechoExistente.setVisible(true);

        Hecho hechoNuevoVisible = new Hecho();
        hechoNuevoVisible.setId(2L);
        hechoNuevoVisible.setVisible(true);

        Hecho hechoNuevoNoVisible = new Hecho();
        hechoNuevoNoVisible.setId(3L);
        hechoNuevoNoVisible.setVisible(false);

        coleccionEjemplo.agregarHecho(hechoExistente);

        List<Hecho> hechosFiltrados = Arrays.asList(
                hechoExistente, hechoNuevoVisible, hechoNuevoNoVisible
        );

        when(hechoRepositorio.buscarHechosPorFiltros(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(hechosFiltrados);

        // Act
        agregadorServicio.actualizarColeccion(coleccionEjemplo);

        // Assert
        assertEquals(2, coleccionEjemplo.getHechos().size());
        assertTrue(coleccionEjemplo.getHechos().contains(hechoExistente));
        assertTrue(coleccionEjemplo.getHechos().contains(hechoNuevoVisible));
        assertFalse(coleccionEjemplo.getHechos().contains(hechoNuevoNoVisible));
        verify(coleccionRepositorio).save(coleccionEjemplo);
    }

    @Test
    void actualizarColeccion_conHechosNull_deberiaAgregarTodosLosHechosFiltrados() {
        // Arrange
        Hecho hecho1 = new Hecho();
        hecho1.setId(1L);
        hecho1.setVisible(true);

        coleccionEjemplo.setHechos(null);

        when(hechoRepositorio.buscarHechosPorFiltros(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(Arrays.asList(hecho1));

        // Act
        agregadorServicio.actualizarColeccion(coleccionEjemplo);

        // Assert
        assertNotNull(coleccionEjemplo.getHechos());
        verify(coleccionRepositorio).save(coleccionEjemplo);
    }


    @Test
    void cargarColeccionConHechos_cuandoExiste_deberiaActualizar() throws ColeccionNoEncontradaException {
        // Arrange
        Long coleccionId = 1L;
        when(coleccionRepositorio.findById(coleccionId))
                .thenReturn(Optional.of(coleccionEjemplo));
        when(hechoRepositorio.buscarHechosPorFiltros(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(new ArrayList<>());

        // Act
        agregadorServicio.cargarColeccionConHechos(coleccionId);

        // Assert
        verify(coleccionRepositorio).findById(coleccionId);
        verify(coleccionRepositorio).save(coleccionEjemplo);
    }

    @Test
    void cargarColeccionConHechos_cuandoNoExiste_deberiaLanzarExcepcion() {
        // Arrange
        Long coleccionId = 999L;
        when(coleccionRepositorio.findById(coleccionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ColeccionNoEncontradaException.class, () -> {
            agregadorServicio.cargarColeccionConHechos(coleccionId);
        });
        verify(coleccionRepositorio, never()).save(any());
    }

    // ==================== TESTS CASOS EDGE ====================

    @Test
    void crearEntidades_conDatosIncompletos_deberiaManejarNulos() {
        // Arrange
        when(paisRepositorio.buscarOCrear(null)).thenReturn(null);
        when(provinciaRepositorio.buscarOCrear(null, null)).thenReturn(null);
        when(localidadRepositorio.buscarOCrear(null, null)).thenReturn(null);
        when(categoriaRepositorio.buscarOCrear(null)).thenReturn(null);

        // Act
        Pais pais = agregadorServicio.crearPais(null);
        Provincia provincia = agregadorServicio.crearProvincia(null, null);
        Localidad localidad = agregadorServicio.crearLocalidad(null, null);
        Categoria categoria = agregadorServicio.crearCategoria(null);

        // Assert
        assertNull(pais);
        assertNull(provincia);
        assertNull(localidad);
        assertNull(categoria);
    }

    @Test
    void actualizarColeccion_conListaHechosVacia_deberiaFuncionar() {
        // Arrange
        coleccionEjemplo.setHechos(new ArrayList<>());
        when(hechoRepositorio.buscarHechosPorFiltros(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(new ArrayList<>());

        // Act
        agregadorServicio.actualizarColeccion(coleccionEjemplo);

        // Assert
        assertTrue(coleccionEjemplo.getHechos().isEmpty());
        verify(coleccionRepositorio).save(coleccionEjemplo);
    }

    @Test
    void actualizarColecciones_conListaVacia_noDeberiaGuardarNada() {
        // Arrange
        when(coleccionRepositorio.findAllWithRelations()).thenReturn(new ArrayList<>());

        // Act
        agregadorServicio.actualizarColecciones();

        // Assert
        verify(coleccionRepositorio, never()).save(any());
    }
}