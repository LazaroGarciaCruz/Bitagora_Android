package com.lazarogarciacruz.bitagora.modelo;

import android.graphics.Bitmap;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class Juego {

    private String id;
    private String titulo;
    private String cover;
    //private Bitmap coverImage;
    private String logo;
    //private Bitmap logoImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    /*public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public Bitmap getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Bitmap logoImage) {
        this.logoImage = logoImage;
    }*/

}
