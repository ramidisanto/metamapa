package Main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "Controlador", "servicios", "Repositorio", "Modelos", "Configuracion", "RateLimit"
})
@EnableJpaRepositories(basePackages = "Repositorio")
@EntityScan(basePackages = "Modelos")
public class Normalizador {

    public static void main(String[] args) {
        SpringApplication.run(Normalizador.class, args);
    }
}