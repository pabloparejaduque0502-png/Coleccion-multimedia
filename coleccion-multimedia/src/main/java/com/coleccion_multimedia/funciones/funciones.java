package com.coleccion_multimedia.funciones;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.TipoMultimedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class funciones {

    private static final String RUTA_ARCHIVO = "elementos.txt";

    // Función para guardar un elemento en el archivo de texto cuando el usuario lo cree
    public static void guardarElemento(ElementoMultimedia elemento) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            writer.write(elemento.id + "|" + 
                         elemento.titulo + "|" + 
                         elemento.tipo + "|" + 
                         elemento.genero + "|" + 
                         elemento.anio + "|" + 
                         elemento.creador + "|" + 
                         elemento.calificacion + "|" + 
                         elemento.estado + "|" + 
                         elemento.imagen);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar en el archivo: " + e.getMessage());
        }
    }

    // Función para contar las lineas del archivo de texto y conocer el tamaño del vector
    private static int contarLineas() {
        int contador = 0;
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            while (reader.readLine() != null) {
                contador++;
            }
        } catch (IOException e) {
            System.out.println("Error al contar las líneas: " + e.getMessage());
        }
        return contador;
    }

    // Función para leer los elementos del archivo de texto y crear un vector con ellos para que carguen en la pagina
    // esta funcion vuelve y crea los elementos multimedia del usuario cada vez que se inicie la paagina, de este modo se tiene la persistencia
    public static ElementoMultimedia[] leerElementos() {
        int totalLineas = contarLineas();
        ElementoMultimedia[] elementos = new ElementoMultimedia[totalLineas];

        if (totalLineas == 0) {
            return elementos;
        }

        File archivo = new File(RUTA_ARCHIVO);
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int indice = 0;
            
            while ((linea = reader.readLine()) != null) {
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

                    ElementoMultimedia elemento = new ElementoMultimedia(id, titulo, tipo, genero, anio, creador, calificacion, estado,imagen);
                    elementos[indice] = elemento;
                    indice++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return elementos;
    }
    public static void eliminarElemento(String id) {
        ElementoMultimedia[] elementos = leerElementos();

        // Sobrescribimos el archivo plano desde cero (FileWriter con false)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (int i = 0; i < elementos.length; i++) {
                ElementoMultimedia e = elementos[i];
                
                // Si el elemento es válido y su ID NO coincide con el que queremos borrar, lo volvemos a escribir
                if (e != null && !e.id.equals(id)) {
                    writer.write(e.id + "|" + 
                                 e.titulo + "|" + 
                                 e.tipo + "|" + 
                                 e.genero + "|" + 
                                 e.anio + "|" + 
                                 e.creador + "|" + 
                                 e.calificacion + "|" + 
                                 e.estado + "|" + 
                                 e.imagen);
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar el elemento: " + ex.getMessage());
        }
    }
 }
