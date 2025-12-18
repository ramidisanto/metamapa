package servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URL;
import java.time.LocalDateTime ;
import java.util.Map;



@Service
public class ConexionService implements IConexionService {

    @Autowired
    RestTemplate restTemplate;


    @Override
    public Map<String, Object> siguienteHecho(URL url, LocalDateTime  fechaUltimaConsulta) {
        try {
            String urlStr = url.toString();
            if (fechaUltimaConsulta != null) {
                urlStr += "?ultima_consulta=" + fechaUltimaConsulta.toString();
            }
            ResponseEntity<Map> response = restTemplate.getForEntity(urlStr, Map.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}



