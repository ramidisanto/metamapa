package Servicio;


import Modelos.Entidades.HechoCSV;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;


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



}