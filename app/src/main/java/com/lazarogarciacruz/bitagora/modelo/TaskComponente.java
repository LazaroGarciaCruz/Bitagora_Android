package com.lazarogarciacruz.bitagora.modelo;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskComponente {

    private transient TaskComponenteTipo tipoComponete; //Esta variable esta declarada como transient para que el GSON la ignore al crear el JSON
    private int tipo;

    public TaskComponente(TaskComponenteTipo tipoComponete) {
        this.tipoComponete = tipoComponete;
        this.tipo = tipoComponete.getTipoComponente();
    }

    public TaskComponenteTipo getTipoComponete() {
        return tipoComponete;
    }

}
