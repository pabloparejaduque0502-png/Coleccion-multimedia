package com.coleccion_multimedia.funciones;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.clases.ListaPersonalizada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class funciones {

    private static final String RUTA_ELEMENTOS = "elementos.txt";
    private static final String RUTA_PRESTAMOS = "prestamos.txt";
    private static final String RUTA_LISTAS = "listas.txt";
    private static final String CARPETA_PORTADAS = "portadas";

    public static final String PORTADA_ELEMENTO = "default_elemento.png";
    public static final String PORTADA_LISTA = "default_lista.png";

    // ---------- Utilidades generales ----------

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

    // Guarda la imagen subida en la carpeta portadas con un nombre único.
    // Devuelve el nombre con el que quedó guardada, o null si hubo un error.
    public static String guardarPortada(byte[] datos, String nombreOriginal) {
        File carpeta = new File(CARPETA_PORTADAS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreFinal = System.currentTimeMillis() + "_" + nombreOriginal;

        try (FileOutputStream output = new FileOutputStream(new File(carpeta, nombreFinal))) {
            output.write(datos);
            return nombreFinal;
        } catch (IOException e) {
            System.out.println("Error al guardar la portada: " + e.getMessage());
            return null;
        }
    }

    // Borra una portada de la carpeta, excepto las imágenes por defecto.
    public static void borrarPortada(String nombre) {
        if (nombre.equals(PORTADA_ELEMENTO) || nombre.equals(PORTADA_LISTA)) {
            return;
        }
        File foto = new File(CARPETA_PORTADAS, nombre);
        if (foto.exists()) {
            foto.delete();
        }
    }

    // Devuelve el archivo de la portada, o el de por defecto si no existe.
    public static File archivoPortada(String nombre, String porDefecto) {
        File archivo = new File(CARPETA_PORTADAS, nombre);
        if (!archivo.exists()) {
            archivo = new File(CARPETA_PORTADAS, porDefecto);
        }
        return archivo;
    }

    // ---------- Elementos multimedia ----------

    private static String lineaElemento(ElementoMultimedia e) {
        return e.id + "|" + e.titulo + "|" + e.tipo + "|" + e.genero + "|" + e.anio + "|" +
               e.creador + "|" + e.calificacion + "|" + e.estado + "|" + e.imagen;
    }

    public static void guardarElemento(ElementoMultimedia elemento) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, true))) {
            writer.println(lineaElemento(elemento));
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

        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_ELEMENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

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
                        EstadoMultimedia estado = EstadoMultimedia.valueOf(partes[7].trim());
                        String imagen = partes[8].trim();

                        elementosTemp[indice] = new ElementoMultimedia(id, titulo, tipo, genero, anio, creador, calificacion, estado, imagen);
                        indice++;
                    } catch (Exception ex) {
                        System.out.println("Error parseando linea en elementos.txt: " + linea);
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

    public static void actualizarElemento(ElementoMultimedia actualizado) {
        ElementoMultimedia[] elementos = leerElementos();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, false))) {
            for (int i = 0; i < elementos.length; i++) {
                ElementoMultimedia e = elementos[i];

                if (e.id.equals(actualizado.id)) {
                    e = actualizado;
                }
                writer.println(lineaElemento(e));
            }
        } catch (IOException ex) {
            System.out.println("Error al actualizar el elemento: " + ex.getMessage());
        }
    }

    public static void eliminarElemento(String id) {
        ElementoMultimedia[] elementos = leerElementos();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, false))) {
            for (int i = 0; i < elementos.length; i++) {
                if (elementos[i].id.equals(id)) {
                    borrarPortada(elementos[i].imagen);
                } else {
                    writer.println(lineaElemento(elementos[i]));
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar el elemento: " + ex.getMessage());
        }

        // Si el elemento estaba prestado, se borra su préstamo
        Prestamo[] prestamos = leerPrestamos();
        for (int i = 0; i < prestamos.length; i++) {
            if (prestamos[i].idElemento.equals(id)) {
                eliminarPrestamo(prestamos[i].id);
            }
        }

        // Si el elemento estaba en alguna lista, se quita de esa lista
        ListaPersonalizada[] listas = leerListas();
        for (int i = 0; i < listas.length; i++) {
            if (quitarDeLista(listas[i], id)) {
                actualizarLista(listas[i]);
            }
        }
    }
    // Devuelve los géneros sin repetir. "Drill" y "drill" cuentan como el mismo.
    public static String[] generosUnicos(ElementoMultimedia[] elementos) {
        String[] temp = new String[elementos.length];
        int cantidad = 0;

        for (int i = 0; i < elementos.length; i++) {
            boolean repetido = false;
            for (int j = 0; j < cantidad; j++) {
                if (temp[j].equalsIgnoreCase(elementos[i].genero)) {
                    repetido = true;
                    break;
                }  
            }
            if (!repetido) {
                temp[cantidad] = elementos[i].genero;
                cantidad++;
            }
        }

        String[] resultado = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            resultado[i] = temp[i];
        }
        return resultado;
    }

    public static ElementoMultimedia buscarElementoPorId(String id) {
        ElementoMultimedia[] elementos = leerElementos();
        for (int i = 0; i < elementos.length; i++) {
            if (elementos[i].id.equals(id)) {
                return elementos[i];
            }
        }
        return null;
    }

    // ---------- Préstamos ----------

    private static String lineaPrestamo(Prestamo p) {
        return p.id + "|" + p.idElemento + "|" + p.persona + "|" + p.fechaPrestamo + "|" + p.fechaDevolucion;
    }

    public static void guardarPrestamo(Prestamo prestamo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_PRESTAMOS, true))) {
            writer.println(lineaPrestamo(prestamo));
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

        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_PRESTAMOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split("\\|");

                if (partes.length >= 4) {
                    String id = partes[0].trim();
                    String idElemento = partes[1].trim();
                    String persona = partes[2].trim();
                    String fechaPrestamo = partes[3].trim();
                    String fechaDevolucion = (partes.length > 4) ? partes[4].trim() : "Sin fecha";

                    prestamosTemp[indice] = new Prestamo(id, idElemento, persona, fechaPrestamo, fechaDevolucion);
                    indice++;
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
                if (!prestamos[i].id.equals(id)) {
                    writer.println(lineaPrestamo(prestamos[i]));
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar el préstamo: " + ex.getMessage());
        }
    }

    // Revisa si un elemento ya tiene un préstamo activo
    public static boolean estaPrestado(String idElemento) {
        Prestamo[] prestamos = leerPrestamos();
        for (int i = 0; i < prestamos.length; i++) {
            if (prestamos[i].idElemento.equals(idElemento)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Listas personalizadas ----------

    private static String lineaLista(ListaPersonalizada l) {
        return l.id + "|" + l.nombre + "|" + l.descripcion + "|" + l.imagen + "|" + String.join(",", l.idsElementos);
    }

    public static void guardarLista(ListaPersonalizada lista) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, true))) {
            writer.println(lineaLista(lista));
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

        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_LISTAS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split("\\|");

                if (partes.length >= 4) {
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

    public static void actualizarLista(ListaPersonalizada listaActualizada) {
        ListaPersonalizada[] listas = leerListas();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, false))) {
            for (int i = 0; i < listas.length; i++) {
                ListaPersonalizada l = listas[i];

                if (l.id.equals(listaActualizada.id)) {
                    l = listaActualizada;
                }
                writer.println(lineaLista(l));
            }
        } catch (IOException ex) {
            System.out.println("Error al actualizar la lista: " + ex.getMessage());
        }
    }

    public static void eliminarLista(String id) {
        ListaPersonalizada[] listas = leerListas();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, false))) {
            for (int i = 0; i < listas.length; i++) {
                if (listas[i].id.equals(id)) {
                    borrarPortada(listas[i].imagen);
                } else {
                    writer.println(lineaLista(listas[i]));
                }
            }
        } catch (IOException ex) {
            System.out.println("Error al eliminar la lista personalizada: " + ex.getMessage());
        }
    }

    // Revisa si un elemento ya está dentro de la lista
    public static boolean listaContiene(ListaPersonalizada lista, String idElemento) {
        for (int i = 0; i < lista.idsElementos.length; i++) {
            if (lista.idsElementos[i].equals(idElemento)) {
                return true;
            }
        }
        return false;
    }

    // Quita un id del arreglo de la lista. Devuelve true si lo encontró y lo quitó.
    // Como los arreglos tienen tamaño fijo, se crea uno nuevo con un espacio menos.
    public static boolean quitarDeLista(ListaPersonalizada lista, String idElemento) {
        if (!listaContiene(lista, idElemento)) {
            return false;
        }

        String[] nuevosIds = new String[lista.idsElementos.length - 1];
        int idx = 0;
        for (int i = 0; i < lista.idsElementos.length; i++) {
            if (!lista.idsElementos[i].equals(idElemento)) {
                nuevosIds[idx] = lista.idsElementos[i];
                idx++;
            }
        }
        lista.idsElementos = nuevosIds;
        return true;
    }
}
