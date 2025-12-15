package Repositorio;

import Modelos.Localidad;

import Modelos.Pais;
import Modelos.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioLocalidad extends JpaRepository<Localidad,Long> {
    Localidad findByLocalidad(String localidad);
    Localidad findByLocalidadAndProvincia(String localidad, Provincia provincia);
}
