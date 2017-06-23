package com.lazarogarciacruz.bitagora.modelo;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskComponenteTexto extends TaskComponente {

    private String texto = "";

    public TaskComponenteTexto() {
        super(new TaskComponenteTipo(TaskComponenteTipo.TEXTO));
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}
