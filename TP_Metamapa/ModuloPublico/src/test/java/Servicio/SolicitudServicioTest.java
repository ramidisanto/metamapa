package Servicio;

import Modelos.Entidades.Estado;
import Modelos.Entidades.Hecho;
import Modelos.Entidades.Solicitud;
import Modelos.SolicitudDTOInput;
import Repositorio.HechoRepositorio;
import Repositorio.SolicitudRepositorio;
import Servicio.Solicitudes.DetectorDeSpam;
import Servicio.Solicitudes.SolicitudInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudServicioTest {

    @Mock
    private SolicitudRepositorio solicitudRepositorio;

    @Mock
    private HechoRepositorio hechoRepositorio;

    @Mock
    private DetectorDeSpam detectorDeSpam;

    @InjectMocks
    private SolicitudServicio solicitudServicio;

    private Hecho hechoMock;
    private SolicitudDTOInput solicitudDTOInput;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock de Hecho
        hechoMock = new Hecho();
        hechoMock.setId(1L);
        hechoMock.setTitulo("Hecho de prueba");

        solicitudDTOInput = new SolicitudDTOInput();
        solicitudDTOInput.setIdHecho(1L);
        solicitudDTOInput.setMotivo("Motivo de prueba válido");
    }


    @Test
    void crearSolicitud_HechoExiste_MotivoValido_NoSpam_CreaSolicitudPendiente() {
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio, times(1)).save(solicitudCaptor.capture());

        Solicitud solicitudGuardada = solicitudCaptor.getValue();
        assertNotNull(solicitudGuardada);
        assertEquals(Estado.PENDIENTE, solicitudGuardada.getEstado());
        assertEquals("Motivo de prueba válido", solicitudGuardada.getMotivo());
        assertEquals(hechoMock, solicitudGuardada.getHecho());
        assertNotNull(solicitudGuardada.getFecha_creacion());
    }

    @Test
    void crearSolicitud_MotivoEsSpam_CreaSolicitudConEstadoSpamYMotivoNull() {
        solicitudDTOInput.setMotivo("Compra bitcoin ahora en www.scam.com");
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam("Compra bitcoin ahora en www.scam.com")).thenReturn(true);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio, times(1)).save(solicitudCaptor.capture());

        Solicitud solicitudGuardada = solicitudCaptor.getValue();
        assertNotNull(solicitudGuardada);
        assertEquals(Estado.SPAM, solicitudGuardada.getEstado());
        assertNull(solicitudGuardada.getMotivo()); // Motivo guardado como null para spam
        assertEquals(hechoMock, solicitudGuardada.getHecho());
    }

    @Test
    void crearSolicitud_MotivoConURL_DetectadoComoSpam() {
        solicitudDTOInput.setMotivo("Visita mi sitio https://ejemplo.com");
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(true);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio).save(solicitudCaptor.capture());

        assertEquals(Estado.SPAM, solicitudCaptor.getValue().getEstado());
    }

    @Test
    void crearSolicitud_MotivoCon500Caracteres_NoEsSpam_GuardaCorrectamente() {
        String motivoLargo = "a".repeat(500); // Exactamente 500 caracteres
        solicitudDTOInput.setMotivo(motivoLargo);
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(motivoLargo)).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio).save(solicitudCaptor.capture());

        Solicitud solicitudGuardada = solicitudCaptor.getValue();
        assertEquals(Estado.PENDIENTE, solicitudGuardada.getEstado());
        assertEquals(motivoLargo, solicitudGuardada.getMotivo());
    }


    @Test
    void crearSolicitud_HechoNoExiste_LanzaSolicitudInvalidaException() {
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.empty());

        SolicitudInvalidaException exception = assertThrows(
                SolicitudInvalidaException.class,
                () -> solicitudServicio.crearSolicitud(solicitudDTOInput)
        );

        assertEquals("Hecho con id 1 no existe.", exception.getMessage());
        verify(solicitudRepositorio, never()).save(any());
    }

    @Test
    void crearSolicitud_HechoIdInexistente_LanzaExcepcionConMensajeCorrecto() {
        solicitudDTOInput.setIdHecho(999L);
        when(hechoRepositorio.findById(999L)).thenReturn(Optional.empty());

        SolicitudInvalidaException exception = assertThrows(
                SolicitudInvalidaException.class,
                () -> solicitudServicio.crearSolicitud(solicitudDTOInput)
        );

        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("no existe"));
    }



    @Test
    void crearSolicitud_MotivoVacio_NoEsSpam_GuardaCorrectamente() {
        solicitudDTOInput.setMotivo("");
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam("")).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio).save(solicitudCaptor.capture());

        assertEquals(Estado.PENDIENTE, solicitudCaptor.getValue().getEstado());
        assertEquals("", solicitudCaptor.getValue().getMotivo());
    }

    @Test
    void crearSolicitud_MotivoNull_NoLanzaExcepcion() {
        solicitudDTOInput.setMotivo(null);
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(null)).thenReturn(false);

        assertDoesNotThrow(() -> solicitudServicio.crearSolicitud(solicitudDTOInput));
        verify(solicitudRepositorio, times(1)).save(any(Solicitud.class));
    }

    @Test
    void crearSolicitud_MotivoConCaracteresEspeciales_NoEsSpam_GuardaCorrectamente() {
        solicitudDTOInput.setMotivo("Motivo con ñ, á, é, í, ó, ú y símbolos !@#$%");
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio).save(solicitudCaptor.capture());

        assertEquals(Estado.PENDIENTE, solicitudCaptor.getValue().getEstado());
        assertTrue(solicitudCaptor.getValue().getMotivo().contains("ñ"));
    }

    @Test
    void crearSolicitud_VerificaQueFechaCreacionSeEstableceAutomaticamente() {

        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);


        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio).save(solicitudCaptor.capture());

        assertNotNull(solicitudCaptor.getValue().getFecha_creacion());
    }

    @Test
    void crearSolicitud_MotivoConPalabrasSpam_EsDetectado() {

        solicitudDTOInput.setMotivo("Invierte en bitcoin y gana dinero fácil");
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(true);


        solicitudServicio.crearSolicitud(solicitudDTOInput);


        verify(detectorDeSpam, times(1)).esSpam("Invierte en bitcoin y gana dinero fácil");
        ArgumentCaptor<Solicitud> solicitudCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepositorio).save(solicitudCaptor.capture());

        assertEquals(Estado.SPAM, solicitudCaptor.getValue().getEstado());
    }


    @Test
    void crearSolicitud_VerificaLlamadaAlDetectorDeSpam() {
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        verify(detectorDeSpam, times(1)).esSpam("Motivo de prueba válido");
    }

    @Test
    void crearSolicitud_VerificaQueSoloSeGuardaUnaSolicitud() {
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hechoMock));
        when(detectorDeSpam.esSpam(anyString())).thenReturn(false);

        solicitudServicio.crearSolicitud(solicitudDTOInput);

        verify(solicitudRepositorio, times(1)).save(any(Solicitud.class));
    }
}