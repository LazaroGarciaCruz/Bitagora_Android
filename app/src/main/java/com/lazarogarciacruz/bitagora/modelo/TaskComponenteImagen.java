package com.lazarogarciacruz.bitagora.modelo;

import com.lazarogarciacruz.bitagora.modelo.TaskComponente;
import com.lazarogarciacruz.bitagora.modelo.TaskComponenteTipo;

import java.util.LinkedList;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskComponenteImagen extends TaskComponente {

    private LinkedList<String> imagenes = new LinkedList<>();

    public TaskComponenteImagen() {
        super(new TaskComponenteTipo(TaskComponenteTipo.IMAGEN));
    }

    public LinkedList<String> getListaNombresImagenes() {
        return imagenes;
    }

    public void setListaNombresImagenes(LinkedList<String> listaNombresImagenes) {
        this.imagenes = listaNombresImagenes;
    }

}
