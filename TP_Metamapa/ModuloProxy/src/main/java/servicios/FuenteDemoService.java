package servicios;

import Modelos.DTOs.HechoDTO;
import Modelos.Entidades.Fuente;
import Modelos.Entidades.HechoDemo;
import Modelos.Entidades.TipoFuente;
import Repositorios.FuenteRepositorio;
import Repositorios.HechoDemoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.net.MalformedURLException;
import java.time.LocalDateTime ;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service
public class FuenteDemoService {

    @Autowired
    ConexionService conexionService;
    @Autowired
    HechoDemoRepositorio hechoDemoRepositorio;
    @Autowired
    FuenteRepositorio fuenteRepositorio;


    public List<HechoDTO> obtenerHecho() {
        return hechoDemoRepositorio.findByPublicadoFalse().stream().map(this::entityToDTO).toList();
    }



    public void actualizarHechos() {

        Map<String, Object> data;
        List<Fuente> fuentes =fuenteRepositorio.findByTipoFuente(TipoFuente.DEMO);
        List<HechoDemo> hechosDemo = new ArrayList<>();
        for( Fuente fuente : fuentes ) {
            URL url;
            try {
                url = new URL(fuente.getUrl());
            } catch (MalformedURLException e) {
                throw new RuntimeException("URL inválida", e);
            }
            while ((data = conexionService.siguienteHecho(url , fuente.getFechaUltimaConsulta())) != null) {
                HechoDemo entity = mapToEntity(data);
                entity.setFuente(fuente);
                hechosDemo.add(entity);
            }



            if (!hechosDemo.isEmpty()) {
                hechoDemoRepositorio.saveAll(hechosDemo);
            }

            fuente.setFechaUltimaConsulta(LocalDateTime .now());
            fuenteRepositorio.save(fuente);
        }

    }

    private HechoDemo mapToEntity(Map<String, Object> data) {
        HechoDemo entity = new HechoDemo();
        entity.setTitulo((String) data.get("titulo"));
        entity.setDescripcion((String) data.get("descripcion"));
        entity.setContenido((String) data.get("contenido"));
        entity.setContenido_multimedia((String) data.get("contenidoMultimedia"));
        entity.setCategoria((String) data.get("categoria"));
        entity.setFechaAcontecimiento((java.time.LocalDateTime) data.get("fecha"));
        entity.setPais((String) data.get("pais"));
        entity.setProvincia((String) data.get("provincia"));
        entity.setLocalidad((String) data.get("localidad"));
        entity.setLatitud((Double) data.get("latitud"));
        entity.setLongitud((Double) data.get("longitud"));

        return entity;
    }

    private HechoDTO entityToDTO(HechoDemo entity) {
        HechoDTO dto = new HechoDTO();
        dto.setIdFuente(entity.getFuente().getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setContenido(entity.getContenido());
        dto.setContenido_multimedia(entity.getContenido_multimedia());
        dto.setCategoria(entity.getCategoria());
        dto.setFechaAcontecimiento(entity.getFechaAcontecimiento());
        dto.setLocalidad(entity.getLocalidad());
        dto.setProvincia(entity.getProvincia());
        dto.setPais(entity.getPais());
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());
        return dto;
    }


    private HechoDTO convertirDTODesdeMap(Map<String, Object> data) {
        return new HechoDTO(
                (String) data.get("titulo"),
                (String) data.get("descripcion"),
                (String) data.get("contenido"),
                (String) data.get("contenidoMultimedia"),
                (String) data.get("categoria"),
                (LocalDateTime) data.get("fecha"),
                (String) data.get("pais"),
                (String) data.get("provincia"),
                (String) data.get("localidad"),
                (Double) data.get("latitud"),
                (Double) data.get("longitud")
        );
    }

}