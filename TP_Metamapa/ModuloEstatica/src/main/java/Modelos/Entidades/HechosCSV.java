package Modelos.Entidades;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HechosCSV {
    private List<HechoCSV> hechos = new ArrayList<>();

    public HechosCSV() {
    }

    public void addHecho (HechoCSV hecho) {
        if (! hechos.contains(hecho)) {
            hechos.add(hecho);
        }
    }
}
