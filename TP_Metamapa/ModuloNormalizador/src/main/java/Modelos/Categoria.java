package Modelos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Categoria {
    String nombre_categoria;

    public Categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }
}