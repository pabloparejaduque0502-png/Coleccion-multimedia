package com.coleccion_multimedia.clases;

public class Prestamo {
    public String idPrestamo;
    public String idElemento;
    public String personaDestino;
    public String fechaPrestamo;
    public boolean devuelto;

    
    public Prestamo(String idPrestamo, String idElemento, String personaDestino, String fechaPrestamo, boolean devuelto) {
        this.idPrestamo = idPrestamo;
        this.idElemento = idElemento;
        this.personaDestino = personaDestino;
        this.fechaPrestamo = fechaPrestamo;
        this.devuelto = devuelto; 
    }
}
