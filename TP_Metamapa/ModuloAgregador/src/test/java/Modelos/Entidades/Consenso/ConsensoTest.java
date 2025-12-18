package Modelos.Entidades.Consenso;

import Modelos.Entidades.*;
import Repositorio.HechoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsensoTest {

    @Mock
    private HechoRepositorio repositorioMock;

    private Hecho hechoEjemplo;
    private Categoria categoriaEjemplo;
    private Ubicacion ubicacionEjemplo;
    private Contenido contenidoEjemplo;
    private Contribuyente contribuyenteEjemplo;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria();
        ubicacionEjemplo = new Ubicacion();
        contenidoEjemplo = new Contenido();
        contribuyenteEjemplo = new Contribuyente();

        hechoEjemplo = new Hecho(
                1L,
                "Titulo de prueba",
                "Descripcion de prueba",
                contenidoEjemplo,
                categoriaEjemplo,
                LocalDateTime.now(),
                ubicacionEjemplo,
                LocalDateTime.now(),
                OrigenCarga.FUENTE_DINAMICA,
                true,
                contribuyenteEjemplo,
                false,
                false,
                false,
                false

        );
    }



    @Test
    void cuandoTodasLasFuentesTienenElHecho_consensoAbsolutaDeberiaRetornarTrue() {
        // Arrange
        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(5L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(5L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertTrue(resultado);
        verify(repositorioMock).cantidadFuentes();
        verify(repositorioMock).cantidadDeFuentesConHecho(
                hechoEjemplo.getTitulo(),
                hechoEjemplo.getCategoria(),
                hechoEjemplo.getFecha(),
                hechoEjemplo.getUbicacion()
        );
    }

    @Test
    void cuandoNoTodasLasFuentesTienenElHecho_consensoAbsolutaDeberiaRetornarFalse() {
        // Arrange
        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(5L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(3L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void cuandoNingunaFuenteTieneElHecho_consensoAbsolutaDeberiaRetornarFalse() {
        // Arrange
        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(5L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(0L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertFalse(resultado);
    }


    @Test
    void cuandoMayoriaSimpleTieneElHecho_deberiaRetornarTrue() {
        // Arrange
        ConsensoMayoriaSimple consenso = new ConsensoMayoriaSimple(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(10L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(6L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void cuandoExactamenteLaMitadTieneElHecho_deberiaRetornarTrue() {
        // Arrange
        ConsensoMayoriaSimple consenso = new ConsensoMayoriaSimple(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(10L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(5L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void cuandoMenosDeLaMitadTieneElHecho_deberiaRetornarFalse() {
        // Arrange
        ConsensoMayoriaSimple consenso = new ConsensoMayoriaSimple(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(10L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(4L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void cuandoHayNumeroImparDeFuentes_mayoriaSimpleDeberiaCalcularCorrectamente() {
        // Arrange
        ConsensoMayoriaSimple consenso = new ConsensoMayoriaSimple(repositorioMock);
        when(repositorioMock.cantidadFuentes()).thenReturn(9L);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(5L);

        // Act
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        // Assert
        assertTrue(resultado); // 5 >= 9/2 (4.5)
    }



    @Test
    void cuandoHayDosMencionesYNoHayConflictos_deberiaRetornarTrue() {
        // Arrange
        ConsensoMultiplesMenciones consenso = new ConsensoMultiplesMenciones(repositorioMock);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(2L);
        when(repositorioMock.cantidadDeFuentesConMismoTituloDiferentesAtributos(
                anyString(), anyString(), any(Categoria.class), any(LocalDateTime.class),
                any(Ubicacion.class), any(Contribuyente.class), any(Contenido.class)
        )).thenReturn(0L);

        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        assertTrue(resultado);
    }

    @Test
    void cuandoHayMasDeDOsMencionesYNoHayConflictos_deberiaRetornarTrue() {
        // Arrange
        ConsensoMultiplesMenciones consenso = new ConsensoMultiplesMenciones(repositorioMock);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(5L);
        when(repositorioMock.cantidadDeFuentesConMismoTituloDiferentesAtributos(
                anyString(), anyString(), any(Categoria.class), any(LocalDateTime.class),
                any(Ubicacion.class), any(Contribuyente.class), any(Contenido.class)
        )).thenReturn(0L);

        //
        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        assertTrue(resultado);
    }

    @Test
    void cuandoHayDosMencionesConConflictos_deberiaRetornarFalse() {
        // Arrange
        ConsensoMultiplesMenciones consenso = new ConsensoMultiplesMenciones(repositorioMock);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(2L);
        when(repositorioMock.cantidadDeFuentesConMismoTituloDiferentesAtributos(
                anyString(), anyString(), any(Categoria.class), any(LocalDateTime.class),
                any(Ubicacion.class), any(Contribuyente.class), any(Contenido.class)
        )).thenReturn(1L);

        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);

        assertFalse(resultado);
    }

    @Test
    void cuandoHayUnaSolaMencion_deberiaRetornarFalse() {

        ConsensoMultiplesMenciones consenso = new ConsensoMultiplesMenciones(repositorioMock);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(1L);
        when(repositorioMock.cantidadDeFuentesConMismoTituloDiferentesAtributos(
                anyString(), anyString(), any(Categoria.class), any(LocalDateTime.class),
                any(Ubicacion.class), any(Contribuyente.class), any(Contenido.class)
        )).thenReturn(0L);


        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);


        assertFalse(resultado);
    }

    @Test
    void cuandoNoHayMenciones_deberiaRetornarFalse() {
        // Arrange
        ConsensoMultiplesMenciones consenso = new ConsensoMultiplesMenciones(repositorioMock);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(0L);
        when(repositorioMock.cantidadDeFuentesConMismoTituloDiferentesAtributos(
                anyString(), anyString(), any(Categoria.class), any(LocalDateTime.class),
                any(Ubicacion.class), any(Contribuyente.class), any(Contenido.class)
        )).thenReturn(0L);


        Boolean resultado = consenso.tieneConsenso(hechoEjemplo);


        assertFalse(resultado);
    }


    @Test
    void cuandoSeInvocaTieneConsensoConRepositorio_deberiaUsarRepositorioProvisto() {

        HechoRepositorio otroRepositorioMock = mock(HechoRepositorio.class);
        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);

        when(otroRepositorioMock.cantidadFuentes()).thenReturn(3L);
        when(otroRepositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(3L);


        Boolean resultado = consenso.tieneConsenso(hechoEjemplo, otroRepositorioMock);


        assertTrue(resultado);
        verify(otroRepositorioMock).cantidadFuentes();
        verify(otroRepositorioMock).cantidadDeFuentesConHecho(
                hechoEjemplo.getTitulo(),
                hechoEjemplo.getCategoria(),
                hechoEjemplo.getFecha(),
                hechoEjemplo.getUbicacion()
        );
        verifyNoInteractions(repositorioMock);
    }

    @Test
    void cuandoSeInvocaTieneConsensoConRepositorioNull_deberiaLanzarExcepcion() {

        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> consenso.tieneConsenso(hechoEjemplo, null)
        );

        assertEquals("HechoRepositorio no puede ser null", excepcion.getMessage());
    }

    @Test
    void cuandoSeInvocaTieneConsensoConRepositorio_deberiaRestaurarRepositorioOriginal() {
        // Arrange
        HechoRepositorio otroRepositorioMock = mock(HechoRepositorio.class);
        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);

        when(otroRepositorioMock.cantidadFuentes()).thenReturn(1L);
        when(otroRepositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(1L);

        consenso.tieneConsenso(hechoEjemplo, otroRepositorioMock);

        assertEquals(repositorioMock, consenso.getRepositorio());
    }


    @Test
    void cuandoSeLlamaCantidadFuentesConHecho_deberiaInvocarMetodoDelRepositorio() {
        // Arrange
        ConsensoAbsoluta consenso = new ConsensoAbsoluta(repositorioMock);
        when(repositorioMock.cantidadDeFuentesConHecho(
                anyString(), any(Categoria.class), any(LocalDateTime.class), any(Ubicacion.class)
        )).thenReturn(7L);

        // Act
        Long resultado = consenso.cantidadFuentesConHecho(hechoEjemplo);

        // Assert
        assertEquals(7L, resultado);
        verify(repositorioMock).cantidadDeFuentesConHecho(
                hechoEjemplo.getTitulo(),
                hechoEjemplo.getCategoria(),
                hechoEjemplo.getFecha(),
                hechoEjemplo.getUbicacion()
        );
    }
}