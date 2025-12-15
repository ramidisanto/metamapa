package servicios;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class ServicioTitulo {


    @Cacheable("titulo")
    public String normalizarTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return titulo;
        }

        return Arrays.stream(titulo.trim().split("\\s+"))
                .map(p -> p.substring(0, 1).toUpperCase() + p.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}