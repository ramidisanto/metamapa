package Servicio;

import Modelos.Entidades.*;
import Modelos.Entidades.Consenso.ConsensoAbsoluta;
import Modelos.Entidades.DTOs.ColeccionDTO;
import Modelos.Entidades.DTOs.CriterioDTO;
import Modelos.Entidades.DTOs.HechoDTOoutput;
import Repositorio.ColeccionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColeccionGraphQLServicioTest {

    @Mock
    private ColeccionRepositorio coleccionRepositorio;

    @Mock
    private AgregadorGraphQLServicio agregadorGraphQLServicio;

    @InjectMocks
    private ColeccionGraphQLServicio coleccionGraphQLServicio;

    private Coleccion coleccionEjemplo;
    private Hecho hechoEjemplo1;
    private Hecho hechoEjemplo2;
    private CriteriosDePertenencia criterioEjemplo;
    private HechoDTOoutput hechoDTOEjemplo;
    private Categoria categoriaEjemplo;
    private Ubicacion ubicacionEjemplo;
    private Pais paisEjemplo;
    private Provincia provinciaEjemplo;
    private Localidad localidadEjemplo;

    @BeforeEach
    void setUp() {
        // Crear entidades de ubicación
        paisEjemplo = new Pais("Argentina");
        provinciaEjemplo = new Provincia("Buenos Aires", paisEjemplo);
        localidadEjemplo = new Localidad("CABA", provinciaEjemplo);
        ubicacionEjemplo = new Ubicacion(localidadEjemplo, provinciaEjemplo, paisEjemplo, -34.6037, -58.3816);

        // Crear categoría
        categoriaEjemplo = new Categoria("Deportes");

        // Crear hechos de ejemplo
        hechoEjemplo1 = new Hecho(
                1L,
                "Hecho 1",
                "Descripción 1",
                new Contenido("Contenido 1", null),
                categoriaEjemplo,
                LocalDateTime.now(),
                ubicacionEjemplo,
                LocalDateTime.now(),
                OrigenCarga.FUENTE_DINAMICA,
                true,
                new Contribuyente("user1", "Juan", "Perez", LocalDate.of(1990, 1, 1)),
                false,
                true,
                true,
                false
        );
        hechoEjemplo1.setId(1L);

        hechoEjemplo2 = new Hecho(
                2L,
                "Hecho 2",
                "Descripción 2",
                new Contenido("Contenido 2", null),
                categoriaEjemplo,
                LocalDateTime.now(),
                ubicacionEjemplo,
                LocalDateTime.now(),
                OrigenCarga.FUENTE_ESTATICA,
                true,
                new Contribuyente("user2", "Maria", "Lopez", LocalDate.of(1992, 5, 15)),
                false,
                true,
                false,
                false
        );
        hechoEjemplo2.setId(2L);

        // Crear HechoDTOoutput de ejemplo
        hechoDTOEjemplo = new HechoDTOoutput(
                1L,
                "Hecho 1",
                "Descripción 1",
                "Contenido 1",
                null,
                "Deportes",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "CABA",
                "Buenos Aires",
                "Argentina",
                -34.6037,
                -58.3816,
                "user1",
                "Juan",
                "Perez",
                LocalDate.of(1990, 1, 1),
                "FUENTE_DINAMICA",
                true,
                true,
                false
        );

        // Crear criterio de pertenencia
        criterioEjemplo = new CriteriosDePertenencia(
                "Deportes en CABA",
                false,
                categoriaEjemplo,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59),
                ubicacionEjemplo,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59),
                OrigenCarga.FUENTE_DINAMICA
        );

        // Crear colección de ejemplo
        coleccionEjemplo = new Coleccion(
                1L,
                "Colección Deportes",
                "Colección de hechos deportivos",
                criterioEjemplo,
                Arrays.asList(hechoEjemplo1, hechoEjemplo2)
        );
        coleccionEjemplo.setConsenso(new ConsensoAbsoluta());
        coleccionEjemplo.setHechosConsensuados(Arrays.asList(hechoEjemplo1));
    }

    // ==================== TESTS LISTAR COLECCIONES ====================

    @Test
    void listarColecciones_conColeccionesExistentes_deberiaRetornarListaDTO() {
        // Arrange
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        ColeccionDTO dto = resultado.get(0);
        assertEquals(1L, dto.getColeccionId());
        assertEquals("Colección Deportes", dto.getTitulo());
        assertEquals("Colección de hechos deportivos", dto.getDescripcion());
        assertNotNull(dto.getHechos());
        assertEquals(2, dto.getHechos().size());
        assertNotNull(dto.getCriterio());
        assertNotNull(dto.getConsenso());
        assertEquals("ConsensoAbsoluta", dto.getConsenso());
        assertEquals(1, dto.getHechosConsensuados().size());
    }

    @Test
    void listarColecciones_sinColecciones_deberiaRetornarListaVacia() {
        // Arrange
        when(coleccionRepositorio.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(coleccionRepositorio).findAll();
    }

    @Test
    void listarColecciones_conMultiplesColecciones_deberiaRetornarTodas() {
        // Arrange
        Coleccion coleccion2 = new Coleccion(
                2L,
                "Colección Política",
                "Hechos políticos",
                criterioEjemplo,
                Arrays.asList(hechoEjemplo1)
        );

        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo, coleccion2);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        assertEquals(2, resultado.size());
    }

    // ==================== TESTS CONVERTIR A COLECCION DTO ====================

    @Test
    void convertirAColeccionDTO_conDatosCompletos_deberiaMapearCorrectamente() {
        // Arrange
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        ColeccionDTO dto = resultado.get(0);
        assertEquals(1L, dto.getColeccionId());
        assertEquals("Colección Deportes", dto.getTitulo());
        assertEquals("Colección de hechos deportivos", dto.getDescripcion());
        assertEquals(2, dto.getHechos().size());
        assertEquals(1, dto.getHechosConsensuados().size());
        assertEquals("ConsensoAbsoluta", dto.getConsenso());
    }

    @Test
    void convertirAColeccionDTO_conHechosNull_deberiaRetornarListaVacia() {
        // Arrange
        coleccionEjemplo.setHechos(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        ColeccionDTO dto = resultado.get(0);
        assertNotNull(dto.getHechos());
        assertTrue(dto.getHechos().isEmpty());
    }

    @Test
    void convertirAColeccionDTO_conHechosConsensuadosNull_deberiaRetornarListaVacia() {
        // Arrange
        coleccionEjemplo.setHechosConsensuados(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        ColeccionDTO dto = resultado.get(0);
        assertNotNull(dto.getHechosConsensuados());
        assertTrue(dto.getHechosConsensuados().isEmpty());
    }

    @Test
    void convertirAColeccionDTO_conCriterioNull_deberiaRetornarCriterioNull() {
        // Arrange
        coleccionEjemplo.setCriterio_pertenencia(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        ColeccionDTO dto = resultado.get(0);
        assertNull(dto.getCriterio());
    }

    @Test
    void convertirAColeccionDTO_conConsensoNull_deberiaRetornarNinguno() {
        // Arrange
        coleccionEjemplo.setConsenso(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        ColeccionDTO dto = resultado.get(0);
        assertEquals("Ninguno", dto.getConsenso());
    }

    // ==================== TESTS CONVERTIR A CRITERIO DTO ====================

    @Test
    void convertirACriterioDTO_conDatosCompletos_deberiaMapearCorrectamente() {
        // Arrange
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        CriterioDTO criterioDTO = resultado.get(0).getCriterio();
        assertNotNull(criterioDTO);
        assertEquals("Deportes en CABA", criterioDTO.getTitulo());
        assertFalse(criterioDTO.getContenido_multimedia());
        assertEquals("Deportes", criterioDTO.getCategoria());
        assertEquals("CABA", criterioDTO.getLocalidad());
        assertEquals("Buenos Aires", criterioDTO.getProvincia());
        assertEquals("Argentina", criterioDTO.getPais());
        assertNotNull(criterioDTO.getFechaCargaDesde());
        assertNotNull(criterioDTO.getFechaCargaHasta());
        assertNotNull(criterioDTO.getFechaAcontecimientoDesde());
        assertNotNull(criterioDTO.getFechaAcontecimientoHasta());
        assertEquals("FUENTE_DINAMICA", criterioDTO.getOrigen_carga());
    }

    @Test
    void convertirACriterioDTO_conUbicacionNull_deberiaRetornarUbicacionesNull() {
        // Arrange
        criterioEjemplo.setUbicacion(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        CriterioDTO criterioDTO = resultado.get(0).getCriterio();
        assertNotNull(criterioDTO);
        assertNull(criterioDTO.getLocalidad());
        assertNull(criterioDTO.getProvincia());
        assertNull(criterioDTO.getPais());
    }

    @Test
    void convertirACriterioDTO_conUbicacionParcial_deberiaManejarNulos() {
        // Arrange
        Ubicacion ubicacionParcial = new Ubicacion();
        ubicacionParcial.setPais(paisEjemplo);
        criterioEjemplo.setUbicacion(ubicacionParcial);

        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        CriterioDTO criterioDTO = resultado.get(0).getCriterio();
        assertNotNull(criterioDTO);
        assertNull(criterioDTO.getLocalidad());
        assertNull(criterioDTO.getProvincia());
        assertEquals("Argentina", criterioDTO.getPais());
    }

    @Test
    void convertirACriterioDTO_conCategoriaNull_deberiaRetornarCategoriaNull() {
        // Arrange
        criterioEjemplo.setCategoria(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        CriterioDTO criterioDTO = resultado.get(0).getCriterio();
        assertNotNull(criterioDTO);
        assertNull(criterioDTO.getCategoria());
    }

    @Test
    void convertirACriterioDTO_conOrigenNull_deberiaRetornarOrigenNull() {
        // Arrange
        criterioEjemplo.setOrigen(null);
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        CriterioDTO criterioDTO = resultado.get(0).getCriterio();
        assertNotNull(criterioDTO);
        assertNull(criterioDTO.getOrigen_carga());
    }

    // ==================== TESTS DE INTEGRACIÓN ====================

    @Test
    void listarColecciones_deberiaLlamarConvertirHechoDTOParaCadaHecho() {
        // Arrange
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        coleccionGraphQLServicio.listarColecciones();

        // Assert
        // 2 hechos normales + 1 hecho consensuado = 3 llamadas
        verify(agregadorGraphQLServicio, times(3)).convertirAHechoDTO(any(Hecho.class));
    }

    @Test
    void listarColecciones_conColeccionVacia_noDeberiaLlamarConvertirHecho() {
        // Arrange
        coleccionEjemplo.setHechos(new ArrayList<>());
        coleccionEjemplo.setHechosConsensuados(new ArrayList<>());
        List<Coleccion> colecciones = Arrays.asList(coleccionEjemplo);
        when(coleccionRepositorio.findAll()).thenReturn(colecciones);

        // Act
        coleccionGraphQLServicio.listarColecciones();

        // Assert
        verify(agregadorGraphQLServicio, never()).convertirAHechoDTO(any(Hecho.class));
    }

    // ==================== TESTS DE CASOS ESPECIALES ====================

    @Test
    void listarColecciones_conColeccionesMixtas_deberiaManejarTodasCorrectamente() {
        // Arrange
        Coleccion coleccionCompleta = coleccionEjemplo;

        Coleccion coleccionSinHechos = new Coleccion();
        coleccionSinHechos.setId(2L);
        coleccionSinHechos.setTitulo("Sin Hechos");
        coleccionSinHechos.setHechos(new ArrayList<>());

        Coleccion coleccionSinConsenso = new Coleccion();
        coleccionSinConsenso.setId(3L);
        coleccionSinConsenso.setTitulo("Sin Consenso");
        coleccionSinConsenso.setConsenso(null);
        coleccionSinConsenso.setHechos(Arrays.asList(hechoEjemplo1));

        List<Coleccion> colecciones = Arrays.asList(
                coleccionCompleta, coleccionSinHechos, coleccionSinConsenso
        );

        when(coleccionRepositorio.findAll()).thenReturn(colecciones);
        when(agregadorGraphQLServicio.convertirAHechoDTO(any(Hecho.class)))
                .thenReturn(hechoDTOEjemplo);

        // Act
        List<ColeccionDTO> resultado = coleccionGraphQLServicio.listarColecciones();

        // Assert
        assertEquals(3, resultado.size());
        assertEquals("ConsensoAbsoluta", resultado.get(0).getConsenso());
        assertTrue(resultado.get(1).getHechos().isEmpty());
        assertEquals("Ninguno", resultado.get(2).getConsenso());
    }
}