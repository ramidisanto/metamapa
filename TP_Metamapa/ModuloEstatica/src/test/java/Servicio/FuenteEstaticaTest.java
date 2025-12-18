package Servicio;

import Modelos.DTOS.HechoDTO;
import Modelos.Entidades.Archivo;
import Modelos.Entidades.Hecho;
import Modelos.Entidades.HechoCSV;
import Repositorio.ArchivoRepository;
import Repositorio.HechosRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuenteEstaticaTest {

    @Mock
    private HechosRepositorio repositorio;

    @Mock
    private ArchivoRepository archivoRepository;

    @Mock
    private Importador importador;

    @InjectMocks
    private FuenteEstatica fuenteEstatica;


    private Archivo mockArchivo;
    private Hecho mockHecho;

    @BeforeEach
    void setUp() throws Exception {
        Field importadorField = FuenteEstatica.class.getDeclaredField("importador");
        importadorField.setAccessible(true);
        importadorField.set(fuenteEstatica, importador);

        mockArchivo = new Archivo();
        mockArchivo.setId(1L);
        mockArchivo.setPath("test/path.csv");

        mockHecho = new Hecho(
                false,
                LocalDateTime.now().minusDays(1),
                "-58.123",
                "-34.456",
                "Robo",
                mockArchivo,
                "Descripción de prueba",
                "Titulo de prueba"
        );
    }

    @Test
    void testGetHechosNoEnviados_ConvierteYActualizaCorrectamente() {
        when(repositorio.findAllByProcesadoFalse()).thenReturn(List.of(mockHecho));

        when(repositorio.save(any(Hecho.class))).thenReturn(mockHecho);

        List<HechoDTO> dtos = fuenteEstatica.getHechosNoEnviados();

        verify(repositorio).findAllByProcesadoFalse();

        ArgumentCaptor<Hecho> hechoCaptor = ArgumentCaptor.forClass(Hecho.class);
        verify(repositorio).save(hechoCaptor.capture());
        assertTrue(hechoCaptor.getValue().getProcesado(), "El hecho debió marcarse como procesado");

        assertEquals(1, dtos.size());

        HechoDTO dto = dtos.get(0);
        assertEquals(mockHecho.getTitulo(), dto.getTitulo());
        assertEquals(mockHecho.getDescripcion(), dto.getDescripcion());
        assertEquals(mockHecho.getCategoria(), dto.getCategoria());
        assertEquals(mockHecho.getArchivo().getId(), dto.getIdFuente());
        assertEquals(Double.parseDouble(mockHecho.getLatitud()), dto.getLatitud());
        assertEquals(Double.parseDouble(mockHecho.getLongitud()), dto.getLongitud());
    }

    @Test
    void testGetHechosNoEnviados_CuandoNoHayHechos() {
        when(repositorio.findAllByProcesadoFalse()).thenReturn(Collections.emptyList());

        List<HechoDTO> dtos = fuenteEstatica.getHechosNoEnviados();

        assertTrue(dtos.isEmpty(), "La lista de DTOs debería estar vacía");
        verify(repositorio).findAllByProcesadoFalse();
        verify(repositorio, never()).save(any(Hecho.class)); // Verificamos que save NUNCA se llamó
    }

    @Test
    void testCargarHechos_ProcesaArchivoNuevoCorrectamente() throws Exception {

        String pathNuevo = "nuevo/archivo.csv";

        when(importador.getPaths()).thenReturn(List.of(pathNuevo));

        when(archivoRepository.findByPath(pathNuevo)).thenReturn(Optional.empty());

        HechoCSV hechoPasado = HechoCSV.getInstance("Titulo 1", "Desc 1", "Robo", LocalDate.now().minusDays(1), "-34.1", "-58.1");
        HechoCSV hechoFuturo = HechoCSV.getInstance("Titulo 2", "Desc 2", "Hurto", LocalDate.now().plusDays(1), "-34.2", "-58.2"); // Este debe ser ignorado

        when(importador.getHechoFromFile(pathNuevo)).thenReturn(List.of(hechoPasado, hechoFuturo));

        ArgumentCaptor<Archivo> archivoCaptor = ArgumentCaptor.forClass(Archivo.class);
        ArgumentCaptor<Hecho> hechoCaptor = ArgumentCaptor.forClass(Hecho.class);

        fuenteEstatica.cargarHechos();

        verify(archivoRepository).findByPath(pathNuevo);

        verify(importador).getHechoFromFile(pathNuevo);

        verify(archivoRepository).save(archivoCaptor.capture());
        assertEquals(pathNuevo, archivoCaptor.getValue().getPath());

        verify(repositorio, times(1)).save(hechoCaptor.capture());

        assertEquals("Titulo 1", hechoCaptor.getValue().getTitulo());
        assertEquals("Desc 1", hechoCaptor.getValue().getDescripcion());
    }

    @Test
    void testCargarHechos_OmiteArchivoExistenteNoModificado() throws Exception {

        String pathExistente = "existente/archivo.csv";

        when(importador.getPaths()).thenReturn(List.of(pathExistente));

        when(archivoRepository.findByPath(pathExistente)).thenReturn(Optional.of(mockArchivo));

        fuenteEstatica.cargarHechos();

        verify(archivoRepository).findByPath(pathExistente);

        verify(importador, never()).getHechoFromFile(anyString());
        verify(archivoRepository, never()).save(any(Archivo.class));
        verify(repositorio, never()).save(any(Hecho.class));
    }

}