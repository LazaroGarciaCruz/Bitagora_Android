package com.lazarogarciacruz.bitagora.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class Task {

    private String id;
    private int prioridad = TaskPrioridad.BAJA;
    private int dificultad = TaskDificultad.FACIL;
    private String idJuego = "";
    private String titulo = "";
    private String fecha;
    transient private Date creationDate;
    private LinkedList<TaskComponente> componentes = new LinkedList<>();

    public Task() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getDificultad() {
        return dificultad;
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }

    public String getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(String idJuego) {
        this.idJuego = idJuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFechaCreacion() {
        return fecha;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fecha = fechaCreacion;
        try {
            creationDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaCreacion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date getCreationDate() { return creationDate; }

    public LinkedList<TaskComponente> getListaComponentes() {
        return componentes;
    }

    public void setListaComponentes(LinkedList<TaskComponente> listaComponentes) {
        this.componentes = listaComponentes;
    }

}
