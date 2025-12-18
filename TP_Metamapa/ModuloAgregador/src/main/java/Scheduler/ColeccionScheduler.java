package Scheduler;



import Servicio.ColeccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionScheduler {

    @Autowired
    ColeccionServicio coleccionServicio;



    @Scheduled(cron = "0 0 3 * * ?")
    //@Scheduled(fixedRate = 60000) //PUEBAS
    public void actualizarHechos() {
        coleccionServicio.actualizarHechosConsensuados();
    }
}
