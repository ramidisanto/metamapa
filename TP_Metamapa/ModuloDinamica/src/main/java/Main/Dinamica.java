package Main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"Controlador", "servicios", "Repositorios", "Modelos", "RateLimit","Configuracion"})
@EnableJpaRepositories(basePackages = "Repositorios")
@EntityScan(basePackages = "Modelos")
public class Dinamica {
    public static void main(String[] args) {
        SpringApplication.run(Dinamica.class, args);
    }
}
