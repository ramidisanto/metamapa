package Modelos.Entidades;

import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime ;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter

public class HechoCSV {
    public static final Map<String, HechoCSV> hechosConTitulos = new HashMap<>();

    private String titulo;
    private String descripcion;
    private Long fuente; // ID
    private String categoria;
    private String latitud;
    private String longitud;
    private LocalDate fechaAcontecimiento;


    protected HechoCSV(String unTitulo, String unaDescripcion, String unaCategoria, LocalDate unaFechaAcontecimiento,
                       String latitud, String longitud) {
        this.titulo = unTitulo;
        this.descripcion = unaDescripcion;
        this.categoria = unaCategoria;
        this.fechaAcontecimiento = unaFechaAcontecimiento;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public static HechoCSV getInstance(String unTitulo, String unaDescripcion, String unaCategoria, LocalDate unaFechaAcontecimiento,
                                       String latitud, String longitud) {

        HechoCSV hechoConEseTitulo = hechosConTitulos.get(unTitulo);
        if (hechoConEseTitulo == null) {
            hechoConEseTitulo = new HechoCSV(unTitulo, unaDescripcion, unaCategoria, unaFechaAcontecimiento, latitud, longitud);
            hechosConTitulos.put(unTitulo, hechoConEseTitulo);
        } else {
            hechoConEseTitulo.sobreescribirse(unTitulo, unaDescripcion, unaCategoria, unaFechaAcontecimiento, latitud, longitud);
        }
        return hechoConEseTitulo;
    }

    public void sobreescribirse(String unTitulo, String unaDescripcion,  String unaCategoria, LocalDate unaFechaAcontecimiento,
                                String latitud, String longitud) {
        this.titulo = unTitulo;
        this.descripcion = unaDescripcion;
        this.categoria = unaCategoria;
        this.fechaAcontecimiento = unaFechaAcontecimiento;
        this.latitud = latitud;
        this.longitud = longitud;
    }

}



