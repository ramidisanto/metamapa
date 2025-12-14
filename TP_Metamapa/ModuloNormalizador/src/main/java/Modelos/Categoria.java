package Modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Categoria")
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;
    private String nombre_categoria;

    public Categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }

    public Categoria(){}
}
