package Scheduler;

import Servicio.AgregadorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AgregadorScheduler {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    AgregadorServicio agregadorService;

    @Scheduled(fixedRate = 300000)
    public void actualizarHechos() {

//        if (!running.compareAndSet(false, true)) {
//            return;
//        }

        try {
            agregadorService.actualizarHechos();
        } finally {
            running.set(false);
        }
    }
}
