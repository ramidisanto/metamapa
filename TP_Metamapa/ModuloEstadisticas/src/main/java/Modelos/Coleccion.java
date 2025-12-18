package Modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Coleccion")
public class Coleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn()
    private CriteriosDePertenencia criterio_pertenencia;

    @ManyToMany()
    @JoinTable()
    private List<Hecho> hechos;


    public Coleccion(Long id, String titulo, String descripcion, CriteriosDePertenencia criterio_pertenencia, List<Hecho> hechos) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.criterio_pertenencia = criterio_pertenencia;
        this.hechos = hechos;

    }

    public Coleccion() {}


    public void agregarHecho(Hecho unHecho) {
        if (! hechos.contains(unHecho)) {
            hechos.add(unHecho);
        }
    }

    public void agregarHechos(List<Hecho> hechosNuevos) {
       for (Hecho hecho : hechosNuevos) {
           agregarHecho(hecho);
       }
    }


}




