package Servicio;

public class ErrorValidacion {
    private final int linea;
    private final String campo;
    private final String mensaje;

    public ErrorValidacion(int linea, String campo, String mensaje) {
        this.linea = linea;
        this.campo = campo;
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return String.format("Línea %d, Campo '%s': %s", linea, campo, mensaje);
    }
}