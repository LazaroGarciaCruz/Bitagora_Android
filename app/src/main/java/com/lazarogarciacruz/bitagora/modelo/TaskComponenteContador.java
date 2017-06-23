package com.lazarogarciacruz.bitagora.modelo;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskComponenteContador extends TaskComponente {

    private String id = "";
    private String descripcion = "";
    private int cantidadElementosMaxima = 0;
    private int cantidadElementosActuales = 0;

    public TaskComponenteContador() {
        super(new TaskComponenteTipo(TaskComponenteTipo.CONTADOR));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadElementosMaxima() {
        return cantidadElementosMaxima;
    }

    public void setCantidadElementosMaxima(int cantidadElementosMaxima) {
        this.cantidadElementosMaxima = cantidadElementosMaxima;
    }

    public int getCantidadElementosActuales() {
        return cantidadElementosActuales;
    }

    public void setCantidadElementosActuales(int cantidadElementosActuales) {
        this.cantidadElementosActuales = cantidadElementosActuales;
    }

}
