package Servicio;

import Modelos.DTOs.HechoDTO;
import Modelos.Entidades.Fuente;
import Modelos.Entidades.HechoDemo;
import Modelos.Entidades.TipoFuente;
import Repositorios.FuenteRepositorio;
import Repositorios.HechoDemoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import servicios.ConexionService;
import servicios.FuenteDemoService;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuenteDemoServiceTest {

    @Mock
    private ConexionService conexionService;

    @Mock
    private HechoDemoRepositorio hechoDemoRepositorio;

    @Mock
    private FuenteRepositorio fuenteRepositorio;

    @InjectMocks
    private FuenteDemoService fuenteDemoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerHecho_devuelveListaDeHechosNoPublicados() {
        HechoDemo hecho1 = crearHechoDemo(1L, "Titulo 1", false);
        HechoDemo hecho2 = crearHechoDemo(2L, "Titulo 2", false);

        when(hechoDemoRepositorio.findByPublicadoFalse())
                .thenReturn(Arrays.asList(hecho1, hecho2));

        List<HechoDTO> resultado = fuenteDemoService.obtenerHecho();

        assertEquals(2, resultado.size());
        assertEquals("Titulo 1", resultado.get(0).getTitulo());
        assertEquals("Titulo 2", resultado.get(1).getTitulo());
        verify(hechoDemoRepositorio, times(1)).findByPublicadoFalse();
    }

    @Test
    void obtenerHecho_sinHechos_devuelveListaVacia() {
        when(hechoDemoRepositorio.findByPublicadoFalse())
                .thenReturn(Collections.emptyList());

        List<HechoDTO> resultado = fuenteDemoService.obtenerHecho();

        assertTrue(resultado.isEmpty());
        verify(hechoDemoRepositorio, times(1)).findByPublicadoFalse();
    }

    @Test
    void actualizarHechos_conDatosNuevos_guardaEnRepositorio() throws MalformedURLException {
        Fuente fuente = crearFuente(1L, "http://ejemplo.com/api", LocalDateTime.now().minusDays(1));

        Map<String, Object> data1 = crearMapaHecho("Titulo Test", "Descripcion Test");

        when(fuenteRepositorio.findByTipoFuente(TipoFuente.DEMO))
                .thenReturn(Collections.singletonList(fuente));

        when(conexionService.siguienteHecho(any(), any()))
                .thenReturn(data1)
                .thenReturn(null);

        fuenteDemoService.actualizarHechos();

        verify(hechoDemoRepositorio, times(1)).saveAll(anyList());
        verify(fuenteRepositorio, times(1)).save(any(Fuente.class));
    }

    @Test
    void actualizarHechos_sinDatosNuevos_noGuardaHechos() {
        Fuente fuente = crearFuente(1L, "http://ejemplo.com/api", LocalDateTime.now());

        when(fuenteRepositorio.findByTipoFuente(TipoFuente.DEMO))
                .thenReturn(Collections.singletonList(fuente));

        when(conexionService.siguienteHecho(any(), any()))
                .thenReturn(null);

        fuenteDemoService.actualizarHechos();

        verify(hechoDemoRepositorio, never()).saveAll(anyList());
        verify(fuenteRepositorio, times(1)).save(any(Fuente.class));
    }

    @Test
    void actualizarHechos_conVariasFuentes_procesamTodasCorrectamente() {
        Fuente fuente1 = crearFuente(1L, "http://ejemplo1.com/api", LocalDateTime.now().minusDays(1));
        Fuente fuente2 = crearFuente(2L, "http://ejemplo2.com/api", LocalDateTime.now().minusDays(1));

        when(fuenteRepositorio.findByTipoFuente(TipoFuente.DEMO))
                .thenReturn(Arrays.asList(fuente1, fuente2));

        when(conexionService.siguienteHecho(any(), any()))
                .thenReturn(null);

        fuenteDemoService.actualizarHechos();

        verify(fuenteRepositorio, times(2)).save(any(Fuente.class));
    }

    @Test
    void actualizarHechos_urlInvalida_lanzaRuntimeException() {
        Fuente fuente = crearFuente(1L, "url-invalida", LocalDateTime.now());

        when(fuenteRepositorio.findByTipoFuente(TipoFuente.DEMO))
                .thenReturn(Collections.singletonList(fuente));

        assertThrows(RuntimeException.class, () -> fuenteDemoService.actualizarHechos());
    }


    private HechoDemo crearHechoDemo(Long id, String titulo, Boolean publicado) {
        HechoDemo hecho = new HechoDemo();
        hecho.setIdHecho(id);
        hecho.setTitulo(titulo);
        hecho.setDescripcion("Descripcion");
        hecho.setContenido("Contenido");
        hecho.setCategoria("Categoria");
        hecho.setFechaAcontecimiento(LocalDateTime.now());
        hecho.setFechaCarga(LocalDateTime.now());
        hecho.setPais("Argentina");
        hecho.setProvincia("Buenos Aires");
        hecho.setLocalidad("CABA");
        hecho.setLatitud(-34.6037);
        hecho.setLongitud(-58.3816);
        hecho.setPublicado(publicado);
        hecho.setFuente(crearFuente(1L, "http://ejemplo.com", LocalDateTime.now()));
        return hecho;
    }

    private Fuente crearFuente(Long id, String url, LocalDateTime ultimaConsulta) {
        Fuente fuente = new Fuente();
        fuente.setId(id);
        fuente.setUrl(url);
        fuente.setFechaUltimaConsulta(ultimaConsulta);
        fuente.setTipoFuente(TipoFuente.DEMO);
        return fuente;
    }

    private Map<String, Object> crearMapaHecho(String titulo, String descripcion) {
        Map<String, Object> data = new HashMap<>();
        data.put("titulo", titulo);
        data.put("descripcion", descripcion);
        data.put("contenido", "Contenido test");
        data.put("contenidoMultimedia", "imagen.jpg");
        data.put("categoria", "Categoria test");
        data.put("fecha", LocalDateTime.now());
        data.put("pais", "Argentina");
        data.put("provincia", "Buenos Aires");
        data.put("localidad", "CABA");
        data.put("latitud", -34.6037);
        data.put("longitud", -58.3816);
        return data;
    }
}