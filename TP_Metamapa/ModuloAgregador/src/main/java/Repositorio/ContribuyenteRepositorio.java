package Repositorio;

import Modelos.Entidades.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ContribuyenteRepositorio extends JpaRepository<Contribuyente, String>{
    Optional<Contribuyente> findByUsuario(String usuario);

    default Contribuyente buscarOCrear(String usuario, String nombre, String apellido, LocalDate fechaNacimiento) {
        if (usuario == null) return null;

        return findByUsuario(usuario)
                .orElseGet(() -> save(new Contribuyente(usuario, nombre, apellido, fechaNacimiento)));
    }
}
