package servicios;

import Modelos.HechoDTO;
import Modelos.HechoDTOInput;
import Repositorios.*;
import Modelos.Entidades.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime ;
import java.util.ArrayList;
import java.util.List;
import java.lang.Exception;

@Service
public class HechoServicio {

    final
    HechoRepositorio hechoRepositorio;
    final
    ContribuyenteRepositorio contribuyenteRepositorio;
    final
    CategoriaRepositorio categoriaRepositorio;
    final
    ProvinciaRepositorio provinciaRepositorio;
    final
    PaisRepositorio paisRepositorio;
    final
    LocalidadRepositorio localidadRepositorio;
    final
    UbicacionRepositorio ubicacionRepositorio;

    public HechoServicio(HechoRepositorio hechoRepositorio, ContribuyenteRepositorio contribuyenteRepositorio, CategoriaRepositorio categoriaRepositorio, ProvinciaRepositorio provinciaRepositorio, PaisRepositorio paisRepositorio, LocalidadRepositorio localidadRepositorio, UbicacionRepositorio ubicacionRepositorio) {
        this.hechoRepositorio = hechoRepositorio;
        this.contribuyenteRepositorio = contribuyenteRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
        this.provinciaRepositorio = provinciaRepositorio;
        this.paisRepositorio = paisRepositorio;
        this.localidadRepositorio = localidadRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
    }

    public void crearHecho(HechoDTOInput dto) throws Exception {
        try {
            System.out.println("HECHO DTO INPUT, entre a CREAR HECHOS: ");
            Categoria categoria = this.crearCategoria(dto.getCategoria());
            Contenido contenido = new Contenido(dto.getContenido(), dto.getContenido_multimedia());
            Pais pais = this.crearPais(dto.getPais());
            Provincia provincia = this.crearProvincia(dto.getProvincia(), pais);
            Localidad localidad = this.crearLocalidad(dto.getLocalidad(), provincia);
            Ubicacion ubicacion = this.crearUbicacion(dto.getLatitud(), dto.getLongitud(), localidad, provincia, pais);
            LocalDateTime fechaOcurrencia = dto.getFechaAcontecimiento();
            Contribuyente contribuyente = this.crearContribuyente(dto.getUsuario(), dto.getNombre(), dto.getApellido(), dto.getFechaNacimiento());
            boolean anonimo = dto.getAnonimo();

            Hecho hecho = new Hecho(null, contribuyente.getId(), dto.getTitulo(), dto.getDescripcion(), contenido, categoria, fechaOcurrencia, ubicacion,
                    contribuyente, anonimo, true, false);

            System.out.println("ANTES DE GURDAR EL HECHO -----------------------------------------------------------");
            hechoRepositorio.save(hecho);
        } catch( Exception e) {
            System.out.println("ERROR CREANDO EL HECHO: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Categoria crearCategoria(String nombre) {
        Categoria categoria = categoriaRepositorio.findByNombre(nombre);
        if(categoria == null){
            categoria = new Categoria(nombre);
            categoriaRepositorio.save(categoria);
        }
        return categoria;
    }

    public Contribuyente crearContribuyente( String usuario,String nombre, String apellido, LocalDate fechaNacimiento){
        Contribuyente contribuyente = contribuyenteRepositorio.findByUsuario(usuario);
        if (contribuyente == null) {
            contribuyente = new Contribuyente();
            contribuyente.setUsuario(usuario);
            contribuyente.setNombre(nombre);
            contribuyente.setApellido(apellido);
            contribuyente.setFecha_nacimiento(fechaNacimiento);
            System.out.println("Antes de guardar el contribuyente: ");
            contribuyenteRepositorio.save(contribuyente);
        }

        return contribuyente;
    }

    public Pais crearPais(String nombre) throws Exception {
        if (nombre == null || nombre.isEmpty() ) {
            throw new Exception("El pais no debe ser vacío");
        }
        Pais pais = paisRepositorio.findByPais(nombre);
        if(pais == null){
            pais = new Pais(nombre);
            paisRepositorio.save(pais);
        }
        return pais;
    }

    public Provincia crearProvincia(String nombre, Pais pais) throws Exception {
        if (nombre == null || nombre.isEmpty() ) {
            throw new Exception("La provincia no debe ser vacía");
        }
        Provincia provincia = provinciaRepositorio.findByProvinciaAndPais(nombre, pais);
        if(provincia == null){
            provincia = new Provincia(nombre, pais);
            provinciaRepositorio.save(provincia);
        }
        return provincia;
    }

    public Localidad crearLocalidad(String nombre, Provincia provincia) throws Exception {
        if (nombre == null || nombre.isEmpty() ) {
            throw new Exception("La localidad no debe ser vacía");
        }
        Localidad localidad = localidadRepositorio.findByLocalidadAndProvincia(nombre, provincia);
        if(localidad == null){
            localidad = new Localidad(nombre, provincia);
            localidadRepositorio.save(localidad);
        }
        return localidad;
    }

    public Ubicacion crearUbicacion(Double latitud, Double longitud, Localidad localidad, Provincia provincia, Pais pais) {
        Ubicacion ubicacion = ubicacionRepositorio.findByLatitudAndLongitud(latitud, longitud);
        if(ubicacion == null){
            ubicacion = new Ubicacion(localidad, provincia, pais, latitud, longitud);
            ubicacionRepositorio.save(ubicacion);
        }
        return ubicacion;
    }

    @Transactional
    public List<HechoDTO> obtenerHechos() {
        List<Hecho> hechos = hechoRepositorio.findByPublicadoFalse();
        hechos.forEach(h -> h.setPublicado(true));
        hechoRepositorio.saveAll(hechos);
        return transformarADTOLista(hechos);
    }

    @Transactional
    public List<HechoDTO> obtenerHechosPendientesDeUsuario(String usuario) {
        Contribuyente user = contribuyenteRepositorio.findByUsuario(usuario);
        if (user == null) {
            System.out.println("user" + usuario + "no tiene hechos registrados");
            return new ArrayList<>();
        }
        List<Hecho> hechos = hechoRepositorio.findByIdfuenteAndPublicado(user.getId(), false);
        return transformarADTOLista(hechos);
    }

    public List<HechoDTO> transformarADTOLista(List<Hecho> hechos) {
        List<HechoDTO> hechosDTO = new ArrayList<>();
        for (Hecho hecho : hechos) {
            HechoDTO dto = new HechoDTO(
                    null,
                    hecho.getIdfuente(),
                    hecho.getTitulo(),
                    hecho.getDescripcion(),
                    hecho.getContenido().getTexto(),
                    hecho.getContenido().getContenido_multimedia(),
                    hecho.getCategoria().getNombre(),
                    hecho.getFecha(),
                    null,
                    hecho.getUbicacion().getLocalidad().getLocalidad(),
                    hecho.getUbicacion().getProvincia().getProvincia(),
                    hecho.getUbicacion().getPais().getPais(),
                    hecho.getUbicacion().getLatitud(),
                    hecho.getUbicacion().getLongitud(),
                    hecho.getContribuyente().getUsuario(),
                    hecho.getContribuyente().getNombre(),
                    hecho.getContribuyente().getApellido(),
                    hecho.getContribuyente().getFecha_nacimiento(),
                    hecho.getAnonimo(),
                    hecho.getVisible(),
                    null
            );
            hechosDTO.add(dto);
        }
        return hechosDTO;
    }
}