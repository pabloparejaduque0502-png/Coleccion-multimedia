package com.coleccion_multimedia.funciones;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.clases.TipoMultimedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class funciones {

    private static final String RUTA_ELEMENTOS = "elementos.txt";
    private static final String RUTA_PRESTAMOS = "prestamos.txt";

    // Método auxiliar genérico para contar líneas de cualquier archivo plano
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

                if (partes.length == 9) {
                    String id = partes[0];
                    String titulo = partes[1];
                    TipoMultimedia tipo = TipoMultimedia.valueOf(partes[2]);
                    String genero = partes[3];
                    int anio = Integer.parseInt(partes[4]);
                    String creador = partes[5];
                    int calificacion = Integer.parseInt(partes[6]);
                    EstadoMultimedia estado = EstadoMultimedia.valueOf(partes[7]);
                    String imagen = partes[8];

                    elementosTemp[indice] = new ElementoMultimedia(id, titulo, tipo, genero, anio, creador, calificacion, estado, imagen);
                    indice++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de elementos: " + e.getMessage());
        }

        // Ajuste exacto del tamaño del arreglo tradicional
        if (indice < totalLineas) {
            ElementoMultimedia[] resultado = new ElementoMultimedia[indice];
            for (int i = 0; i < indice; i++) {
                resultado[i] = elementosTemp[i];
            }
            return resultado;
        }

        return elementosTemp;
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
                    String id = partes[0];
                    String idElemento = partes[1];
                    String persona = partes[2];
                    String fechaPrestamo = partes[3];
                    String fechaDevolucion = (partes.length > 4) ? partes[4] : "Sin fecha";

                    prestamosTemp[indice] = new Prestamo(id, idElemento, persona, fechaPrestamo, fechaDevolucion);
                    indice++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de préstamos: " + e.getMessage());
        }

        // Ajuste exacto del tamaño del arreglo
        if (indice < totalLineas) {
            Prestamo[] resultado = new Prestamo[indice];
            for (int i = 0; i < indice; i++) {
                resultado[i] = prestamosTemp[i];
            }
            return resultado;
        }

        return prestamosTemp;
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
}