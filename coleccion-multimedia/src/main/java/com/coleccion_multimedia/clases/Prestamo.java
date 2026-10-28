package com.coleccion_multimedia.clases;

public class Prestamo {
    public String id;
    public String idElemento;
    public String persona;
    public String fechaPrestamo;
    public String fechaDevolucion;

    public Prestamo(String id, String idElemento, String persona, String fechaPrestamo, String fechaDevolucion) {
        this.id = id;
        this.idElemento = idElemento;
        this.persona = persona;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = (fechaDevolucion == null || fechaDevolucion.trim().isEmpty()) ? "Sin fecha" : fechaDevolucion;
    }
}
