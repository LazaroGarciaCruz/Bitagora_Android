package com.lazarogarciacruz.bitagora.modelo;

import java.util.LinkedList;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class Dataset {

    public String title;
    public String last_update;
    public LinkedList<Juego> juegos = new LinkedList<>();
    public LinkedList<Task> tasks = new LinkedList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public LinkedList<Juego> getJuegos() {
        return juegos;
    }

    public void setJuegos(LinkedList<Juego> juegos) {
        this.juegos = juegos;
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(LinkedList<Task> tasks) {
        this.tasks = tasks;
    }

}
