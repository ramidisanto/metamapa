package Servicio;

import Modelos.DTOs.HechoDTO;
import Modelos.DTOs.SolicitudDTOOutput;
import Modelos.Entidades.*;
import Modelos.Exceptions.EstadoInvalidoException;
import Modelos.Exceptions.SolicitudInvalidaException;
import Repositorio.SolicitudRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudServicioTest {

    @Mock
    private SolicitudRepositorio solicitudRepositorio;

    @Mock
    private ColeccionServicio coleccionServicio;

    @InjectMocks
    private SolicitudServicio solicitudServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---- TEST crearEstado ----
    @Test
    void crearEstado_valido_devuelveEnumCorrecto() {
        Estado estado = solicitudServicio.crearEstado("ACEPTADA");
        assertEquals(Estado.ACEPTADA, estado);
    }

    @Test
    void crearEstado_invalido_lanzaExcepcion() {
        assertThrows(EstadoInvalidoException.class, () ->
                solicitudServicio.crearEstado("OTRO_ESTADO"));
    }

    // ---- TEST solicitudesPendientes ----
    @Test
    void solicitudesPendientes_devuelveListaDeDTOs() {

        Solicitud solicitud = new Solicitud();
        solicitud.setIdSolcitud(1L);
        solicitud.setMotivo("Motivo de prueba");
        solicitud.setEstado(Estado.PENDIENTE);
        solicitud.setHecho(mockHecho());
        solicitud.setFecha_creacion(LocalDateTime.now());

        when(solicitudRepositorio.findByEstado(Estado.PENDIENTE))
                .thenReturn(List.of(solicitud));

        List<SolicitudDTOOutput> resultado = solicitudServicio.solicitudesPendientes();


        assertEquals(1, resultado.size());
        assertEquals("Motivo de prueba", resultado.get(0).getMotivo());
        verify(solicitudRepositorio, times(1)).findByEstado(Estado.PENDIENTE);
    }


    @Test
    void actualizarEstadoSolicitud_Aceptada_eliminaHechoYGuardarSolicitud() {
        Hecho hecho = mockHecho();
        Solicitud solicitud = new Solicitud();
        solicitud.setIdSolcitud(10L);
        solicitud.setHecho(hecho);
        solicitud.setEstado(Estado.PENDIENTE);

        when(solicitudRepositorio.findById(10L)).thenReturn(Optional.of(solicitud));


        solicitudServicio.actualizarEstadoSolicitud(10L, "ACEPTADA");

        verify(coleccionServicio, times(1)).eliminarHecho(hecho.getId());
        verify(solicitudRepositorio, times(1)).save(solicitud);
        assertEquals(Estado.ACEPTADA, solicitud.getEstado());
    }

    @Test
    void actualizarEstadoSolicitud_cuandoNoExiste_lanzaExcepcion() {
        when(solicitudRepositorio.findById(99L)).thenReturn(Optional.empty());
        assertThrows(SolicitudInvalidaException.class, () ->
                solicitudServicio.actualizarEstadoSolicitud(99L, "ACEPTADA"));
    }

    @Test
    void transformarHechoDTO_devuelveDTOConDatosCorrectos() {
        Hecho hecho = mockHecho();

        HechoDTO dto = solicitudServicio.transformarHechoDTO(hecho);

        assertEquals(hecho.getTitulo(), dto.getTitulo());
        assertEquals(hecho.getCategoria().getNombre(), dto.getCategoria());
        assertEquals(hecho.getUbicacion().getPais().getPais(), dto.getPais());
    }


    private Hecho mockHecho() {
        Hecho hecho = new Hecho();
        hecho.setId(1L);
        hecho.setIdFuente(123L);
        hecho.setTitulo("Título de prueba");
        hecho.setDescripcion("Descripción de prueba");
        hecho.setContenido(new Contenido("texto ejemplo", "imagen.jpg"));

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
        hecho.setAnonimo(true);
        hecho.setOrigen(OrigenCarga.valueOf("FUENTE_DINAMICA"));

        return hecho;
    }
}
