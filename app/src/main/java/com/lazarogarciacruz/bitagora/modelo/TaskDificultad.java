package com.lazarogarciacruz.bitagora.modelo;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskDificultad {

    public static final int FACIL = 0;
    public static final int NORMAL = 1;
    public static final int DIFICIL = 2;

    private int dificultad;

    public TaskDificultad(@Dificultad int dificultad) {
        this.dificultad = dificultad;
    }

    public int getDificultad() {
        return dificultad;
    }

    @IntDef({FACIL, NORMAL, DIFICIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Dificultad {
    }

}
