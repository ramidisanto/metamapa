package servicios;

import Modelos.HechoDTO;
import Modelos.HechoDTOInput;
import Modelos.Entidades.*;
import Repositorios.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HechoServicioTest {

    @Mock private HechoRepositorio hechoRepositorio;
    @Mock private ContribuyenteRepositorio contribuyenteRepositorio;
    @Mock private CategoriaRepositorio categoriaRepositorio;
    @Mock private ProvinciaRepositorio provinciaRepositorio;
    @Mock private PaisRepositorio paisRepositorio;
    @Mock private LocalidadRepositorio localidadRepositorio;
    @Mock private UbicacionRepositorio ubicacionRepositorio;

    @InjectMocks
    private HechoServicio hechoServicio;

    private HechoDTOInput hechoDTOInput;
    private Pais paisEjemplo;
    private Provincia provinciaEjemplo;
    private Localidad localidadEjemplo;
    private Categoria categoriaEjemplo;
    private Ubicacion ubicacionEjemplo;
    private Contribuyente contribuyenteEjemplo;
    private Hecho hechoEjemplo;

    @BeforeEach
    void setUp() {
        hechoDTOInput = new HechoDTOInput();
        hechoDTOInput.setTitulo("Hecho de prueba");
        hechoDTOInput.setDescripcion("Descripción de prueba");
        hechoDTOInput.setContenido("Contenido de prueba");
        hechoDTOInput.setContenido_multimedia("url/imagen.jpg");
        hechoDTOInput.setCategoria("Deportes");
        hechoDTOInput.setFechaAcontecimiento(LocalDateTime.now());
        hechoDTOInput.setPais("Argentina");
        hechoDTOInput.setProvincia("Buenos Aires");
        hechoDTOInput.setLocalidad("CABA");
        hechoDTOInput.setLatitud(-34.6037);
        hechoDTOInput.setLongitud(-58.3816);
        hechoDTOInput.setUsuario("usuario1");
        hechoDTOInput.setNombre("Juan");
        hechoDTOInput.setApellido("Perez");
        hechoDTOInput.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        hechoDTOInput.setAnonimo(false);
        hechoDTOInput.setMostrarNombre(true);
        hechoDTOInput.setMostrarApellido(true);
        hechoDTOInput.setMostrarFechaNacimiento(false);

        paisEjemplo = new Pais("Argentina");
        provinciaEjemplo = new Provincia("Buenos Aires", paisEjemplo);
        localidadEjemplo = new Localidad("CABA", provinciaEjemplo);
        categoriaEjemplo = new Categoria("Deportes");
        ubicacionEjemplo = new Ubicacion(localidadEjemplo, provinciaEjemplo, paisEjemplo, -34.6037, -58.3816);

        contribuyenteEjemplo = new Contribuyente("usuario1", "Juan", "Perez", LocalDate.of(1990, 1, 1));
        contribuyenteEjemplo.setId(1L);

        hechoEjemplo = new Hecho(
                1L, 1L, "Hecho de prueba", "Descripción",
                new Contenido("Contenido", null),
                categoriaEjemplo,
                LocalDateTime.now(),
                ubicacionEjemplo,
                contribuyenteEjemplo,
                false, true, false,
                true, true, false
        );
        hechoEjemplo.setIdHecho(1L);
    }


    @Test
    void crearHecho_conDatosValidos_deberiaCrearHechoExitosamente() throws Exception {
        configurarMocksParaCreacionExitosa();


        hechoServicio.crearHecho(hechoDTOInput);


        verify(categoriaRepositorio).findByNombre("Deportes");
        verify(paisRepositorio).findByPais("Argentina");
        verify(provinciaRepositorio).findByProvinciaAndPais("Buenos Aires", paisEjemplo);
        verify(localidadRepositorio).findByLocalidadAndProvincia("CABA", provinciaEjemplo);
        verify(ubicacionRepositorio).findByLatitudAndLongitud(-34.6037, -58.3816);
        verify(contribuyenteRepositorio).findByUsuario("usuario1");
        verify(hechoRepositorio).save(any(Hecho.class));
    }

    @Test
    void crearHecho_sinPais_deberiaLanzarExcepcion() {

        hechoDTOInput.setPais(null);

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearHecho(hechoDTOInput);
        });
        assertTrue(exception.getMessage().contains("pais"));
    }

    @Test
    void crearHecho_sinProvincia_deberiaLanzarExcepcion() {
        hechoDTOInput.setProvincia(null);
        when(paisRepositorio.findByPais("Argentina")).thenReturn(paisEjemplo);

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearHecho(hechoDTOInput);
        });
        assertTrue(exception.getMessage().contains("provincia"));
    }

    @Test
    void crearHecho_sinLocalidad_deberiaLanzarExcepcion() {
        hechoDTOInput.setLocalidad(null);
        when(paisRepositorio.findByPais("Argentina")).thenReturn(paisEjemplo);
        when(provinciaRepositorio.findByProvinciaAndPais("Buenos Aires", paisEjemplo))
                .thenReturn(provinciaEjemplo);

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearHecho(hechoDTOInput);
        });
        assertTrue(exception.getMessage().contains("localidad"));
    }


    @Test
    void crearCategoria_cuandoNoExiste_deberiaCrearYRetornar() {
        when(categoriaRepositorio.findByNombre("Deportes")).thenReturn(null);
        when(categoriaRepositorio.save(any(Categoria.class))).thenReturn(categoriaEjemplo);

        Categoria resultado = hechoServicio.crearCategoria("Deportes");

        assertNotNull(resultado);
        verify(categoriaRepositorio).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_cuandoExiste_deberiaRetornarExistente() {
        when(categoriaRepositorio.findByNombre("Deportes")).thenReturn(categoriaEjemplo);

        Categoria resultado = hechoServicio.crearCategoria("Deportes");

        assertEquals(categoriaEjemplo, resultado);
        verify(categoriaRepositorio, never()).save(any(Categoria.class));
    }


    @Test
    void crearContribuyente_cuandoNoExiste_deberiaCrearYRetornar() {
        when(contribuyenteRepositorio.findByUsuario("usuario1")).thenReturn(null);
        when(contribuyenteRepositorio.save(any(Contribuyente.class))).thenReturn(contribuyenteEjemplo);

        Contribuyente resultado = hechoServicio.crearContribuyente(
                "usuario1", "Juan", "Perez", LocalDate.of(1990, 1, 1)
        );


        assertNotNull(resultado);
        verify(contribuyenteRepositorio).save(any(Contribuyente.class));
    }

    @Test
    void crearContribuyente_cuandoExiste_deberiaRetornarExistente() {
        when(contribuyenteRepositorio.findByUsuario("usuario1")).thenReturn(contribuyenteEjemplo);

        Contribuyente resultado = hechoServicio.crearContribuyente(
                "usuario1", "Juan", "Perez", LocalDate.of(1990, 1, 1)
        );

        assertEquals(contribuyenteEjemplo, resultado);
        verify(contribuyenteRepositorio, never()).save(any(Contribuyente.class));
    }

    @Test
    void crearPais_conNombreValido_deberiaCrearSiNoExiste() throws Exception {
        when(paisRepositorio.findByPais("Argentina")).thenReturn(null);
        when(paisRepositorio.save(any(Pais.class))).thenReturn(paisEjemplo);

        Pais resultado = hechoServicio.crearPais("Argentina");

        assertNotNull(resultado);
        verify(paisRepositorio).save(any(Pais.class));
    }

    @Test
    void crearPais_conNombreNull_deberiaLanzarExcepcion() {

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearPais(null);
        });
        assertTrue(exception.getMessage().contains("pais"));
    }

    @Test
    void crearPais_conNombreVacio_deberiaLanzarExcepcion() {

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearPais("");
        });
        assertTrue(exception.getMessage().contains("pais"));
    }


    @Test
    void crearProvincia_conDatosValidos_deberiaCrearSiNoExiste() throws Exception {

        when(provinciaRepositorio.findByProvinciaAndPais("Buenos Aires", paisEjemplo))
                .thenReturn(null);
        when(provinciaRepositorio.save(any(Provincia.class))).thenReturn(provinciaEjemplo);


        Provincia resultado = hechoServicio.crearProvincia("Buenos Aires", paisEjemplo);


        assertNotNull(resultado);
        verify(provinciaRepositorio).save(any(Provincia.class));
    }

    @Test
    void crearProvincia_conNombreNull_deberiaLanzarExcepcion() {

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearProvincia(null, paisEjemplo);
        });
        assertTrue(exception.getMessage().contains("provincia"));
    }


    @Test
    void crearLocalidad_conDatosValidos_deberiaCrearSiNoExiste() throws Exception {

        when(localidadRepositorio.findByLocalidadAndProvincia("CABA", provinciaEjemplo))
                .thenReturn(null);
        when(localidadRepositorio.save(any(Localidad.class))).thenReturn(localidadEjemplo);


        Localidad resultado = hechoServicio.crearLocalidad("CABA", provinciaEjemplo);


        assertNotNull(resultado);
        verify(localidadRepositorio).save(any(Localidad.class));
    }

    @Test
    void crearLocalidad_conNombreNull_deberiaLanzarExcepcion() {

        Exception exception = assertThrows(Exception.class, () -> {
            hechoServicio.crearLocalidad(null, provinciaEjemplo);
        });
        assertTrue(exception.getMessage().contains("localidad"));
    }


    @Test
    void crearUbicacion_cuandoNoExiste_deberiaCrearYRetornar() {

        when(ubicacionRepositorio.findByLatitudAndLongitud(-34.6037, -58.3816))
                .thenReturn(null);
        when(ubicacionRepositorio.save(any(Ubicacion.class))).thenReturn(ubicacionEjemplo);


        Ubicacion resultado = hechoServicio.crearUbicacion(
                -34.6037, -58.3816, localidadEjemplo, provinciaEjemplo, paisEjemplo
        );


        assertNotNull(resultado);
        verify(ubicacionRepositorio).save(any(Ubicacion.class));
    }

    @Test
    void crearUbicacion_cuandoExiste_deberiaRetornarExistente() {

        when(ubicacionRepositorio.findByLatitudAndLongitud(-34.6037, -58.3816))
                .thenReturn(ubicacionEjemplo);


        Ubicacion resultado = hechoServicio.crearUbicacion(
                -34.6037, -58.3816, localidadEjemplo, provinciaEjemplo, paisEjemplo
        );


        assertEquals(ubicacionEjemplo, resultado);
        verify(ubicacionRepositorio, never()).save(any(Ubicacion.class));
    }

    @Test
    void obtenerHechos_deberiaRetornarListaYMarcarComoPublicado() {

        Hecho hecho1 = crearHechoEjemplo();
        hecho1.setPublicado(false);
        Hecho hecho2 = crearHechoEjemplo();
        hecho2.setPublicado(false);

        when(hechoRepositorio.findByPublicadoFalse()).thenReturn(Arrays.asList(hecho1, hecho2));
        when(hechoRepositorio.saveAll(anyList())).thenReturn(Arrays.asList(hecho1, hecho2));


        List<HechoDTO> resultado = hechoServicio.obtenerHechos();


        assertEquals(2, resultado.size());
        assertTrue(hecho1.getPublicado());
        assertTrue(hecho2.getPublicado());
        verify(hechoRepositorio).saveAll(anyList());
    }

    @Test
    void obtenerHechos_sinHechosPendientes_deberiaRetornarListaVacia() {

        when(hechoRepositorio.findByPublicadoFalse()).thenReturn(Arrays.asList());


        List<HechoDTO> resultado = hechoServicio.obtenerHechos();


        assertTrue(resultado.isEmpty());
        verify(hechoRepositorio).saveAll(anyList());
    }

    @Test
    void obtenerHechosPendientesDeUsuario_usuarioExiste_deberiaRetornarHechos() {

        when(contribuyenteRepositorio.findByUsuario("usuario1")).thenReturn(contribuyenteEjemplo);
        when(hechoRepositorio.findByIdfuenteAndPublicado(1L, false))
                .thenReturn(Arrays.asList(hechoEjemplo));


        List<HechoDTO> resultado = hechoServicio.obtenerHechosPendientesDeUsuario("usuario1");


        assertEquals(1, resultado.size());
        verify(contribuyenteRepositorio).findByUsuario("usuario1");
    }

    @Test
    void obtenerHechosPendientesDeUsuario_usuarioNoExiste_deberiaRetornarListaVacia() {

        when(contribuyenteRepositorio.findByUsuario("noexiste")).thenReturn(null);


        List<HechoDTO> resultado = hechoServicio.obtenerHechosPendientesDeUsuario("noexiste");


        assertTrue(resultado.isEmpty());
    }

    @Test
    void transformarADTOLista_deberiaConvertirCorrectamente() {

        List<Hecho> hechos = Arrays.asList(hechoEjemplo, crearHechoEjemplo());


        List<HechoDTO> resultado = hechoServicio.transformarADTOLista(hechos);


        assertEquals(2, resultado.size());
        assertEquals(hechoEjemplo.getTitulo(), resultado.get(0).getTitulo());
    }

    @Test
    void transformarADTOLista_conListaVacia_deberiaRetornarListaVacia() {

        List<HechoDTO> resultado = hechoServicio.transformarADTOLista(Arrays.asList());


        assertTrue(resultado.isEmpty());
    }

    private void configurarMocksParaCreacionExitosa() {
        when(categoriaRepositorio.findByNombre("Deportes")).thenReturn(categoriaEjemplo);
        when(paisRepositorio.findByPais("Argentina")).thenReturn(paisEjemplo);
        when(provinciaRepositorio.findByProvinciaAndPais("Buenos Aires", paisEjemplo))
                .thenReturn(provinciaEjemplo);
        when(localidadRepositorio.findByLocalidadAndProvincia("CABA", provinciaEjemplo))
                .thenReturn(localidadEjemplo);
        when(ubicacionRepositorio.findByLatitudAndLongitud(-34.6037, -58.3816))
                .thenReturn(ubicacionEjemplo);
        when(contribuyenteRepositorio.findByUsuario("usuario1")).thenReturn(contribuyenteEjemplo);
        when(hechoRepositorio.save(any(Hecho.class))).thenReturn(hechoEjemplo);
    }

    private Hecho crearHechoEjemplo() {
        Hecho hecho = new Hecho(
                null, 1L, "Título", "Descripción",
                new Contenido("Contenido", null),
                categoriaEjemplo,
                LocalDateTime.now(),
                ubicacionEjemplo,
                contribuyenteEjemplo,
                false, true, false,
                true, true, false
        );
        return hecho;
    }
}