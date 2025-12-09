package Servicio;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidadorCSV {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final double LAT_MIN = -90.0;
    private static final double LAT_MAX = 90.0;
    private static final double LON_MIN = -180.0;
    private static final double LON_MAX = 180.0;


    /*Valida una línea del CSV*/
    public ResultadoValidacion validarLinea(String[] campos, int numeroLinea) {
        List<ErrorValidacion> errores = new ArrayList<>();

        // Validar que tenga 6 campos
        if (campos.length != 6) {
            errores.add(new ErrorValidacion(
                    numeroLinea,
                    "Estructura",
                    String.format("Se esperaban 6 campos, se encontraron %d", campos.length)
            ));
            return new ResultadoValidacion(false, errores);
        }

        // Validar Título (campo 0)
        String titulo = campos[0].trim();
        if (titulo.isEmpty() || titulo.replaceAll("\"", "").trim().isEmpty()) {
            errores.add(new ErrorValidacion(numeroLinea, "Título", "El título no puede estar vacío"));
        }

        // Validar Descripción (campo 1)
        String descripcion = campos[1].trim();
        if (descripcion.isEmpty() || descripcion.replaceAll("\"", "").trim().isEmpty()) {
            errores.add(new ErrorValidacion(numeroLinea, "Descripción", "La descripción no puede estar vacía"));
        }

        // Validar Categoría (campo 2)
        String categoria = campos[2].trim();
        if (categoria.isEmpty() || categoria.replaceAll("\"", "").trim().isEmpty()) {
            errores.add(new ErrorValidacion(numeroLinea, "Categoría", "La categoría no puede estar vacía"));
        }

        // Validar Latitud (campo 3)
        String latitudStr = campos[3].trim();
        if (!validarCoordenada(latitudStr, LAT_MIN, LAT_MAX)) {
            errores.add(new ErrorValidacion(
                    numeroLinea,
                    "Latitud",
                    String.format("Latitud inválida '%s'. Debe ser un número entre -90 y 90", latitudStr)
            ));
        }

        // Validar Longitud (campo 4)
        String longitudStr = campos[4].trim();
        if (!validarCoordenada(longitudStr, LON_MIN, LON_MAX)) {
            errores.add(new ErrorValidacion(
                    numeroLinea,
                    "Longitud",
                    String.format("Longitud inválida '%s'. Debe ser un número entre -180 y 180", longitudStr)
            ));
        }

        // Validar Fecha (campo 5)
        String fechaStr = campos[5].trim();
        if (!validarFecha(fechaStr)) {
            errores.add(new ErrorValidacion(
                    numeroLinea,
                    "Fecha",
                    String.format("Fecha inválida '%s'. Debe estar en formato dd/MM/yyyy", fechaStr)
            ));
        }

        return new ResultadoValidacion(errores.isEmpty(), errores);
    }

    /**
     * Valida que una coordenada sea un número válido dentro del rango
     */
    private boolean validarCoordenada(String coordenada, double min, double max) {
        if (coordenada == null || coordenada.isEmpty()) {
            return false;
        }

        try {
            double valor = Double.parseDouble(coordenada);
            return valor >= min && valor <= max && !Double.isNaN(valor) && !Double.isInfinite(valor);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida que una fecha sea válida y esté en el formato correcto
     */
    private boolean validarFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(fecha, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Valida el encabezado del CSV
     */
    public boolean validarEncabezado(String[] encabezado) {
        if (encabezado.length != 6) {
            return false;
        }

        String[] encabezadoEsperado = {
                "Título", "Descripción", "Categoría", "Latitud", "Longitud", "Fecha del hecho"
        };

        for (int i = 0; i < encabezado.length; i++) {
            String campo = encabezado[i].trim().replaceAll("\"", "");
            if (!campo.equalsIgnoreCase(encabezadoEsperado[i])) {
                return false;
            }
        }

        return true;
    }
}
