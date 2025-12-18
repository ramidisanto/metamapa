package Utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextoUtils {

    private TextoUtils() {

    }

    public static String capitalizarCadaPalabra(String texto) {
        if (texto == null || texto.isBlank()) return texto;

        return Arrays.stream(texto.trim().toLowerCase().split("\\s+"))
                .map(p -> Character.toUpperCase(p.charAt(0)) + p.substring(1))
                .collect(Collectors.joining(" "));
    }
}