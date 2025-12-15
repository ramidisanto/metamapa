package Repositorio;

import Modelos.Provincia;
import Modelos.Pais;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RepositorioProvincia {
    private List<Provincia> Provincia = new ArrayList<>();;

    public Provincia crearProvincia(String nombre_provincia, Pais pais) {
        Provincia provincia = this.obtenerProvincia(nombre_provincia, pais);
        if (provincia == null) {
            provincia = new Provincia(nombre_provincia, pais);
            agregarProvincia(provincia);
        }
        return provincia;
    }

    public Provincia obtenerProvincia(String nombre_provincia, Pais pais) {
        return this.Provincia.stream()
                .filter(c->c.getPais().equals(pais))
                .filter(c->c.getNombre_provincia().equals(nombre_provincia))
                .findFirst()
                .orElse(null);
    }

    public void agregarProvincia(Provincia provincia) {
        Provincia.add(provincia);
    }

}