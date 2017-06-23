package com.lazarogarciacruz.bitagora.modelo;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lazarogarciacruz on 14/6/17.
 */

public class TaskComponenteTipo {

    public static final int TEXTO = 0;
    public static final int IMAGEN = 1;
    public static final int ENLACE = 2;
    public static final int CONTADOR = 3;

    private int tipoComponente;

    public TaskComponenteTipo(@TipoComponente int tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public int getTipoComponente() {
        return tipoComponente;
    }

    @IntDef({TEXTO, IMAGEN, ENLACE, CONTADOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TipoComponente {
    }

}
