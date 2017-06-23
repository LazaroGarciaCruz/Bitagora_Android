package com.lazarogarciacruz.bitagora.modelo;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskPrioridad {

    public static final int BAJA = 0;
    public static final int MEDIA = 1;
    public static final int ALTA = 2;

    private int prioridad;

    public TaskPrioridad(@Prioridad int prioridad) {
        this.prioridad = prioridad;
    }

    public int getPrioridad() {
        return prioridad;
    }

    @IntDef({BAJA, MEDIA, ALTA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Prioridad {
    }

}
