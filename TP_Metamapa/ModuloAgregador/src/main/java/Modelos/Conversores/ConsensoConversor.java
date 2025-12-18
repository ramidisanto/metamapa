package Modelos.Conversores;

import Modelos.Entidades.Consenso.Consenso;
import Modelos.Entidades.Consenso.ConsensoAbsoluta;
import Modelos.Entidades.Consenso.ConsensoMayoriaSimple;
import Modelos.Entidades.Consenso.ConsensoMultiplesMenciones;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConsensoConversor implements AttributeConverter<Consenso, String> {

    @Override
    public String convertToDatabaseColumn(Consenso atributo) {
        if (atributo == null) {
            return null;
        }
        if (atributo instanceof ConsensoAbsoluta) {
            return "ABSOLUTA";
        }
        if (atributo instanceof ConsensoMultiplesMenciones) {
            return "MULTIPLES_MENCIONES";
        }
        if (atributo instanceof ConsensoMayoriaSimple) {
            return "MAYORIA_SIMPLE";
        }
        throw new IllegalArgumentException(
                "Tipo de Consenso no soportado: " + atributo.getClass().getName()
        );
    }

    @Override
    public Consenso convertToEntityAttribute(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        switch (valor) {
            case "ABSOLUTA": return new ConsensoAbsoluta();
            case "MULTIPLES_MENCIONES": return new ConsensoMultiplesMenciones();
            case "MAYORIA_SIMPLE": return new ConsensoMayoriaSimple();
            default:
                throw new IllegalArgumentException("Tipo desconocido: " + valor);
        }
    }
}