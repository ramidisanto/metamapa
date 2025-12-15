package Repositorio;

import Modelos.Categoria;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RepositorioCategoria {
    private List<Categoria> categorias;

    public RepositorioCategoria(){
        this.categorias = new ArrayList<>();
    }

    public Categoria crearCategoria(String nombre_categoria) {
        Categoria categoria = this.obtenerCategoria(nombre_categoria);
        if (categoria == null) {
            categoria = new Categoria(nombre_categoria);
            agregarCategoria(categoria);
        }
        return categoria;
    }

    public Categoria obtenerCategoria(String nombre){
        return this.categorias.stream()
                .filter(c->c.getNombre_categoria().equals(nombre))
                .findFirst()
                .orElse(null);
    }

    public void agregarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }
}