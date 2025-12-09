package Servicio;

import java.util.List;

public class ResultadoValidacion {
    private final boolean valido;
    private final List<ErrorValidacion> errores;

    public ResultadoValidacion(boolean valido, List<ErrorValidacion> errores) {
        this.valido = valido;
        this.errores = errores;
    }

    public boolean isValido() {
        return valido;
    }

    public List<ErrorValidacion> getErrores() {
        return errores;
    }

    public String getMensajeError() {
        if (valido) return "";
        StringBuilder sb = new StringBuilder("Errores de validación encontrados:\n");
        for (ErrorValidacion error : errores) {
            sb.append("- ").append(error.toString()).append("\n");
        }
        return sb.toString();
    }
}
