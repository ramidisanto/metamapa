package Modelos.Entidades;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColeccionTest {


    @Test
    void agregarHecho_DeberiaAgregarHechoSiNoExiste() {
        Coleccion coleccion = new Coleccion();
        Hecho hecho = new Hecho();

        coleccion.agregarHecho(hecho);

        List<Hecho> hechos = coleccion.getHechos();
        assertEquals(1, hechos.size(), "Debe agregarse un hecho");
        assertTrue(hechos.contains(hecho), "El hecho agregado debe estar en la lista");
    }

    @Test
    void agregarHecho_NoDeberiaAgregarDuplicado() {
        Coleccion coleccion = new Coleccion();
        Hecho hecho = new Hecho();

        coleccion.agregarHecho(hecho);
        coleccion.agregarHecho(hecho);

        List<Hecho> hechos = coleccion.getHechos();
        assertEquals(1, hechos.size(), "No debe agregarse el mismo hecho dos veces");
    }
}

