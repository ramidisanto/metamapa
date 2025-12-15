/*package servicios;

import Modelos.Categoria;
import Repositorio.RepositorioCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioCategoriaTest {

    private RepositorioCategoria repositorioMock;
    private ServicioCategoria servicioCategoria;

    @BeforeEach
    void setUp() {
        repositorioMock = mock(RepositorioCategoria.class);
        servicioCategoria = new ServicioCategoria(repositorioMock);
    }

    @Test
    void normalizarCategoria_DeberiaCapitalizarCategoria() {
        Categoria cat = new Categoria("HISTORIA");
        when(repositorioMock.crearCategoria("HISTORIA")).thenReturn(cat);

        String resultado = servicioCategoria.normalizarCategoria("historia");

        assertEquals("Historia", resultado);
        verify(repositorioMock).crearCategoria("HISTORIA");
    }

    @Test
    void normalizarCategoria_CuandoNombreVacioDevuelveVacio() {
        when(repositorioMock.crearCategoria("")).thenReturn(new Categoria(""));

        String resultado = servicioCategoria.normalizarCategoria("");

        assertEquals("", resultado);
        verify(repositorioMock).crearCategoria("");
    }
}
*/