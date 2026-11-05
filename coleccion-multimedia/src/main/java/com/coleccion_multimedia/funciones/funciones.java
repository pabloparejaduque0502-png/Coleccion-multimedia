package com.coleccion_multimedia.funciones;

//se importan las clases que van a usar las funciones para manipular sus atributos y guardarlos en los .txt entre otras cosas
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
    //estas dos se dejan como public ya que las otras vistas tambien la usaaran
    public static final String PORTADA_ELEMENTO = "default_elemento.png";
    public static final String PORTADA_LISTA = "default_lista.png";

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

    //relacionado con portadas

    // Guarda la imagen subida en la carpeta portadas con un nombre único.
    // Devuelve el nombre con el que quedó guardada, o null si hubo un error.
    public static String guardarPortada(byte[] datos, String nombreOriginal) {
        File carpeta = new File(CARPETA_PORTADAS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreFinal = System.currentTimeMillis() + "_" + nombreOriginal;
            //abre la ruta especificada dentro de la carpeta portadas, y usa fileoutputstream para escribir los bytes de la imagen en el archivo, que es como un filewriter de bytes
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

    // Devuelve el archivo de la portada, o el de por defecto si no existe para mostrar la imagen
    public static File archivoPortada(String nombre, String porDefecto) {
        File archivo = new File(CARPETA_PORTADAS, nombre);
        if (!archivo.exists()) {
            archivo = new File(CARPETA_PORTADAS, porDefecto);
        }
        return archivo;
    }

    // relacionado a los elementos

    //esta funcion arma la linea de un elemento multimedia y la deja lista para cargarla en el archivo .txt
    private static String lineaElemento(ElementoMultimedia e) {
        return e.id + "|" + e.titulo + "|" + e.tipo + "|" + e.genero + "|" + e.anio + "|" +
               e.creador + "|" + e.calificacion + "|" + e.estado + "|" + e.imagen;
    }

    //funcion que guarda un elemento multimedia en el archivo elementos.txt, usando la funcion anterior 
    public static void guardarElemento(ElementoMultimedia elemento) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, true))) {
            writer.println(lineaElemento(elemento));
        } catch (IOException e) {
            System.out.println("Error al guardar el elemento: " + e.getMessage());
        }
    }

    //funcion que me permite leer todos los elementos multimedia guardados en el archivo elementos.txt y devolverlos en un arreglo
    public static ElementoMultimedia[] leerElementos() {
        int totalLineas = contarLineas(RUTA_ELEMENTOS);
        if (totalLineas == 0) {
            return new ElementoMultimedia[0];  //esto se hace para que no devuelva null y no rompa el programa
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

                if (partes.length == 9) {
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
                    } catch(NumberFormatException e){
                        System.out.println("Año o calificación no es un número en la línea: " + linea);
                     }  catch (Exception ex) {
                        System.out.println("Error parseando linea en elementos.txt: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de elementos: " + e.getMessage());
        }

        ElementoMultimedia[] resultado = new ElementoMultimedia[indice]; //se crea esta nueva matriz, ya que la anterior pudo haber quedado con espacios vacios
        for (int i = 0; i < indice; i++) {
            resultado[i] = elementosTemp[i];
        }
        return resultado;
    }
    
    //funcion que me permite actualizar un elemento multimedia, reescribe todo el archivo elementos.txt, verifica si el id del elemento
    //que se le paso como arg, coincide con el id de algun elemento del archivo, si coincide lo reemplaza por el nuevo y lo escribe en el archivo
    //sino escribe el elemento original
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
    //funcion que me permite eliminar un elemento multimedia, reescribe todo el archivo elementos.txt
    //ademas de eliminarlo de elementos, revisa si hay esta prestado y elimina el prestamo
    //y si esta en alguna lista, lo elimina de esa lista
    public static void eliminarElemento(String id) {
        ElementoMultimedia[] elementos = leerElementos();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ELEMENTOS, false))) {
            for (int i = 0; i < elementos.length; i++) {  // reescribe todo el archivo, excepto el elemento que se quiere eliminar
                if (elementos[i].id.equals(id)) {
                    borrarPortada(elementos[i].imagen); //lo unico que tiene que borrar explicitamente es el archivo de portada, ya que el elemento se elimina al no escribirse en el archivo
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
    // Revisa si un texto está vacío o tiene solo espacios
    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // En los .txt el | separa los campos y el salto de línea separa los registros.
    // Si un texto trae alguno de los dos, la línea se dañaría al guardarla.

    //esta funcion revisa si un texto tiene alguno de los caracteres prohibidos, los cuales son el salto de linea y el caracter |
    //ya que estos caracteres se usan para separar los campos y los registros en los archivos .txt y puedenn generar inconvenientes
     
    public static boolean tieneCaracterProhibido(String texto) {
        return texto != null && (texto.contains("|") || texto.contains("\n"));
    }
    // esta funcion revisa todos los generos de los elementos, revisa cuales estan repetidos, y devuelve un arreglo con los generos unicos, sin repetidos
    public static String[] generosUnicos(ElementoMultimedia[] elementos) {
        String[] temp = new String[elementos.length];
        int cantidad = 0;
        //recorre todos los elementos y va guardando su genero en la matrizz temp en caso que se no se haya guardado antes
        for (int i = 0; i < elementos.length; i++) {
            boolean repetido = false;
            for (int j = 0; j < cantidad; j++) { //va hasta cantidad para evitar que compare con un null y arroje un error, asi solo revisa haasta la posicion que se ha llenado hasta el momento
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
    //busca el elemento por su id y lo devuelve, si no lo encuentra devuelve null
    public static ElementoMultimedia buscarElementoPorId(String id) {
        ElementoMultimedia[] elementos = leerElementos();
        for (int i = 0; i < elementos.length; i++) {
            if (elementos[i].id.equals(id)) {
                return elementos[i];
            }
        }
        return null;
    }

    // relacionado a prestamos

    //funciona de la misma manera que lineaElemento, pero con los atributos de la clase Prestamo, devuelve una linea lista para guardar en el archivo prestamos.txt
    private static String lineaPrestamo(Prestamo p) {
        return p.id + "|" + p.idElemento + "|" + p.persona + "|" + p.fechaPrestamo + "|" + p.fechaDevolucion;
    }
    //funcion que guarda un prestamo en el archivo prestamos.txt, usando la funcion lineaPrestamo para armar la linea a guardar
    public static void guardarPrestamo(Prestamo prestamo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_PRESTAMOS, true))) {
            writer.println(lineaPrestamo(prestamo));
        } catch (IOException e) {
            System.out.println("Error al guardar el préstamo: " + e.getMessage());
        }
    }
    //funciona igual a leerElementos, pero con los atributos de la clase Prestamo, devuelve un arreglo con todos los prestamos guardados en el archivo prestamos.txt
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

                if (partes.length >= 4) { //se pone mayor que 4 para que no rompa si no tiene fecha de devolucion, ya que es opcional, apesar de que si el usuario no póne fecha, siempre se pone un valor ahi, es solo por si las moscas
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
        //funciona igual a actualizarElemento, pero con los atributos de la clase Prestamo, reescribe todo el archivo prestamos.txt
        //  y reemplaza el prestamo que tenga el mismo id que el prestamo que se le pasa como arg
        public static void actualizarPrestamo(Prestamo prestamoActualizado) {
        Prestamo[] prestamos = leerPrestamos();

        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_PRESTAMOS, false))) {
            for (int i = 0; i < prestamos.length; i++) {
                Prestamo p = prestamos[i];

                if (p.id.equals(prestamoActualizado.id)) {
                    p = prestamoActualizado;
                }
                writer.println(lineaPrestamo(p));
            }
        } catch (IOException ex) {
            System.out.println("Error al actualizar el préstamo: " + ex.getMessage());
        }
    }
    //funciona igual a eliminarElemento, pero con los atributos de la clase Prestamo, reescribe todo el archivo prestamos.txt, sin escribir el prestamo que tenga el mismo id que el argumento que se le paso
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

    // Revisa si un elemento ya tiene un préstamo activo y devuelve un booleano, sirve para evitar luego que se preste un elemento que ya está prestado
    //esta validacion no se usa en eliminarElemento ya que devuelve un booleano, pero necesito ademas de eso saber cual es el prestamo que tiene ese elemento, para poder eliminarlo
    public static boolean estaPrestado(String idElemento) {
        Prestamo[] prestamos = leerPrestamos();
        for (int i = 0; i < prestamos.length; i++) {
            if (prestamos[i].idElemento.equals(idElemento)) {
                return true;
            }
        }
        return false;
    }

    // relacionado a listas personalizadas

//funciona de la misma manera que lineaElemento, pero con los atributos de la clase ListaPersonalizada, devuelve una linea lista para guardar en el archivo listas.txt
//ademas de eso, como el atributo idsElementos es un arreglo, se usa String.join para unir todos los ids en una sola cadena separada por comas
    private static String lineaLista(ListaPersonalizada l) {
        return l.id + "|" + l.nombre + "|" + l.descripcion + "|" + l.imagen + "|" + String.join(",", l.idsElementos);
    }

    //funciona igual a guardarElemento, pero con los atributos de la clase ListaPersonalizada, guarda una lista personalizada en el archivo listas.txt, usando la funcion lineaLista 
    public static void guardarLista(ListaPersonalizada lista) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_LISTAS, true))) {
            writer.println(lineaLista(lista));
        } catch (IOException e) {
            System.out.println("Error al guardar la lista personalizada: " + e.getMessage());
        }
    }
    //lee las listas personalizadas del aarchivo y las devuelve todas en un arreglo
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
                    if (partes.length > 4 && !partes[4].trim().isEmpty()) { //aca revisamos que haya un quinto campo, porque el usuario puede tener unaa lista vacia por haber borrado todos los elementos 
                        idsElementos = partes[4].split(","); //en caso de que si haya, separamos ese quinto campo por comas y lo guardamos en el aarreglo, si no hay quinto campo, el arreglo queda vacio
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
    
    //funciona igual a actualizarElemento, pero con los atributos de la clase ListaPersonalizada, reescribe todo el archivo listas.txt exactamente igual a excepcion de la lista que tenga el mismo id que la lista que se le pasa como arg, que es reemplazada por la nueva
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
    //funciona igual a eliminarElemento, pero con los atributos de la clase ListaPersonalizada, reescribe todo el archivo listas.txt, sin escribir la lista que tenga el mismo id que el argumento que se le paso
    //ademas invoca la funcion borrarPortada para eliminar la portada de la lista que se esta eliminando
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

    // Revisa si un elemento ya está dentro de la lista, recorriendo el arreglo de idsElementos y comparando con el idElemento que se le pasa como argumento, devuelve true si lo encuentra, false si no
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
