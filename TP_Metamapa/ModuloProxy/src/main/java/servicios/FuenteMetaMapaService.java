package servicios;

import Modelos.DTOs.HechoDTO;
import Modelos.Entidades.Fuente;
import Modelos.Entidades.TipoFuente;
import Repositorios.FuenteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime ;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FuenteMetaMapaService{


    @Autowired
    RestTemplate restTemplate;
    @Autowired
    FuenteRepositorio fuenteRepositorio;


    public List<HechoDTO> obtenerHechos() {
        List<Fuente> fuentes =fuenteRepositorio.findByTipoFuente(TipoFuente.METAMAPA);
        List<HechoDTO> hechosMetamapa = new ArrayList<>();
        for( Fuente fuente : fuentes ) {

                UriComponentsBuilder url = UriComponentsBuilder
                        .fromHttpUrl(fuente.getUrl())
                        .queryParamIfPresent("fechaCargaDesde", Optional.ofNullable(fuente.getFechaUltimaConsulta()));


            ResponseEntity<List<HechoDTO>> response = restTemplate.exchange(
                    url.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getBody() != null ) {
                List<HechoDTO> hechosConIdFuente = this.setearFuente(response.getBody(), fuente.getId());
                hechosMetamapa.addAll(hechosConIdFuente);
            }
            fuente.setFechaUltimaConsulta(LocalDateTime .now());
            fuenteRepositorio.save(fuente);
        }

        return hechosMetamapa;
    }

    public List<HechoDTO> setearFuente(List<HechoDTO> hechos, Long idfuente){
        for ( HechoDTO hecho : hechos){
            hecho.setIdFuente(idfuente);
        }
        return  hechos;
    }


}