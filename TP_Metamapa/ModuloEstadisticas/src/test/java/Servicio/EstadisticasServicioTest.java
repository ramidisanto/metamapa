package Servicio;

import Modelos.Coleccion;
import Modelos.Estado;
import Modelos.UltimasEstadisticasDTO;
import Repositorio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadisticasServicioTest {

    @Mock
    private ProvinciaRepositorio provinciaRepositorio;
    @Mock
    private CategoriaRepositorio categoriaRepositorio;
    @Mock
    private HechoRepositorio hechoRepositorio;
    @Mock
    private SolicitudRepositorio solicitudRepositorio;
    @Mock
    private ColeccionRepositorio coleccionRepositorio;
    @Mock
    private UltimasEstadisticasRepositorio ultimasEstadisticasRepositorio;


    @InjectMocks
    private EstadisticasServicio estadisticasServicio;

    private Pageable pageable;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 1);
    }



    @Test
    void testProvinciaConMasHechos_ConResultado() {

        when(provinciaRepositorio.getProvinciaConMasHechos(eq(1L), any(Pageable.class)))
                .thenReturn(List.of("Buenos Aires"));

        String resultado = estadisticasServicio.provinciaConMasHechos(1L);

        assertEquals("Buenos Aires", resultado);
    }

    @Test
    void testProvinciaConMasHechos_SinResultado() {

        when(provinciaRepositorio.getProvinciaConMasHechos(eq(1L), any(Pageable.class)))
                .thenReturn(Collections.emptyList());


        String resultado = estadisticasServicio.provinciaConMasHechos(1L);


        assertEquals("Todavía no hay hechos cargados.", resultado);
    }

    @Test
    void testCategoriaConMasHechos_ConResultado() {

        when(categoriaRepositorio.getCategoriaConMasHechos(any(Pageable.class)))
                .thenReturn(List.of("Robos"));


        String resultado = estadisticasServicio.categoriaConMasHechos();


        assertEquals("Robos", resultado);
    }

    @Test
    void testCategoriaConMasHechos_SinResultado() {

        when(categoriaRepositorio.getCategoriaConMasHechos(any(Pageable.class)))
                .thenReturn(Collections.emptyList());


        String resultado = estadisticasServicio.categoriaConMasHechos();


        assertEquals("Todavía no hay hechos cargados.", resultado);
    }

    @Test
    void testProvinciaConMasHechosDeCategoria_ConResultado() {

        when(provinciaRepositorio.getProvinciaConMasHechosDeCategoria(eq("Robos"), any(Pageable.class)))
                .thenReturn(List.of("Cordoba"));


        String resultado = estadisticasServicio.provinciaConMasHechosDeCategoria("Robos");


        assertEquals("Cordoba", resultado);
    }

    @Test
    void testProvinciaConMasHechosDeCategoria_SinResultado() {

        when(provinciaRepositorio.getProvinciaConMasHechosDeCategoria(eq("Robos"), any(Pageable.class)))
                .thenReturn(Collections.emptyList());


        String resultado = estadisticasServicio.provinciaConMasHechosDeCategoria("Robos");


        assertEquals("Todavía no hay hechos cargados.", resultado);
    }

    @Test
    void testObtenerHoraConMasHechos_ConResultado() {

        when(hechoRepositorio.getHoraConMasHechos(eq("Robos"), any(Pageable.class)))
                .thenReturn(List.of(20)); // 8 PM

        Integer resultado = estadisticasServicio.obtenerHoraConMasHechos("Robos");


        assertEquals(20, resultado);
    }

    @Test
    void testObtenerHoraConMasHechos_SinResultado() {

        when(hechoRepositorio.getHoraConMasHechos(eq("Robos"), any(Pageable.class)))
                .thenReturn(Collections.emptyList());


        Integer resultado = estadisticasServicio.obtenerHoraConMasHechos("Robos");


        assertEquals(0, resultado);
    }

    @Test
    void testCantidadSolicitudesSpam_ConResultado() {

        when(solicitudRepositorio.countSolicitudByEstado(Estado.SPAM)).thenReturn(15L);


        Long resultado = estadisticasServicio.cantidadSolicitudesSpam();


        assertEquals(15L, resultado);
    }

    @Test
    void testCantidadSolicitudesSpam_ConResultadoNull() {

        when(solicitudRepositorio.countSolicitudByEstado(Estado.SPAM)).thenReturn(null);


        Long resultado = estadisticasServicio.cantidadSolicitudesSpam();


        assertEquals(0L, resultado);
    }

    @Test
    void testObtenerEstadisticas() {

        UltimasEstadisticasDTO dtoMock = new UltimasEstadisticasDTO();
        when(ultimasEstadisticasRepositorio.getCache()).thenReturn(dtoMock);


        UltimasEstadisticasDTO resultado = estadisticasServicio.obtenerEstadisticas();


        assertNotNull(resultado);
        assertSame(dtoMock, resultado);
    }

    @Test
    void testRecalcularEstadisticas() {

        when(ultimasEstadisticasRepositorio.isEmpty()).thenReturn(false);


        Coleccion c1 = new Coleccion(); c1.setId(1L);
        Coleccion c2 = new Coleccion(); c2.setId(2L);
        when(coleccionRepositorio.findAll()).thenReturn(List.of(c1, c2));


        when(provinciaRepositorio.getProvinciaConMasHechos(eq(1L), any(Pageable.class))).thenReturn(List.of("Buenos Aires"));
        when(provinciaRepositorio.getProvinciaConMasHechos(eq(2L), any(Pageable.class))).thenReturn(List.of("Cordoba"));


        when(categoriaRepositorio.getCategoriaConMasHechos(any(Pageable.class))).thenReturn(List.of("Robos"));


        List<String> categorias = List.of("Robos", "Hurtos");
        when(categoriaRepositorio.getCategorias()).thenReturn(categorias);


        when(provinciaRepositorio.getProvinciaConMasHechosDeCategoria(eq("Robos"), any(Pageable.class))).thenReturn(List.of("Capital Federal"));
        when(provinciaRepositorio.getProvinciaConMasHechosDeCategoria(eq("Hurtos"), any(Pageable.class))).thenReturn(List.of("Santa Fe"));

        when(hechoRepositorio.getHoraConMasHechos(eq("Robos"), any(Pageable.class))).thenReturn(List.of(22));
        when(hechoRepositorio.getHoraConMasHechos(eq("Hurtos"), any(Pageable.class))).thenReturn(List.of(10));


        when(solicitudRepositorio.countSolicitudByEstado(Estado.SPAM)).thenReturn(5L);


        ArgumentCaptor<UltimasEstadisticasDTO> dtoCaptor = ArgumentCaptor.forClass(UltimasEstadisticasDTO.class);


        estadisticasServicio.recalcularEstadisticas();


        verify(ultimasEstadisticasRepositorio).vaciarCache();


        verify(ultimasEstadisticasRepositorio).save(dtoCaptor.capture());
        UltimasEstadisticasDTO dtoGuardado = dtoCaptor.getValue();


        assertNotNull(dtoGuardado);


        assertEquals(2, dtoGuardado.getProvinciaConMasHechosPorColeccion().size());
        assertEquals("Buenos Aires", dtoGuardado.getProvinciaConMasHechosPorColeccion().get(1L));
        assertEquals("Cordoba", dtoGuardado.getProvinciaConMasHechosPorColeccion().get(2L));


        assertEquals("Robos", dtoGuardado.getCategoriaConMasHechos());


        assertEquals(2, dtoGuardado.getProvinciaConMasHechosDeCategoria().size());
        assertEquals("Capital Federal", dtoGuardado.getProvinciaConMasHechosDeCategoria().get("Robos"));
        assertEquals("Santa Fe", dtoGuardado.getProvinciaConMasHechosDeCategoria().get("Hurtos"));


        assertEquals(2, dtoGuardado.getHoraConMasHechosPorCategoria().size());
        assertEquals(22, dtoGuardado.getHoraConMasHechosPorCategoria().get("Robos"));
        assertEquals(10, dtoGuardado.getHoraConMasHechosPorCategoria().get("Hurtos"));


        assertEquals(5L, dtoGuardado.getCantidadSolicitudesSpam());
    }

    @Test
    void testExportarCSV_CacheVacio() {

        when(ultimasEstadisticasRepositorio.getCache()).thenReturn(null);


        String csv = estadisticasServicio.exportarCSV();


        assertEquals("", csv);
    }

    @Test
    void testExportarCSV_ConDatos() {

        UltimasEstadisticasDTO dto = new UltimasEstadisticasDTO();
        dto.setProvinciaConMasHechosPorColeccion(Map.of(1L, "Buenos Aires"));
        dto.setCategoriaConMasHechos("Robos");
        dto.setProvinciaConMasHechosDeCategoria(Map.of("Robos", "Cordoba"));
        dto.setHoraConMasHechosPorCategoria(Map.of("Robos", 20));
        dto.setCantidadSolicitudesSpam(5L);

        when(ultimasEstadisticasRepositorio.getCache()).thenReturn(dto);

        String csv = estadisticasServicio.exportarCSV();

        assertNotNull(csv);

        assertTrue(csv.contains("IdColeccion,Provincia"));
        assertTrue(csv.contains("1,Buenos Aires\n"));

        assertTrue(csv.contains("CategoriaMasHechos"));
        assertTrue(csv.contains("Robos\n\n\n"));

        assertTrue(csv.contains("Categoria,Provincia"));
        assertTrue(csv.contains("Robos,Cordoba\n"));

        assertTrue(csv.contains("# Hora con más hechos por categoría"));
        assertTrue(csv.contains("Categoria,Hora"));
        assertTrue(csv.contains("Robos,20\n"));

        assertTrue(csv.contains("# Cantidad de solicitudes marcadas como SPAM"));
        assertTrue(csv.contains("CantidadSpam"));
        assertTrue(csv.contains("5\n"));
    }
}