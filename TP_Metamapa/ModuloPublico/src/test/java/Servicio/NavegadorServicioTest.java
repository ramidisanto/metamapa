package Servicio;

import Modelos.ColeccionDTO;
import Modelos.CriterioDTO;
import Modelos.Entidades.*;
import Modelos.Entidades.Consenso.ConsensoAbsoluta;
import Modelos.Entidades.Consenso.ConsensoMayoriaSimple;
import Modelos.Entidades.Consenso.ConsensoMultiplesMenciones;
import Modelos.Entidades.Excepciones.ColeccionNotFoundException;
import Modelos.Entidades.Excepciones.HechosNoEncontradosException;
import Modelos.HechoDTO;
import Repositorio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NavegadorServicioTest {

    @Mock private HechoRepositorio hechoRepositorio;
    @Mock private ColeccionRepositorio coleccionRepositorio;
    @Mock private PaisRepositorio paisRepositorio;
    @Mock private ProvinciaRepositorio provinciaRepositorio;
    @Mock private LocalidadRepositorio localidadRepositorio;
    @Mock private CategoriaRepositorio categoriaRepositorio;

    @InjectMocks
    private NavegadorServicio navegadorServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearOrigen_devuelveEnumCorrecto() {
        OrigenCarga origen = navegadorServicio.crearOrigen("FUENTE_DINAMICA");
        assertEquals(OrigenCarga.valueOf("FUENTE_DINAMICA"), origen);
    }

    @Test
    void crearOrigen_nuloDevuelveNull() {
        assertNull(navegadorServicio.crearOrigen(null));
    }


    @Test
    void validarColeccion_existente_noLanzaExcepcion() {
        Coleccion coleccion = new Coleccion();
        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.of(coleccion));

        assertDoesNotThrow(() -> navegadorServicio.validarColeccion(1L));
    }

    @Test
    void validarColeccion_inexistente_lanzaExcepcion() {
        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ColeccionNotFoundException.class, () -> navegadorServicio.validarColeccion(1L));
    }


    @Test
    void obtenerHechoPorId_existente_devuelveDTO() {
        Hecho hecho = mockHecho();
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hecho));

        HechoDTO dto = navegadorServicio.obtenerHechoPorId(1L);

        assertEquals(hecho.getTitulo(), dto.getTitulo());
        assertEquals(hecho.getCategoria().getNombre(), dto.getCategoria());
    }

    @Test
    void obtenerHechoPorId_inexistente_lanzaExcepcion() {
        when(hechoRepositorio.findById(2L)).thenReturn(Optional.empty());
        assertThrows(HechosNoEncontradosException.class, () -> navegadorServicio.obtenerHechoPorId(2L));
    }


    @Test
    void transformarAHechoDTO_devuelveDatosCorrectos() {
        Hecho hecho = mockHecho();
        HechoDTO dto = navegadorServicio.transformarAHechoDTO(hecho);

        assertEquals("Título de prueba", dto.getTitulo());
        assertEquals("Argentina", dto.getPais());
        assertEquals("Buenos Aires", dto.getProvincia());
        assertEquals("Avellaneda", dto.getLocalidad());
    }


    @Test
    void convertirConsenso_tiposValidos_devuelveCorrecto() {
        assertEquals("ABSOLUTA", navegadorServicio.convertirConsenso(new ConsensoAbsoluta()));
        assertEquals("MAYORIA_SIMPLE", navegadorServicio.convertirConsenso(new ConsensoMayoriaSimple()));
        assertEquals("MULTIPLES_MENCIONES", navegadorServicio.convertirConsenso(new ConsensoMultiplesMenciones()));
    }

    @Test
    void convertirConsenso_nullDevuelveNull() {
        assertNull(navegadorServicio.convertirConsenso(null));
    }

    // ---- buscarPorTextoLibre ----
    @Test
    void buscarPorTextoLibre_filtraCorrectamente() {
        Hecho hecho1 = mockHecho();
        hecho1.setTitulo("Incendio en Buenos Aires");

        Hecho hecho2 = mockHecho();
        hecho2.setTitulo("Accidente en Córdoba");

        when(hechoRepositorio.buscarTodosVisibles()).thenReturn(List.of(hecho1, hecho2));

        List<HechoDTO> resultados = navegadorServicio.buscarPorTextoLibre("incendio Buenos");

        assertEquals(1, resultados.size());
        assertTrue(resultados.get(0).getTitulo().contains("Incendio"));
    }

    @Test
    void buscarPorTextoLibre_sinResultados_devuelveListaVacia() {
        when(hechoRepositorio.buscarTodosVisibles()).thenReturn(Collections.emptyList());
        List<HechoDTO> resultados = navegadorServicio.buscarPorTextoLibre("algo");
        assertTrue(resultados.isEmpty());
    }


    @Test
    void obtenerPaises_devuelveNombres() {
        Pais p = new Pais(); p.setPais("Argentina");
        when(paisRepositorio.findAll()).thenReturn(List.of(p));

        List<String> resultado = navegadorServicio.obtenerPaises();
        assertEquals(List.of("Argentina"), resultado);
    }

    @Test
    void obtenerCategorias_devuelveNombres() {
        Categoria c = new Categoria(); c.setNombre("Política");
        when(categoriaRepositorio.findAll()).thenReturn(List.of(c));

        List<String> resultado = navegadorServicio.obtenerCategorias();
        assertEquals(List.of("Política"), resultado);
    }


    @Test
    void filtrarHechos_sinColeccion_llamaHechoRepositorio() {
        when(hechoRepositorio.filtrarHechos(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(mockHecho()));

        List<HechoDTO> resultado = navegadorServicio.filtrarHechos(null, null, null,
                null, null, null, null, null, null, null, null, null, false);

        assertEquals(1, resultado.size());
        verify(hechoRepositorio, times(1))
                .filtrarHechos(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void filtrarHechos_conColeccionYCurada_llamaCuradoRepositorio() {
        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.of(new Coleccion()));
        when(coleccionRepositorio.filtrarHechosCuradosEnColeccion(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(mockHecho()));

        List<HechoDTO> resultado = navegadorServicio.filtrarHechos(1L, null, null,
                null, null, null, null, null, null, null, null, null, true);

        assertEquals(1, resultado.size());
        verify(coleccionRepositorio, times(1))
                .filtrarHechosCuradosEnColeccion(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }


    private Hecho mockHecho() {
        Hecho hecho = new Hecho();
        hecho.setId(1L);
        hecho.setIdFuente(123L);
        hecho.setTitulo("Título de prueba");
        hecho.setDescripcion("Descripción de prueba");

        Contenido contenido = new Contenido("texto ejemplo", "imagen.jpg");
        hecho.setContenido(contenido);

        Categoria categoria = new Categoria();
        categoria.setNombre("Política");
        hecho.setCategoria(categoria);

        Pais pais = new Pais(); pais.setPais("Argentina");
        Provincia provincia = new Provincia(); provincia.setProvincia("Buenos Aires");
        Localidad localidad = new Localidad(); localidad.setLocalidad("Avellaneda");

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setPais(pais);
        ubicacion.setProvincia(provincia);
        ubicacion.setLocalidad(localidad);
        hecho.setUbicacion(ubicacion);

        hecho.setFecha(LocalDateTime.now());
        hecho.setFecha_carga(LocalDateTime.now());
        hecho.setAnonimo(false);
        hecho.setVisible(true);
        hecho.setOrigen(OrigenCarga.valueOf("FUENTE_DINAMICA"));

        return hecho;
    }
}
