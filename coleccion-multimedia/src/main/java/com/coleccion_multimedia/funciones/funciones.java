package com.coleccion_multimedia.funciones;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.clases.ListaPersonalizada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class funciones {

    private static final String RUTA_ELEMENTOS = "elementos.txt";
    private static final String RUTA_PRESTAMOS = "prestamos.txt";
    private static final String RUTA_LISTAS = "listas.txt";

    private static int contarLineas(String ruta) {
        int contador = 0;
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            while (reader.readLine() != null) {
                contador++;
            }
        } catch (IOException e) {
            System.out.println("Error al contar las líneas en " + ruta + ": " + e.getMessage());
        }
        return contador;
    }

    public static void guardarElemento(ElementoMultimedia elemento) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, true))) {
            writer.println(elemento.id + "|" + 
                           elemento.titulo + "|" + 
                           elemento.tipo + "|" + 
                           elemento.genero + "|" + 
                           elemento.anio + "|" + 
                           elemento.creador + "|" + 
                           elemento.calificacion + "|" + 
                           elemento.estado + "|" + 
                           elemento.imagen);
        } catch (IOException e) {
            System.out.println("Error al guardar el elemento: " + e.getMessage());
        }
    }

    public static ElementoMultimedia[] leerElementos() {
        int totalLineas = contarLineas(RUTA_ELEMENTOS);
        if (totalLineas == 0) {
            return new ElementoMultimedia[0];
        }

        ElementoMultimedia[] elementosTemp = new ElementoMultimedia[totalLineas];
        int indice = 0;

        File archivo = new File(RUTA_ELEMENTOS);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");

                if (partes.length >= 9) {
                    try {
                        String id = partes[0].trim();
                        String titulo = partes[1].trim();
                        
                        TipoMultimedia tipo = TipoMultimedia.valueOf(partes[2].trim());
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        String creador = partes[5].trim();
                        int calificacion = Integer.parseInt(partes[6].trim());
                        EstadoMultimedia estado = EstadoMultimedia.valueOf(partes[7].toUpperCase().trim());
                        String imagen = partes[8].trim();

                        elementosTemp[indice] = new ElementoMultimedia(id, titulo, tipo, genero, anio, creador, calificacion, estado, imagen);
                        indice++;
                    } catch (Exception ex) {
                        System.out.println("Error parseando linea en elementos.txt: " + linea + " -> " + ex.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de elementos: " + e.getMessage());
        }

        ElementoMultimedia[] resultado = new ElementoMultimedia[indice];
        for (int i = 0; i < indice; i++) {
            resultado[i] = elementosTemp[i];
        }
        return resultado;
    }

    public static void eliminarElemento(String id) {
        ElementoMultimedia[] elementos = leerElementos();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, false))) {
            for (int i = 0; i < elementos.length; i++) {
                ElementoMultimedia e = elementos[i];
                
                if (e != null && !e.id.equals(id)) {
                    writer.println(e.id + "|" + 
                                   e.titulo + "|" + 
                                   e.tipo + "|" + 
                                   e.genero + "|" + 
                                   e.anio + "|" + 
                                   e.creador + "|" + 
                                   e.calificacion + "|" + 
                                   e.estado + "|" + 
                                   e.imagen);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar el elemento: " + ex.getMessage());
        }

        Prestamo[] prestamos = leerPrestamos();
        for (int i = 0; i < prestamos.length; i++) {
            if (prestamos[i] != null && prestamos[i].idElemento.equals(id)) {
                eliminarPrestamo(prestamos[i].id);
            }
        }
    }

    public static ElementoMultimedia buscarElementoPorId(String id) {
        ElementoMultimedia[] elementos = leerElementos();
        for (int i = 0; i < elementos.length; i++) {
            if (elementos[i] != null && elementos[i].id.equals(id)) {
                return elementos[i];
            }
        }
        return null;
    }

    public static void guardarPrestamo(Prestamo prestamo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_PRESTAMOS, true))) {
            String fechaDev = (prestamo.fechaDevolucion == null || prestamo.fechaDevolucion.trim().isEmpty()) 
                              ? "Sin fecha" 
                              : prestamo.fechaDevolucion;

            writer.println(prestamo.id + "|" + 
                           prestamo.idElemento + "|" + 
                           prestamo.persona + "|" + 
                           prestamo.fechaPrestamo + "|" + 
                           fechaDev);
        } catch (IOException e) {
            System.out.println("Error al guardar el préstamo: " + e.getMessage());
        }
    }

    public static Prestamo[] leerPrestamos() {
        int totalLineas = contarLineas(RUTA_PRESTAMOS);
        if (totalLineas == 0) {
            return new Prestamo[0];
        }

        Prestamo[] prestamosTemp = new Prestamo[totalLineas];
        int indice = 0;

        File archivo = new File(RUTA_PRESTAMOS);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");

                if (partes.length >= 4) {
                    try {
                        String id = partes[0].trim();
                        String idElemento = partes[1].trim();
                        String persona = partes[2].trim();
                        String fechaPrestamo = partes[3].trim();
                        String fechaDevolucion = (partes.length > 4) ? partes[4].trim() : "Sin fecha";

                        prestamosTemp[indice] = new Prestamo(id, idElemento, persona, fechaPrestamo, fechaDevolucion);
                        indice++;
                    } catch (Exception ex) {
                        System.out.println("Error parseando linea en prestamos.txt: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de préstamos: " + e.getMessage());
        }

        Prestamo[] resultado = new Prestamo[indice];
        for (int i = 0; i < indice; i++) {
            resultado[i] = prestamosTemp[i];
        }
        return resultado;
    }

    public static void eliminarPrestamo(String id) {
        Prestamo[] prestamos = leerPrestamos();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_PRESTAMOS, false))) {
            for (int i = 0; i < prestamos.length; i++) {
                Prestamo p = prestamos[i];

                if (p != null && !p.id.equals(id)) {
                    writer.println(p.id + "|" + 
                                   p.idElemento + "|" + 
                                   p.persona + "|" + 
                                   p.fechaPrestamo + "|" + 
                                   p.fechaDevolucion);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar el préstamo: " + ex.getMessage());
        }
    }

    public static void guardarLista(ListaPersonalizada lista) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, true))) {
            String idsJoin = "";
            if (lista.idsElementos != null && lista.idsElementos.length > 0) {
                idsJoin = String.join(",", lista.idsElementos);
            }

            writer.println(lista.id + "|" + 
                           lista.nombre + "|" + 
                           lista.descripcion + "|" + 
                           lista.imagen + "|" + 
                           idsJoin);
        } catch (IOException e) {
            System.out.println("Error al guardar la lista personalizada: " + e.getMessage());
        }
    }

    public static ListaPersonalizada[] leerListas() {
        int totalLineas = contarLineas(RUTA_LISTAS);
        if (totalLineas == 0) {
            return new ListaPersonalizada[0];
        }

        ListaPersonalizada[] listasTemp = new ListaPersonalizada[totalLineas];
        int indice = 0;

        File archivo = new File(RUTA_LISTAS);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");

                if (partes.length >= 4) {
                    try {
                        String id = partes[0].trim();
                        String nombre = partes[1].trim();
                        String descripcion = partes[2].trim();
                        String imagen = partes[3].trim();

                        String[] idsElementos = new String[0];
                        if (partes.length > 4 && !partes[4].trim().isEmpty()) {
                            idsElementos = partes[4].split(",");
                        }

                        listasTemp[indice] = new ListaPersonalizada(id, nombre, descripcion, imagen, idsElementos);
                        indice++;
                    } catch (Exception ex) {
                        System.out.println("Error parseando linea en listas.txt: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de listas: " + e.getMessage());
        }

        ListaPersonalizada[] resultado = new ListaPersonalizada[indice];
        for (int i = 0; i < indice; i++) {
            resultado[i] = listasTemp[i];
        }
        return resultado;
    }

    public static void eliminarLista(String id) {
        ListaPersonalizada[] listas = leerListas();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, false))) {
            for (int i = 0; i < listas.length; i++) {
                ListaPersonalizada l = listas[i];

                if (l != null && !l.id.equals(id)) {
                    String idsJoin = (l.idsElementos != null && l.idsElementos.length > 0) 
                                     ? String.join(",", l.idsElementos) 
                                     : "";

                    writer.println(l.id + "|" + 
                                   l.nombre + "|" + 
                                   l.descripcion + "|" + 
                                   l.imagen + "|" + 
                                   idsJoin);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar la lista personalizada: " + ex.getMessage());
        }
    }

    public static void actualizarLista(ListaPersonalizada listaActualizada) {
        ListaPersonalizada[] listas = leerListas();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, false))) {
            for (int i = 0; i < listas.length; i++) {
                ListaPersonalizada l = listas[i];

                if (l != null) {
                    if (l.id.equals(listaActualizada.id)) {
                        l = listaActualizada;
                    }

                    String idsJoin = (l.idsElementos != null && l.idsElementos.length > 0) 
                                     ? String.join(",", l.idsElementos) 
                                     : "";

                    writer.println(l.id + "|" + 
                                   l.nombre + "|" + 
                                   l.descripcion + "|" + 
                                   l.imagen + "|" + 
                                   idsJoin);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al actualizar la lista: " + ex.getMessage());
        }
    }
}