package Repositorio;

import Modelos.Pais;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RepositorioPais extends JpaRepository<Pais, Long> {
    Pais findByPais(String pais);
}
