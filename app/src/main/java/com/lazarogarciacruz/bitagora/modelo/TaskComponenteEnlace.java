package com.lazarogarciacruz.bitagora.modelo;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskComponenteEnlace extends TaskComponente {

    private String enlace = "";
    private Boolean isVideo = false;

    public TaskComponenteEnlace() {
        super(new TaskComponenteTipo(TaskComponenteTipo.ENLACE));
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public Boolean getEnlaceVideo() {
        return isVideo;
    }

    public void setEnlaceVideo(Boolean enlaceVideo) {
        isVideo = enlaceVideo;
    }

}
