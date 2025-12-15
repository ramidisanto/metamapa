package Scheduler;

import Servicio.AgregadorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class AgregadorScheduler {

    @Autowired
    AgregadorServicio agregadorService;

    @Scheduled(fixedRate = 3600000)
    public void actualizarHechos() {
        agregadorService.actualizarHechos();
    }
}