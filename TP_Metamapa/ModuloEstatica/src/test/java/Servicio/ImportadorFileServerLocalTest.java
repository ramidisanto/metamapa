package Servicio;


import Modelos.Entidades.HechoCSV;
import Modelos.Entidades.HechosCSV;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ImportadorFileServerLocalTest {

    private ImportadorFileServerLocal importador;

    @TempDir
    File tempFolder;
    @Value("${path.archivos:/src/main/resources/datos/Fuentes_de_hechos/}")
    private String path;

    @BeforeEach
    void setUp() {

        importador = new ImportadorFileServerLocal(path);
    }

    @AfterEach
    void tearDown() throws Exception {

        Field mapField = HechoCSV.class.getDeclaredField("hechosConTitulos");
        mapField.setAccessible(true);
        Map<String, HechoCSV> mapaEstatico = (Map<String, HechoCSV>) mapField.get(null);
        mapaEstatico.clear();
    }


    private void setPrivateCarpetaField(File newCarpeta) throws Exception {
        Field carpetaField = ImportadorFileServerLocal.class.getDeclaredField("carpeta");
        carpetaField.setAccessible(true);
        carpetaField.set(importador, newCarpeta);
    }

    @Test
    void testGetPaths_ReturnsFilesAndSkipsDirectories() throws Exception {
        File file1 = new File(tempFolder, "data1.csv");
        file1.createNewFile();
        File file2 = new File(tempFolder, "data2.txt");
        file2.createNewFile();
        File subDir = new File(tempFolder, "subfolder");
        subDir.mkdir();

        setPrivateCarpetaField(tempFolder);

        List<String> paths = importador.getPaths();


        assertEquals(2, paths.size());
        assertTrue(paths.contains(file1.getAbsolutePath()), "Debería contener file1");
        assertTrue(paths.contains(file2.getAbsolutePath()), "Debería contener file2");
        assertFalse(paths.contains(subDir.getAbsolutePath()), "No debería contener subdirectorios");
    }

    @Test
    void testGetPaths_ThrowsException_WhenFolderDoesNotExist() throws Exception {
        File nonExistentFolder = new File(tempFolder, "nonexistent");
        setPrivateCarpetaField(nonExistentFolder);

        Exception exception = assertThrows(Exception.class, () -> {
            importador.getPaths();
        });

        assertEquals("No existe la carpeta o esta vacia", exception.getMessage());
    }

    @Test
    void testGetHechoFromFile_ParsesCsvCorrectly() throws Exception {
        File csvFile = new File(tempFolder, "test.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {

            writer.write("Título,Descripción,Categoría,Latitud,Longitud,Fecha\n");
            writer.write("\"Titulo 1, con coma\",\"Descripción simple\",\"Robo\",-34.123, -58.456 ,01/10/2025\n");
            writer.write("Titulo 2,\"Descripción con \"\"comillas\"\" dobles\",\"Hurto\", -34.789 ,-58.999,02/10/2025\n");
            writer.write("Titulo 3 sin quotes,Desc 3,Cat 3, -35.000, -59.000, 03/10/2025\n");
        }

        List<HechoCSV> hechos = importador.getHechoFromFile(csvFile.getAbsolutePath());

        assertEquals(3, hechos.size());

        HechoCSV hecho1 = hechos.get(0);
        assertEquals("Titulo 1, con coma", hecho1.getTitulo());
        assertEquals("Descripción simple", hecho1.getDescripcion());
        assertEquals("Robo", hecho1.getCategoria());
        assertEquals("-34.123", hecho1.getLatitud());
        assertEquals("-58.456", hecho1.getLongitud());
        assertEquals(LocalDate.of(2025, 10, 1), hecho1.getFechaAcontecimiento());

        HechoCSV hecho2 = hechos.get(1);
        assertEquals("Titulo 2", hecho2.getTitulo());
        assertEquals("Descripción con \"comillas\" dobles", hecho2.getDescripcion());
        assertEquals("Hurto", hecho2.getCategoria());
        assertEquals("-34.789", hecho2.getLatitud());
        assertEquals("-58.999", hecho2.getLongitud());
        assertEquals(LocalDate.of(2025, 10, 2), hecho2.getFechaAcontecimiento());

        HechoCSV hecho3 = hechos.get(2);
        assertEquals("Titulo 3 sin quotes", hecho3.getTitulo());
        assertEquals("Desc 3", hecho3.getDescripcion());
        assertEquals("Cat 3", hecho3.getCategoria());
        assertEquals("-35.000", hecho3.getLatitud());
        assertEquals("-59.000", hecho3.getLongitud());
        assertEquals(LocalDate.of(2025, 10, 3), hecho3.getFechaAcontecimiento());
    }

    @Test
    void testGetHechoFromFile_HandlesDuplicatesByTitle() throws Exception {

        File csvFile = new File(tempFolder, "duplicates.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("Título,Descripción,Categoría,Latitud,Longitud,Fecha\n");
            writer.write("Titulo Repetido,\"Desc 1\",\"Robo\",-34.1, -58.1 ,01/10/2025\n");
            writer.write("Titulo Unico,\"Desc 2\",\"Hurto\", -34.2 ,-58.2,02/10/2025\n");
            writer.write("Titulo Repetido,\"Desc 3 Sobreescrita\",\"Robo\", -34.3, -58.3, 03/10/2025\n");
        }

        List<HechoCSV> hechos = importador.getHechoFromFile(csvFile.getAbsolutePath());

        assertEquals(2, hechos.size());

        HechoCSV hechoRepetido = hechos.stream()
                .filter(h -> h.getTitulo().equals("Titulo Repetido"))
                .findFirst()
                .orElse(null);

        HechoCSV hechoUnico = hechos.stream()
                .filter(h -> h.getTitulo().equals("Titulo Unico"))
                .findFirst()
                .orElse(null);

        assertNotNull(hechoRepetido);
        assertNotNull(hechoUnico);

        assertEquals("Desc 3 Sobreescrita", hechoRepetido.getDescripcion());
        assertEquals(LocalDate.of(2025, 10, 3), hechoRepetido.getFechaAcontecimiento());
        assertEquals("-34.3", hechoRepetido.getLatitud());
    }

    @Test
    void testGetHechoFromFile_HandlesEmptyFile() throws Exception {
        File csvFile = new File(tempFolder, "empty.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("Título,Descripción,Categoría,Latitud,Longitud,Fecha\n");
        }

        List<HechoCSV> hechos = importador.getHechoFromFile(csvFile.getAbsolutePath());

        assertTrue(hechos.isEmpty());
    }

    @Test
    void testUnquoteCsvField() throws Exception {
        Method unquoteMethod = ImportadorFileServerLocal.class.getDeclaredMethod("unquoteCsvField", String.class);
        unquoteMethod.setAccessible(true);

        assertEquals("simple", unquoteMethod.invoke(null, "simple"));
        assertEquals("simple", unquoteMethod.invoke(null, "\"simple\""));
        assertEquals("campo con, coma", unquoteMethod.invoke(null, "\"campo con, coma\""));
        assertEquals("campo con \"comillas\"", unquoteMethod.invoke(null, "\"campo con \"\"comillas\"\"\""));
        assertEquals("campo", unquoteMethod.invoke(null, "  \"campo\"  "));
        assertEquals("  no trim interno  ", unquoteMethod.invoke(null, "\"  no trim interno  \""));
    }
}