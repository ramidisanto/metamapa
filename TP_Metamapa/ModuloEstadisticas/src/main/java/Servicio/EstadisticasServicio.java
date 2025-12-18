package Servicio;

import Modelos.Coleccion;
import Modelos.Estado;
import Modelos.UltimasEstadisticasDTO;
import Repositorio.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EstadisticasServicio{

    @Autowired
    ProvinciaRepositorio provinciaRepositorio;
    @Autowired
    CategoriaRepositorio categoriaRepositorio;
    @Autowired
    HechoRepositorio hechoRepositorio;
    @Autowired
    SolicitudRepositorio solicitudRepositorio;
    @Autowired
    ColeccionRepositorio coleccionRepositorio;
    @Autowired
    UltimasEstadisticasRepositorio ultimasEstadisticasRepositorio;

    public String provinciaConMasHechos(Long idColeccion){
        List<String> resultado = provinciaRepositorio.getProvinciaConMasHechos(idColeccion, PageRequest.of(0, 1));
        return resultado.isEmpty() ? "Todavía no hay hechos cargados." : resultado.get(0);
    }

    public String categoriaConMasHechos(){
        List<String> resultado = categoriaRepositorio.getCategoriaConMasHechos(PageRequest.of(0,1));
        return resultado.isEmpty() ? "Todavía no hay hechos cargados." : resultado.get(0);
    }

    public String provinciaConMasHechosDeCategoria(String categoria){
        List<String> resultado = provinciaRepositorio.getProvinciaConMasHechosDeCategoria(categoria, PageRequest.of(0,1));
        return resultado.isEmpty() ? "Todavía no hay hechos cargados." : resultado.get(0);
    }

    public Integer obtenerHoraConMasHechos(String categoria){
        List<Integer> resultado = hechoRepositorio.getHoraConMasHechos(categoria, PageRequest.of(0,1));
        return resultado.isEmpty() ? 0 : resultado.get(0);
    }

    public Long cantidadSolicitudesSpam(){
        Long resultado = solicitudRepositorio.countSolicitudByEstado(Estado.SPAM);
        return resultado == null ? 0 : resultado;
    }

    @Scheduled(fixedRate = 3600000)
    public void recalcularEstadisticas() {
        
            if(!ultimasEstadisticasRepositorio.isEmpty()){
                ultimasEstadisticasRepositorio.vaciarCache();}
            UltimasEstadisticasDTO dto = new UltimasEstadisticasDTO();
            
            Map<Long, String> provinciaPorColeccion = new HashMap<>();
            for(Long idColeccion : coleccionRepositorio.findAll().stream().map(Coleccion::getId).toList()){
                provinciaPorColeccion.put(idColeccion,this.provinciaConMasHechos(idColeccion));
            }
            dto.setProvinciaConMasHechosPorColeccion(provinciaPorColeccion);

            dto.setCategoriaConMasHechos(this.categoriaConMasHechos());

            Map<String, String> provinciaPorCategoria = new HashMap<>();
            List<String> categorias = categoriaRepositorio.getCategorias();
            for (String categoria : categorias) {
                provinciaPorCategoria.put(categoria, this.provinciaConMasHechosDeCategoria(categoria));
            }
            dto.setProvinciaConMasHechosDeCategoria(provinciaPorCategoria);

            Map<String, Integer> horaPorCategoria = new HashMap<>();
            for (String categoria : categorias) {
                horaPorCategoria.put(categoria, this.obtenerHoraConMasHechos(categoria));
            }
            dto.setHoraConMasHechosPorCategoria(horaPorCategoria);

            dto.setCantidadSolicitudesSpam(this.cantidadSolicitudesSpam());

            ultimasEstadisticasRepositorio.save(dto);
        }

        public UltimasEstadisticasDTO obtenerEstadisticas() {
            return ultimasEstadisticasRepositorio.getCache();
        }

    public String exportarCSV() {
        UltimasEstadisticasDTO dto = ultimasEstadisticasRepositorio.getCache();
        if (dto == null) return "";

        StringBuilder csv = new StringBuilder();

        csv.append("# ===============================\n");
        csv.append("# Provincia con más hechos por colección\n");
        csv.append("# ===============================\n");
        csv.append("IdColeccion,Provincia\n");
        dto.getProvinciaConMasHechosPorColeccion().forEach((col, prov) -> {
            csv.append(col).append(",").append(prov).append("\n");
        });
        csv.append("\n\n");


        csv.append("# ===============================\n");
        csv.append("# Categoría con más hechos\n");
        csv.append("# ===============================\n");
        csv.append("CategoriaMasHechos\n");
        csv.append(dto.getCategoriaConMasHechos()).append("\n\n\n");

        csv.append("# ===============================\n");
        csv.append("# Provincia con más hechos por categoría\n");
        csv.append("# ===============================\n");
        csv.append("Categoria,Provincia\n");
        dto.getProvinciaConMasHechosDeCategoria().forEach((cat, prov) -> {
            csv.append(cat).append(",").append(prov).append("\n");
        });
        csv.append("\n\n");

        csv.append("# ===============================\n");
        csv.append("# Hora con más hechos por categoría\n");
        csv.append("# ===============================\n");
        csv.append("Categoria,Hora\n");
        dto.getHoraConMasHechosPorCategoria().forEach((cat, prov) -> {
            csv.append(cat).append(",").append(prov).append("\n");
        });
        csv.append("\n\n");

        csv.append("# ===============================\n");
        csv.append("# Cantidad de solicitudes marcadas como SPAM\n");
        csv.append("# ===============================\n");
        csv.append("CantidadSpam\n");
        csv.append(dto.getCantidadSolicitudesSpam()).append("\n");

        return csv.toString();
    }


}