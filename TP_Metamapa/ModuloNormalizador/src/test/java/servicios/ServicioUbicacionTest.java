package servicios;

import Modelos.DTOs.UbicacionDTOoutput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioUbicacionTest {

    private RestTemplate restTemplateMock;
    private ObjectMapper objectMapper;
    private ServicioUbicacion servicioUbicacion;

    @BeforeEach
    void setUp() {
        restTemplateMock = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        servicioUbicacion = new ServicioUbicacion(restTemplateMock, objectMapper);
    }

    @Test
    void normalizarUbicacion_DeberiaRetornarUbicacionCorrecta() {
        String json = """
                {
                  "address": {
                    "country": "argentina",
                    "state": "buenos aires",
                    "town": "la plata"
                  }
                }
                """;

        when(restTemplateMock.getForObject(anyString(), eq(String.class), anyDouble(), anyDouble()))
                .thenReturn(json);

        UbicacionDTOoutput dto = servicioUbicacion.normalizarUbicacion(-34.9214, -57.9544);

        assertEquals("Argentina", dto.getPais());
        assertEquals("Buenos Aires", dto.getProvincia());
        assertEquals("La Plata", dto.getLocalidad());
        assertEquals(-34.9214, dto.getLatitud());
        assertEquals(-57.9544, dto.getLongitud());
    }
}
