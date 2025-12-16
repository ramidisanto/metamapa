package Servicio;

import Modelos.Entidades.*;
import Modelos.Exceptions.ValidacionError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportadorFileServerLocal implements Importador{

    private String carpetaRelativePath;
    private File carpeta;

    @Autowired
    private ValidadorCSV validador;

    public ImportadorFileServerLocal(@Value("${path.archivos:/src/main/resources/datos/Fuentes_de_hechos/}") String path) {
        this.carpetaRelativePath = path;
        this.carpeta = new File(path);

        if (!this.carpeta.exists()) {
            this.carpeta.mkdirs();
        }
    }

   @Override
   public List<HechoCSV> getHechoFromFile(String ruta) throws Exception {
       System.out.println("EXTRAYENDO HECHOS DE " + ruta);
       HechosCSV hechos = new HechosCSV();
       BufferedReader br = null;
       List<String> erroresValidacion = new ArrayList<>();
       int numeroLinea = 0;

       try {
           // br = new BufferedReader(new FileReader(ruta));
           br = new BufferedReader( // antes estaba el de arriba, en mi compu tomaba mal los tildes
                   new InputStreamReader(
                           new FileInputStream(ruta),
                           StandardCharsets.UTF_8
                   )
           );
           String linea;
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

           // Leer y validar encabezado
           String lineaEncabezado = br.readLine();
           numeroLinea++;

           if (lineaEncabezado == null) {
               throw new ValidacionError("El archivo CSV está vacío");
           }

           String[] encabezado = lineaEncabezado.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
           if (!validador.validarEncabezado(encabezado)) {
               throw new ValidacionError(
                       "Encabezado inválido. Se esperaba: Título,Descripción,Categoría,Latitud,Longitud,Fecha del hecho"
               );
           }

           // Procesar líneas de datos
           while ((linea = br.readLine()) != null) {
               numeroLinea++;

               // Ignorar líneas vacías
               if (linea.trim().isEmpty()) {
                   continue;
               }

               String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

               // Validar la línea
               ResultadoValidacion resultado = validador.validarLinea(campos, numeroLinea);

               if (!resultado.isValido()) {
                   for (ErrorValidacion error : resultado.getErrores()) {
                       erroresValidacion.add(error.toString());
                   }
                   continue; // Saltar esta línea y continuar con la siguiente
               }

               // Si la validación paso, procesar el hecho
               try {
                   String titulo = unquoteCsvField(campos[0]);
                   String descripcion = unquoteCsvField(campos[1]);
                   String categoria = unquoteCsvField(campos[2]);
                   String lat = campos[3].trim();
                   String lon = campos[4].trim();
                   LocalDate fecha = LocalDate.parse(campos[5].trim(), formatter);

                   HechoCSV hecho = HechoCSV.getInstance(
                           titulo,
                           descripcion,
                           categoria,
                           fecha,
                           lat,
                           lon
                   );

                   hechos.addHecho(hecho);

               } catch (Exception e) {
                   erroresValidacion.add(
                           String.format("Línea %d: Error al procesar - %s", numeroLinea, e.getMessage())
                   );
               }
           }

           // Si hubo errores de validación, lanzar excepción con todos los errores
           if (!erroresValidacion.isEmpty()) {
               StringBuilder mensajeError = new StringBuilder();
               mensajeError.append(String.format(
                       "Se encontraron %d errores de validación en el archivo:\n",
                       erroresValidacion.size()
               ));
               for (String error : erroresValidacion) {
                   mensajeError.append("- ").append(error).append("\n");
               }
               System.out.println(mensajeError.toString());
               throw new ValidacionError("Formato del archivo inválido, debe respetar el formato especificado.");
           }

           // Si no se procesó ningún hecho válido
           if (hechos.getHechos().isEmpty()) {
               throw new ValidacionError("No se encontraron registros válidos en el archivo CSV");
           }

           return hechos.getHechos();

       } finally {
           if (br != null) {
               br.close();
           }
       }
   }

    private static String unquoteCsvField(String s) {

        if (s == null) {
            return null;
        }
        s = s.trim();
        // Quita comillas envolventes "..."
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        // Convierte comillas escapadas CSV ("") a una sola (")
        s = s.replace("\"\"", "\"");
        return s;
    }
     @Override
    public List<String> getPaths() throws Exception {
        List<String> paths = new ArrayList<>();
            if(carpeta.exists() && carpeta.isDirectory()) {
                File[] archivos = carpeta.listFiles();
                if(archivos != null) {
                    for (File archivo : archivos){
                        if (archivo.isFile()) {
                            paths.add(archivo.getAbsolutePath());
                        }
                    }
                }
            } else {
                throw new Exception("No existe la carpeta o esta vacia");
            }
            return paths;
    }
    @Override
    public void guardarCSV(String originalFilename, MultipartFile file) throws Exception {
        try {
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String filename = System.currentTimeMillis() + "-" + originalFilename;
            Path filePath = Paths.get(carpetaRelativePath + filename);

            // Guardar el archivo
            Files.copy(file.getInputStream(), filePath,
                    StandardCopyOption.REPLACE_EXISTING);
            try {
                List<HechoCSV> hechos = getHechoFromFile(filePath.toString());
                System.out.println(String.format(
                        "Archivo validado exitosamente. Se encontraron %d registros válidos.",
                        hechos.size()
                ));
            } catch (Exception e) {
                // Si la validación falla, eliminar el archivo
                Files.deleteIfExists(filePath);
                throw new ValidacionError( e.getMessage());
            }
        } catch (Exception e) {

            e.printStackTrace();
            throw e;

        }
    }
}
