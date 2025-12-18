package Servicio;

import Modelos.DTOs.ColeccionDTOOutput;
import Modelos.DTOs.CriterioDTO;
import Modelos.DTOs.HechoDTO;
import Modelos.Entidades.*;
import Repositorio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime ;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColeccionServicioTest {

    @Mock
    private ColeccionRepositorio coleccionRepositorio;
    @Mock
    private HechoRepositorio hechoRepositorio;
    @Mock
    private CategoriaRepositorio categoriaRepositorio;
    @Mock
    private PaisRepositorio paisRepositorio;
    @Mock
    private ProvinciaRepositorio provinciaRepositorio;
    @Mock
    private LocalidadRepositorio localidadRepositorio;
    @Mock
    private UbicacionRepositorio ubicacionRepositorio;
    @Mock
    private CriterioPertenenciaRepositorio criterioPertenenciaRepositorio;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ColeccionServicio coleccionServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void eliminarColeccion_DeberiaLlamarDeleteById() {
        Long id = 1L;
        coleccionServicio.eliminarColeccion(id);
        verify(coleccionRepositorio).deleteById(id);
    }

    @Test
    void modificarConsenso_DeberiaActualizarConsenso() {
        Coleccion coleccion = new Coleccion("Titulo", "Descripcion", null,null);
        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.of(coleccion));

        coleccionServicio.modificarConsenso(1L, "ABSOLUTA");

        assertNotNull(coleccion.getConsenso());
        assertEquals("ConsensoAbsoluta", coleccion.getConsenso().getClass().getSimpleName());
        verify(coleccionRepositorio).save(coleccion);
    }

    @Test
    void agregarFuente_DeberiaAgregarHechosALaColeccion() {
        Coleccion coleccion = new Coleccion("Titulo", "Desc", null, null);
        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.of(coleccion));

        Hecho hecho = mock(Hecho.class);
        when(hechoRepositorio.findByIdFuenteAndOrigen(1L, OrigenCarga.FUENTE_DINAMICA)).thenReturn(List.of(hecho));

        coleccionServicio.agregarFuente(1L, 1L, "FUENTE_DINAMICA");

        assertTrue(coleccion.getHechos().contains(hecho));
        verify(coleccionRepositorio).save(coleccion);
    }

    @Test
    void eliminarFuente_DeberiaEliminarHechosSegunFuenteYOrigen() {
        Hecho hecho1 = mock(Hecho.class);
        when(hecho1.getIdFuente()).thenReturn(1L);
        when(hecho1.getOrigen()).thenReturn(OrigenCarga.FUENTE_DINAMICA);

        Coleccion coleccion = new Coleccion("Titulo", "Desc", null, null);
        coleccion.agregarHecho(hecho1);

        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.of(coleccion));

        coleccionServicio.eliminarFuente(1L, 1L, "FUENTE_DINAMICA");

        assertTrue(coleccion.getHechos().isEmpty());
        verify(coleccionRepositorio).save(coleccion);
    }

    @Test
    void obtenerColeccion_DeberiaRetornarDTO() {
        CriteriosDePertenencia criteriosDePertenencia = new CriteriosDePertenencia();
        criteriosDePertenencia.setTitulo("Ambiental");
        Coleccion coleccion = new Coleccion("T", "D", null,criteriosDePertenencia);
        when(coleccionRepositorio.findById(1L)).thenReturn(Optional.of(coleccion));

        ColeccionDTOOutput dto = coleccionServicio.obtenerColeccion(1L);

        assertEquals("T", dto.getTitulo());
        assertEquals("D", dto.getDescripcion());
    }

    @Test
    void transformarCriterioADTO_DeberiaTransformar() {
        CriteriosDePertenencia criterio = mock(CriteriosDePertenencia.class);
        when(criterio.getTitulo()).thenReturn("Titulo");
        when(criterio.getMultimedia()).thenReturn(true);

        CriterioDTO dto = coleccionServicio.transformarCriterioADTO(criterio);

        assertEquals("Titulo", dto.getTitulo());
        assertTrue(dto.getContenido_multimedia());
    }

    @Test
    void transformarHechoDTO_DeberiaIncluirDatosUsuarioSiNoEsAnonimo() {
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setUsuario("usuario");
        contribuyente.setNombre("nombre");
        contribuyente.setApellido("apellido");
        contribuyente.setFecha_nacimiento(LocalDateTime.now().toLocalDate());
        Pais pais = new Pais("Pais");
        Provincia provincia = new Provincia("provincia", pais);
        Localidad localidad = new Localidad("localidad", provincia);
        Categoria categoria = new Categoria("Cat");
        Ubicacion ubicacion = new Ubicacion(localidad, provincia, pais, null, null);
        Contenido contenido = new Contenido("Texto", "imagen.png");

        Hecho hecho = new Hecho(null, null, "Titulo", "Desc", contenido, categoria, LocalDateTime.now(),ubicacion,LocalDateTime .now(), OrigenCarga.FUENTE_DINAMICA, true ,contribuyente, false, false, false, false);

        HechoDTO dto = coleccionServicio.transformarHechoDTO(hecho);

        assertEquals("usuario", dto.getUsuario());
        assertEquals("nombre", dto.getNombre());
        assertEquals("apellido", dto.getApellido());
    }

}
