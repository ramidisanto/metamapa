package Servicio;

import Modelos.Entidades.HechoCSV;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

import java.util.List;

public interface Importador {
    List<HechoCSV> getHechoFromFile (String ruta) throws Exception;
    List<String> getPaths() throws Exception;
    List<HechoCSV>  guardarCSV(String originalFilename, MultipartFile file) throws Exception;
    Path obtenerPath( String name);
}
