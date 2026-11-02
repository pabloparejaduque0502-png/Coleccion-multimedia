package com.coleccion_multimedia.clases;

public class ListaPersonalizada {
    public String id;
    public String nombre;
    public String descripcion;
    public String imagen;
    public String[] idsElementos;

    public ListaPersonalizada(String id, String nombre, String descripcion, String imagen, String[] idsElementos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = (descripcion == null || descripcion.trim().isEmpty()) ? "Sin descripción" : descripcion;
        this.imagen = (imagen == null || imagen.trim().isEmpty()) ? "default_lista.png" : imagen;
        this.idsElementos = (idsElementos == null) ? new String[0] : idsElementos;
    }

}
