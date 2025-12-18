package Servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import servicios.ConexionService;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConexionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ConexionService conexionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void siguienteHecho_sinFechaConsulta_realizaPeticionSinParametro() throws Exception {
        URL url = new URL("http://ejemplo.com/api/hechos");
        Map<String, Object> expectedData = crearMapaHecho();

        when(restTemplate.getForEntity(eq("http://ejemplo.com/api/hechos"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(expectedData, HttpStatus.OK));

        Map<String, Object> resultado = conexionService.siguienteHecho(url, null);

        assertNotNull(resultado);
        assertEquals("Titulo Test", resultado.get("titulo"));
        verify(restTemplate).getForEntity("http://ejemplo.com/api/hechos", Map.class);
    }

    @Test
    void siguienteHecho_conFechaConsulta_incluyeParametro() throws Exception {
        URL url = new URL("http://ejemplo.com/api/hechos");
        LocalDateTime fecha = LocalDateTime.of(2024, 1, 1, 12, 0);
        Map<String, Object> expectedData = crearMapaHecho();

        String urlEsperada = "http://ejemplo.com/api/hechos?ultima_consulta=" + fecha.toString();

        when(restTemplate.getForEntity(eq(urlEsperada), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(expectedData, HttpStatus.OK));

        Map<String, Object> resultado = conexionService.siguienteHecho(url, fecha);

        assertNotNull(resultado);
        verify(restTemplate).getForEntity(urlEsperada, Map.class);
    }

    @Test
    void siguienteHecho_errorEnPeticion_devuelveNull() throws Exception {
        URL url = new URL("http://ejemplo.com/api/hechos");

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Error de conexión"));

        Map<String, Object> resultado = conexionService.siguienteHecho(url, null);

        assertNull(resultado);
    }

    @Test
    void siguienteHecho_respuestaVacia_devuelveMapaVacio() throws Exception {
        URL url = new URL("http://ejemplo.com/api/hechos");
        Map<String, Object> emptyMap = new HashMap<>();

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(emptyMap, HttpStatus.OK));

        Map<String, Object> resultado = conexionService.siguienteHecho(url, null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void siguienteHecho_respuesta404_devuelveNull() throws Exception {
        URL url = new URL("http://ejemplo.com/api/hechos");

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        Map<String, Object> resultado = conexionService.siguienteHecho(url, null);

        assertNull(resultado);
    }

    @Test
    void siguienteHecho_conDatosCompletos_devuelveTodosLosCampos() throws Exception {
        URL url = new URL("http://ejemplo.com/api/hechos");
        Map<String, Object> datosCompletos = crearMapaHechoCompleto();

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(datosCompletos, HttpStatus.OK));

        Map<String, Object> resultado = conexionService.siguienteHecho(url, null);

        assertNotNull(resultado);
        assertEquals("Titulo Completo", resultado.get("titulo"));
        assertEquals("Descripcion Completa", resultado.get("descripcion"));
        assertEquals("Contenido Completo", resultado.get("contenido"));
        assertEquals("imagen.jpg", resultado.get("contenidoMultimedia"));
        assertEquals("Categoria", resultado.get("categoria"));
        assertEquals("Argentina", resultado.get("pais"));
        assertEquals("Buenos Aires", resultado.get("provincia"));
        assertEquals("CABA", resultado.get("localidad"));
        assertEquals(-34.6037, resultado.get("latitud"));
        assertEquals(-58.3816, resultado.get("longitud"));
    }




    private Map<String, Object> crearMapaHecho() {
        Map<String, Object> data = new HashMap<>();
        data.put("titulo", "Titulo Test");
        data.put("descripcion", "Descripcion");
        return data;
    }

    private Map<String, Object> crearMapaHechoCompleto() {
        Map<String, Object> data = new HashMap<>();
        data.put("titulo", "Titulo Completo");
        data.put("descripcion", "Descripcion Completa");
        data.put("contenido", "Contenido Completo");
        data.put("contenidoMultimedia", "imagen.jpg");
        data.put("categoria", "Categoria");
        data.put("fecha", LocalDateTime.now());
        data.put("pais", "Argentina");
        data.put("provincia", "Buenos Aires");
        data.put("localidad", "CABA");
        data.put("latitud", -34.6037);
        data.put("longitud", -58.3816);
        return data;
    }
}