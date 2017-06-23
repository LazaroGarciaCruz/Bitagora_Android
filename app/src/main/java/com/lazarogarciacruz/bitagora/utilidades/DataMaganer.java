package com.lazarogarciacruz.bitagora.utilidades;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lazarogarciacruz.bitagora.modelo.Dataset;
import com.lazarogarciacruz.bitagora.modelo.Juego;
import com.lazarogarciacruz.bitagora.modelo.Task;
import com.lazarogarciacruz.bitagora.modelo.TaskComponente;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteContador;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteEnlace;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteImagen;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTexto;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTipo;
import com.lazarogarciacruz.bitagora.modelo.TaskDificultad;
import com.lazarogarciacruz.bitagora.modelo.TaskPrioridad;
import com.sromku.simple.storage.ExternalStorage;
import com.sromku.simple.storage.InternalStorage;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class DataMaganer {

    public interface OnDatosJuegosActualizados {
        void onJuegoCreado();
        void onJuegoBorrado(int position);
    }

    private static final String JSON_RAW_FILE = "bitagora_data.json";

    private static DataMaganer instance;

    private InternalStorage storage;
    private Dataset dataset;

    private OnDatosJuegosActualizados onDatosJuegosActualizadosListener;

    /**
     * Constructor por defecto del singleton
     * */
    public static DataMaganer getInstance() {
        if (instance == null) {
            instance = new DataMaganer();
            instance.verificarInstalacion();
        }
        return instance;
    }

    //-----------------------------------------------------------------
    //---------- FUNCIONES SOBRE LA CARGA DE LA APLICACION ------------
    //-----------------------------------------------------------------

    /**
     * Este metodo se usa para verificar la instalacion
     * de la aplicacion y de llevar a cabo las tareas
     * necesarias en caso de que sea la primera ejecucion
     * de la misma.
     * */
    private void verificarInstalacion() {

        //Obtenemos una referencia al directorio de almacenamiento externo,
        //donde se guardaran las imágenes y al interno, donde se almacena el
        //fichero JSON con la informacion que maneja la aplicacion.

        //externalStorage = SimpleStorage.getExternalStorage(Environment.DIRECTORY_DOCUMENTS);
        storage = SimpleStorage.getInternalStorage(MyApp.getContext());

        SharedPreferences preferences = MyApp.getContext().getSharedPreferences("BitagoraPreferences", Context.MODE_PRIVATE);
        boolean isPrimeraVez = preferences.getBoolean("isPrimeraVez", true);

        if (isPrimeraVez) {

            try {

                //Se crea el fichero JSON de la aplicacion

                InputStream is = MyApp.getContext().getAssets().open("bitagora_data.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                if (storage.createFile("", JSON_RAW_FILE, buffer)) {

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isPrimeraVez", false);
                    editor.commit();

                    //En este punto pasamos a cargar la informacion contenida en el fichero JSON
                    leerDatosJSON(storage.getFile("", JSON_RAW_FILE));

                } else {
                    Log.e("Error", "No se ha podido crear el fichero de datos en la instalacion");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            if (storage.isFileExist("", JSON_RAW_FILE)) {
                //En este punto pasamos a cargar la informacion contenida en el fichero JSON
                leerDatosJSON(storage.getFile("", JSON_RAW_FILE));
            }

        }

    }

    /**
     * Este metodo lee la informacion almacenada en el fichero JSON de
     * datos y la convierte en una estructura que represente el modelo
     * */
    private void leerDatosJSON(File jsonFile) {

        try {

            String json = IOUtils.toString(new FileInputStream(jsonFile), Charset.defaultCharset());
            JsonParser parser = new JsonParser();
            dataset = new Dataset();

            JsonElement element = parser.parse(json);
            if (element.isJsonObject()) {

                JsonObject jsonDataset = element.getAsJsonObject();

                dataset.setTitle(jsonDataset.get("title").getAsString());
                dataset.setLast_update(jsonDataset.get("last_update").getAsString());

                JsonArray jsonListaJuegos = jsonDataset.getAsJsonArray("juegos");
                LinkedList<Juego> listaJuegos =  new LinkedList<>();
                for (int i = 0; i < jsonListaJuegos.size(); i++) {
                    JsonObject jsonJuego = jsonListaJuegos.get(i).getAsJsonObject();
                    Juego juego = new Juego();
                    juego.setId(jsonJuego.get("id").getAsString());
                    juego.setTitulo(jsonJuego.get("titulo").getAsString());
                    juego.setCover(jsonJuego.get("cover").getAsString());
                    juego.setLogo(jsonJuego.get("logo").getAsString());
                    listaJuegos.add(juego);
                }

                JsonArray jsonListaTasks = jsonDataset.getAsJsonArray("tasks");
                LinkedList<Task> listaTasks =  new LinkedList<>();
                for (int i = 0; i < jsonListaTasks.size(); i++) {

                    JsonObject jsonTask = jsonListaTasks.get(i).getAsJsonObject();
                    Task task = new Task();
                    task.setId(jsonTask.get("id") == null ? "" : jsonTask.get("id").getAsString());
                    task.setTitulo(jsonTask.get("titulo").getAsString());
                    task.setFechaCreacion(jsonTask.get("fecha").getAsString());
                    task.setIdJuego(jsonTask.get("idJuego").getAsString());
                    task.setDificultad(jsonTask.get("dificultad").getAsInt());
                    task.setPrioridad(jsonTask.get("prioridad").getAsInt());

                    JsonArray jsonListaComponentesTask = jsonTask.getAsJsonArray("componentes");
                    for (int j = 0; j < jsonListaComponentesTask.size(); j++) {

                        JsonObject jsonComponeteTask = jsonListaComponentesTask.get(j).getAsJsonObject();
                        switch (jsonComponeteTask.get("tipo").getAsInt()) {

                            case TaskComponenteTipo.TEXTO:
                                TaskComponenteTexto taskComponenteTexto = new TaskComponenteTexto();
                                taskComponenteTexto.setTexto(jsonComponeteTask.get("texto").getAsString());
                                task.getListaComponentes().add(taskComponenteTexto);
                                break;
                            case TaskComponenteTipo.IMAGEN:
                                JsonArray jsonListaImagenes = jsonComponeteTask.getAsJsonArray("imagenes");
                                LinkedList<String> listaImagenes = new LinkedList<>();
                                for (int k = 0; k < jsonListaImagenes.size(); k++) {
                                    listaImagenes.add(jsonListaImagenes.get(k).getAsString());
                                }
                                TaskComponenteImagen taskComponenteImagen = new TaskComponenteImagen();
                                taskComponenteImagen.setListaNombresImagenes(listaImagenes);
                                task.getListaComponentes().add(taskComponenteImagen);
                                break;
                            case TaskComponenteTipo.ENLACE:
                                TaskComponenteEnlace taskComponenteEnlace = new TaskComponenteEnlace();
                                taskComponenteEnlace.setEnlace(jsonComponeteTask.get("enlace").getAsString());
                                taskComponenteEnlace.setEnlaceVideo(jsonComponeteTask.get("isVideo").getAsBoolean());
                                task.getListaComponentes().add(taskComponenteEnlace);
                                break;
                            case TaskComponenteTipo.CONTADOR:
                                TaskComponenteContador taskComponenteContador = new TaskComponenteContador();
                                taskComponenteContador.setId(jsonComponeteTask.get("id").getAsString());
                                taskComponenteContador.setDescripcion(jsonComponeteTask.get("descripcion").getAsString());
                                taskComponenteContador.setCantidadElementosMaxima(jsonComponeteTask.get("cantidadElementosMaxima").getAsInt());
                                taskComponenteContador.setCantidadElementosActuales(jsonComponeteTask.get("cantidadElementosActuales").getAsInt());
                                task.getListaComponentes().add(taskComponenteContador);
                                break;

                        }

                    }

                    listaTasks.add(task);

                }

                dataset.setJuegos(listaJuegos);
                dataset.setTasks(listaTasks);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //-----------------------------------------------------------------
    //------------------ FUNCIONES SOBRE LOS JUEGOS -------------------
    //-----------------------------------------------------------------

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * almacenar los datos de un nuevo juego
     * */
    public void almacenarNuevoJuego(String titulo, Bitmap cover, Bitmap logo, boolean isCoverGif, byte[] coverGidData) {

        Juego juego = new Juego();
        juego.setTitulo(titulo);

        if (ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            String id = randomString();

            if (storage.createDirectory(id)) {

                juego.setId(id);

                juego.setCover("");
                if (!isCoverGif) {
                    if (cover != null) {
                        String coverId = "cover.png";
                        if (storage.createFile(id, coverId, cover)) {
                            juego.setCover(coverId);
                        }
                    }
                } else {
                    if (coverGidData != null) {
                        String coverId = "cover.gif";
                        if (storage.createFile(id, coverId, coverGidData)) {
                            juego.setCover(coverId);
                        }
                    }
                }

                juego.setLogo("");
                if (logo != null) {
                    String logoId = "logo.png";
                    if (storage.createFile(id, logoId , logo)) {
                        juego.setLogo(logoId);
                    }
                }

                //En este punto el juego esta creado y pasamos a actualizar el
                //fichero json en disco con la lista de los datos de la aplicacion

                dataset.getJuegos().add(juego);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                dataset.setLast_update(dateFormat.format(new Date()));

                Gson gson = new GsonBuilder()
                        .setExclusionStrategies(new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes f) {
                                return  f.getDeclaringClass().equals(Bitmap.class);
                            }
                            @Override
                            public boolean shouldSkipClass(Class<?> clazz) {
                                return false;
                            }
                        })
                        .setPrettyPrinting()
                        .create();

                System.out.println(gson.toJson(dataset));

                if (storage.deleteFile("", JSON_RAW_FILE)) {
                    if (storage.createFile("", JSON_RAW_FILE, gson.toJson(dataset).getBytes())) {
                        leerDatosJSON(storage.getFile("", JSON_RAW_FILE));
                        onDatosJuegosActualizadosListener.onJuegoCreado();
                    }
                }

            }

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * borrar los datos de un juego ya existente
     * */
    public void borrarJuego(int posicion) {

        if (posicion >= 0 && posicion <= dataset.getJuegos().size()) {

            String rutaJuego = dataset.getJuegos().get(posicion).getId();

            if (ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                if (storage.isDirectoryExists(rutaJuego)) {

                    if (storage.deleteDirectory(rutaJuego)) {

                        LinkedList<Task> listaTaskActualizada = new LinkedList<>();
                        for (Task task : dataset.getTasks()) {
                            if (!task.getIdJuego().equals(dataset.getJuegos().get(posicion).getId())) {
                                listaTaskActualizada.add(task);
                            }
                        }
                        dataset.setTasks(listaTaskActualizada);

                        dataset.getJuegos().remove(posicion);

                        //En este punto el juego esta borrado y pasamos a actualizar el
                        //fichero json en disco con la lista de los datos de la aplicacion

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        dataset.setLast_update(dateFormat.format(new Date()));

                        Gson gson = new GsonBuilder()
                                .setExclusionStrategies(new ExclusionStrategy() {
                                    @Override
                                    public boolean shouldSkipField(FieldAttributes f) {
                                        return f.getDeclaringClass().equals(Bitmap.class);
                                    }

                                    @Override
                                    public boolean shouldSkipClass(Class<?> clazz) {
                                        return false;
                                    }
                                })
                                .setPrettyPrinting()
                                .create();

                        System.out.println(gson.toJson(dataset));

                        if (storage.deleteFile("", JSON_RAW_FILE)) {
                            if (storage.createFile("", JSON_RAW_FILE, gson.toJson(dataset).getBytes())) {
                                leerDatosJSON(storage.getFile("", JSON_RAW_FILE));
                                onDatosJuegosActualizadosListener.onJuegoBorrado(posicion);
                            }
                        }

                    }

                }

            }

        }

    }

    //-----------------------------------------------------------------
    //-------------------- FUNCIONES SOBRE LOS TASK -------------------
    //-----------------------------------------------------------------

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * almacenar los datos de un nuevo task
     * */
    public void almacenarNuevoTask(String titulo, TaskDificultad dificultad, TaskPrioridad prioridad, LinkedList<TaskComponente> listaComponentes, int numJuego) {

        Task task = new Task();
        Juego juegoSeleccionado = dataset.getJuegos().get(numJuego);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        if (ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            task.setTitulo(titulo);
            task.setId(randomString());
            task.setDificultad(dificultad.getDificultad());
            task.setPrioridad(prioridad.getPrioridad());
            task.setIdJuego(juegoSeleccionado.getId());
            task.setListaComponentes(listaComponentes);
            task.setFechaCreacion(dateFormat.format(new Date()));

            for (TaskComponente componente : listaComponentes) {
                if (componente.getTipoComponete().getTipoComponente() == TaskComponenteTipo.IMAGEN) {
                    for (int i = 0; i < ((TaskComponenteImagen)componente).getListaNombresImagenes().size(); i++) {
                        String imageName = randomString() + ".png";
                        if (storage.createFile(juegoSeleccionado.getId(), imageName, getBytesFromFile(((TaskComponenteImagen)componente).getListaNombresImagenes().get(i)))) {
                            ((TaskComponenteImagen)componente).getListaNombresImagenes().set(i, imageName);
                        }
                    }
                } else if (componente.getTipoComponete().getTipoComponente() == TaskComponenteTipo.CONTADOR) {
                    ((TaskComponenteContador)componente).setId(randomString());
                }
            }

            //En este punto el juego esta creado y pasamos a actualizar el
            //fichero json en disco con la lista de los datos de la aplicacion

            dataset.getTasks().add(task);
            dataset.setLast_update(dateFormat.format(new Date()));

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            System.out.println(gson.toJson(dataset));

            if (storage.deleteFile("", JSON_RAW_FILE)) {
                if (storage.createFile("", JSON_RAW_FILE, gson.toJson(dataset).getBytes())) {
                    leerDatosJSON(storage.getFile("", JSON_RAW_FILE));
                    //onDatosJuegosActualizadosListener.onJuegoCreado();
                }
            }

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * borrar los datos de un task ya existente
     * */
    public void borrarTask(int posicion) {

        if (posicion >= 0 && posicion <= dataset.getJuegos().size()) {

            String rutaJuego = dataset.getTasks().get(posicion).getIdJuego();

            if (ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                if (storage.isDirectoryExists(rutaJuego)) {

                    //Si el task tiene componentes de imagenes debemos borrar
                    //dichas imagenes del directorio del juego
                    for (TaskComponente componente : dataset.getTasks().get(posicion).getListaComponentes()) {
                        if (componente instanceof TaskComponenteImagen) {
                            for (String nombreImagen : ((TaskComponenteImagen)componente).getListaNombresImagenes()) {
                                storage.deleteFile(rutaJuego, nombreImagen);
                            }
                        }
                    }

                    dataset.getTasks().remove(posicion);

                    //En este punto el juego esta borrado y pasamos a actualizar el
                    //fichero json en disco con la lista de los datos de la aplicacion

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    dataset.setLast_update(dateFormat.format(new Date()));

                    Gson gson = new GsonBuilder()
                            .setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(Bitmap.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            })
                            .setPrettyPrinting()
                            .create();

                    System.out.println(gson.toJson(dataset));

                    if (storage.deleteFile("", JSON_RAW_FILE)) {
                        if (storage.createFile("", JSON_RAW_FILE, gson.toJson(dataset).getBytes())) {
                            leerDatosJSON(storage.getFile("", JSON_RAW_FILE));
                        }
                    }

                }

            }

        }

    }

    /**
     * En este metodo se llevan a cabo las tareas necesarias para
     * actualizar los datos de las tasks ya existentes
     * */
    public void actualizarTasks() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dataset.setLast_update(dateFormat.format(new Date()));

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(Bitmap.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .setPrettyPrinting()
                .create();

        System.out.println(gson.toJson(dataset));

        if (storage.deleteFile("", JSON_RAW_FILE)) {
            if (storage.createFile("", JSON_RAW_FILE, gson.toJson(dataset).getBytes())) {
                leerDatosJSON(storage.getFile("", JSON_RAW_FILE));
            }
        }

    }

    //-----------------------------------------------------------------
    //-------------------- FUNCIONES GENERALES ------------------------
    //-----------------------------------------------------------------

    /**
     * Este metodo carga una imagen desde el almacenamiento externo
     * y la devuelve en un formato para ser usado en la interfaz
     * */
    private Bitmap getImagenAlmacenamientoExterno(String directorio, String nombreImagen) {

        if (storage != null) {

            File imagen = storage.getFile(directorio, nombreImagen);
            if (imagen != null) {
                return BitmapFactory.decodeFile(imagen.getAbsolutePath());
            }

        }

        return null;

    }

    /**
     * Este metodo genera una cadena alfanumerica aleatoria
     * */
    public static String randomString() {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    /**
     * Este metodo devuelve los bytes que componen
     * el fichero de un path en particular
     * */
    private byte[] getBytesFromFile(String path) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream (path));
            int read;
            byte[] buffer = new byte[32000];
            while ((read = reader.read(buffer)) >= 0) {
                baos.write(buffer, 0, read);
            }
        } catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        return baos.toByteArray();

    }

    /**
     * Devuelve el dataset de la aplicacion con la informacion de la misma
     * */
    public Dataset getDataset() {
        return dataset;
    }

    public Storage getStorage() { return storage; }

    /**
     * Establece el listener de los datos de juegos actualizados
     * */
    public void setOnDatosJuegosActualizadosListener(OnDatosJuegosActualizados listener) {
        this.onDatosJuegosActualizadosListener = listener;
    }

}
