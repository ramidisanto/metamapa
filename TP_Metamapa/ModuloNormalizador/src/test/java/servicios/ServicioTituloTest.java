package servicios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioTituloTest {

    private final ServicioTitulo servicioTitulo = new ServicioTitulo();

    @Test
    void normalizarTitulo_DeberiaCapitalizarCadaPalabra() {
        String input = "esto es un TITULO de prueba";
        String resultado = servicioTitulo.normalizarTitulo(input);

        assertEquals("Esto Es Un Titulo De Prueba", resultado);
    }

    @Test
    void normalizarTitulo_CuandoEsNuloDebeRetornarNulo() {
        assertNull(servicioTitulo.normalizarTitulo(null));
    }

    @Test
    void normalizarTitulo_CuandoEsVacioDebeRetornarVacio() {
        assertEquals("", servicioTitulo.normalizarTitulo(""));
    }
}