package com.coleccion_multimedia.clases;

public class ElementoMultimedia {
    public String id;
    public String titulo;
    public TipoMultimedia tipo;
    public String genero;
    public int anio;          
    public String creador;    
    public int calificacion;
    public String estado;
    public String imagen;

    public ElementoMultimedia(String id, String titulo, TipoMultimedia tipo, String genero, int anio, String creador, int calificacion, String estado,String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.genero = genero;
        this.anio = anio;
        this.creador = creador;
        this.calificacion = calificacion;
        this.estado = estado;
        this.imagen = imagen;
    }
}
