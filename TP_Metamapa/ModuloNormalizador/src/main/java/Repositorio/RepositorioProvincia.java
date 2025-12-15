package Repositorio;

import Modelos.Pais;
import Modelos.Provincia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProvincia extends JpaRepository<Provincia, Long> {
    Provincia findByProvincia(String provincia);
    Provincia findByProvinciaAndPais(String provincia, Pais pais);
}
