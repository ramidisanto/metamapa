package Repositorio;

import Modelos.Localidad;
import Modelos.Provincia;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RepositorioLocalidad {
    private List<Localidad> localidades = new ArrayList<>();

    public Localidad crearLocalidad(String nombre_Localidad, Provincia provincia) {
        Localidad localidad = this.obtenerLocalidad(nombre_Localidad, provincia);
        if (localidad == null) {
            localidad = new Localidad(nombre_Localidad, provincia);
            agregarLocalidad(localidad);
        }
        return localidad;
    }

    public Localidad obtenerLocalidad(String nombre_Localidad, Provincia provincia) {
        return this.localidades.stream()
                .filter(c->c.getProvincia().equals(provincia))
                .filter(c->c.getNombre_localidad().equals(nombre_Localidad))
                .findFirst()
                .orElse(null);
    }
    public void agregarLocalidad(Localidad localidad) {
        localidades.add(localidad);
    }

}