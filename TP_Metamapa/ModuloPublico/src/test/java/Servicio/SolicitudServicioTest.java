package Servicio;

import Modelos.Entidades.Estado;
import Modelos.Entidades.Hecho;
import Modelos.Entidades.Solicitud;
import Modelos.SolicitudDTOInput;
import Repositorio.HechoRepositorio;
import Repositorio.SolicitudRepositorio;
import Servicio.SolicitudServicio;
import Servicio.Solicitudes.SolicitudInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime ;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudServicioTest {

    private SolicitudServicio solicitudServicio;
    private SolicitudRepositorio solicitudRepositorio;
    private HechoRepositorio hechoRepositorio;

    @BeforeEach
    void setUp() {
        solicitudRepositorio = mock(SolicitudRepositorio.class);
        hechoRepositorio = mock(HechoRepositorio.class);
        solicitudServicio = new SolicitudServicio();
        solicitudServicio.solicitudRepositorio = solicitudRepositorio;
        solicitudServicio.hechoRepositorio = hechoRepositorio;
    }

    @Test
    void crearSolicitud_HechoExiste_NoSpam() {
        Hecho hecho = new Hecho();
        hecho.setId(1L);
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.of(hecho));

        SolicitudDTOInput dto = new SolicitudDTOInput();
        dto.setIdHecho(1L);
        dto.setMotivo("Texto valido");

        solicitudServicio.crearSolicitud(dto);

        verify(solicitudRepositorio, times(1)).save(any(Solicitud.class));
    }

    @Test
    void crearSolicitud_HechoNoExiste_DeberiaLanzarExcepcion() {
        when(hechoRepositorio.findById(1L)).thenReturn(Optional.empty());

        SolicitudDTOInput dto = new SolicitudDTOInput();
        dto.setIdHecho(1L);
        dto.setMotivo("Texto");

        assertThrows(SolicitudInvalidaException.class, () -> solicitudServicio.crearSolicitud(dto));
    }

}
