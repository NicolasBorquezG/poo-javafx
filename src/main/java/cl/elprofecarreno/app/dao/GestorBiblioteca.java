package cl.elprofecarreno.app.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cl.elprofecarreno.app.dao.entity.AudioLibro;
import cl.elprofecarreno.app.dao.entity.Libro;
import cl.elprofecarreno.app.dao.entity.Material;
import cl.elprofecarreno.app.dao.excepcion.DAOException;

public class GestorBiblioteca {

    private static final String RECURSO_INICIAL = "/materiales.json";
    private static final Path ARCHIVO = Path.of("materiales.json");
    private static final String CAMPO_MATERIALES = "materiales";
    private static final String CAMPO_TIPO = "tipo";
    private static final String TIPO_LIBRO = "LIBRO";
    private static final String TIPO_AUDIOLIBRO = "AUDIOLIBRO";

    private Gson gson;
    private List<Material> materiales;

    public GestorBiblioteca() {
        try {
            gson = new GsonBuilder().setPrettyPrinting().create();
            materiales = new ArrayList<>();
            cargar();
        } catch (DAOException e) {
            throw new IllegalStateException("No se pudo inicializar GestorBiblioteca: " + e.getMessage(), e);
        }
    }

    public List<Material> findAll() {
        return new ArrayList<>(materiales);
    }

    public Material findByTitulo(String titulo) {
        for (Material m : materiales) {
            if (m.getTitulo().equalsIgnoreCase(titulo)) {
                return m;
            }
        }
        return null;
    }

    public void save(Material material) throws DAOException {
        if (material == null) {
            throw new DAOException("No se puede guardar un material nulo.");
        }
        if (findByTitulo(material.getTitulo()) != null) {
            throw new DAOException("Ya existe un material con el titulo: " + material.getTitulo());
        }
        materiales.add(material);
        try {
            guardarArchivo();
        } catch (DAOException e) {
            materiales.remove(material);
            throw e;
        }
    }

    public void delete(String titulo) throws DAOException {
        Material material = findByTitulo(titulo);
        if (material == null) {
            throw new DAOException("No existe un material con el titulo: " + titulo);
        }
        materiales.remove(material);
        try {
            guardarArchivo();
        } catch (DAOException e) {
            materiales.add(material);
            throw e;
        }
    }

    private void cargar() throws DAOException {
        try {
            String json;
            if (Files.exists(ARCHIVO)) {
                json = Files.readString(ARCHIVO, StandardCharsets.UTF_8);
            } else {
                try (InputStream in = getClass().getResourceAsStream(RECURSO_INICIAL)) {
                    if (in == null) {
                        return;
                    }
                    json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            JsonObject raiz = gson.fromJson(json, JsonObject.class);
            if (raiz == null || !raiz.has(CAMPO_MATERIALES)) {
                return;
            }
            for (JsonElement elemento : raiz.getAsJsonArray(CAMPO_MATERIALES)) {
                JsonObject obj = elemento.getAsJsonObject();
                String tipo = obj.has(CAMPO_TIPO) ? obj.get(CAMPO_TIPO).getAsString() : TIPO_LIBRO;
                materiales.add(TIPO_AUDIOLIBRO.equalsIgnoreCase(tipo)
                        ? gson.fromJson(obj, AudioLibro.class)
                        : gson.fromJson(obj, Libro.class));
            }
        } catch (IOException | RuntimeException e) {
            throw new DAOException("No se pudo leer materiales.json", e);
        }
    }

    private void guardarArchivo() throws DAOException {
        JsonArray arreglo = new JsonArray();
        for (Material material : materiales) {
            JsonObject obj = gson.toJsonTree(material).getAsJsonObject();
            obj.addProperty(CAMPO_TIPO, material instanceof AudioLibro ? TIPO_AUDIOLIBRO : TIPO_LIBRO);
            arreglo.add(obj);
        }
        JsonObject raiz = new JsonObject();
        raiz.add(CAMPO_MATERIALES, arreglo);
        try {
            Files.writeString(ARCHIVO, gson.toJson(raiz), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DAOException("No se pudo guardar materiales.json", e);
        }
    }
}