package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.ListaPersonalizada;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.funciones.funciones;
import com.coleccion_multimedia.icon.Estrellas;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "estadisticas", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Estadísticas")
public class EstadisticasView extends VerticalLayout {

    public EstadisticasView() {
        setWidthFull();
        setMinHeight("100%");
        getStyle().set("background-color", "#000000");
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Estadísticas de tu Colección");
        titulo.getStyle().set("color", "#ffffff");

        ElementoMultimedia[] elementos = funciones.leerElementos();
        Prestamo[] prestamos = funciones.leerPrestamos();
        ListaPersonalizada[] listas = funciones.leerListas();

        // Los dos paneles van uno al lado del otro, y en el celular uno debajo del otro
        FlexLayout contenedorPaneles = new FlexLayout();
        contenedorPaneles.setWidthFull();
        contenedorPaneles.getStyle().set("flex-wrap", "wrap");
        contenedorPaneles.getStyle().set("gap", "20px");
        contenedorPaneles.getStyle().set("margin-top", "10px");
        // Columna derecha: Estado arriba y Género debajo, para llenar el espacio que deja Tipo
        VerticalLayout panelEstados = crearPanelPorEstado(elementos);
        panelEstados.getStyle().set("flex", "none");

        VerticalLayout columnaDerecha = new VerticalLayout(panelEstados, crearPanelPorGenero(elementos));
        columnaDerecha.setPadding(false);
        columnaDerecha.setSpacing(false);
        columnaDerecha.getStyle().set("gap", "20px");
        columnaDerecha.getStyle().set("flex", "1");
        columnaDerecha.getStyle().set("min-width", "min(300px, 100%)");

        contenedorPaneles.add(crearPanelPorTipo(elementos), columnaDerecha);

        VerticalLayout panelTop = crearPanelTop(elementos);
        panelTop.getStyle().set("flex", "none");

        add(titulo, crearBarraEst(elementos, prestamos, listas), contenedorPaneles, panelTop);
    }

    // Tarjetas de arriba: totales y calificación promedio
    private HorizontalLayout crearBarraEst(ElementoMultimedia[] elementos, Prestamo[] prestamos, ListaPersonalizada[] listas) {
        HorizontalLayout barra = new HorizontalLayout();
        barra.setWidthFull();
        barra.getStyle().set("gap", "15px");
        barra.getStyle().set("flex-wrap", "wrap");

        double sumaCalificaciones = 0;
        for (int i = 0; i < elementos.length; i++) {
            sumaCalificaciones += elementos[i].calificacion;
        }

        // Si no hay elementos el promedio queda en 0, para no dividir entre cero
        double promedio = 0.0;
        if (elementos.length > 0) {
            promedio = sumaCalificaciones / elementos.length;
        }

        barra.add(
            crearTarjetaEst("Total Colección", String.valueOf(elementos.length), "#e50914"),
            crearTarjetaEst("Préstamos Activos", String.valueOf(prestamos.length), "#ff4d4d"),
            crearTarjetaEst("Listas Creadas", String.valueOf(listas.length), "#ff4d4d"),
            crearTarjetaEst("Calificación Promedio", String.format("%.1f ★", promedio), "#ffb400")
        );
        return barra;
    }

    private VerticalLayout crearTarjetaEst(String etiqueta, String valor, String colorValor) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.setPadding(true);
        tarjeta.setSpacing(false);
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("flex", "1");
        tarjeta.getStyle().set("min-width", "180px");

        Span lblTitulo = new Span(etiqueta);
        lblTitulo.getStyle().set("color", "#aaaaaa");
        lblTitulo.getStyle().set("font-size", "0.85rem");

        H3 lblValor = new H3(valor);
        lblValor.getStyle().set("color", colorValor);
        lblValor.getStyle().set("margin", "8px 0 0 0");
        lblValor.getStyle().set("font-size", "1.8rem");

        tarjeta.add(lblTitulo, lblValor);
        return tarjeta;
    }

    // Panel con una barra de porcentaje por cada tipo
    private VerticalLayout crearPanelPorTipo(ElementoMultimedia[] elementos) {
        VerticalLayout panel = crearPanel("Distribución por Tipo");

        TipoMultimedia[] tipos = TipoMultimedia.values();
        for (int i = 0; i < tipos.length; i++) {
            int contador = 0;
            for (int j = 0; j < elementos.length; j++) {
                if (elementos[j].tipo == tipos[i]) {
                    contador++;
                }
            }
            panel.add(crearFilaConBarra(tipos[i].name(), contador, elementos.length, "#e50914"));
        }
        return panel;
    }

    // Panel con una tarjeta por cada estado, cada estado con su color
    private VerticalLayout crearPanelPorEstado(ElementoMultimedia[] elementos) {
        VerticalLayout panel = crearPanel("Estado de Consumo");

        FlexLayout grillaEstados = new FlexLayout();
        grillaEstados.setWidthFull();
        grillaEstados.getStyle().set("gap", "12px");
        grillaEstados.getStyle().set("flex-wrap", "wrap");

        EstadoMultimedia[] estados = EstadoMultimedia.values();
        for (int i = 0; i < estados.length; i++) {
            int contador = 0;
            for (int j = 0; j < elementos.length; j++) {
                if (elementos[j].estado == estados[i]) {
                    contador++;
                }
            }

            String colorValor;
            switch (estados[i]) {
                case COMPLETADO:
                    colorValor = "#4caf50";
                    break;
                case EN_PROCESO:
                    colorValor = "#2196f3";
                    break;
                case PENDIENTE:
                    colorValor = "#ff9800";
                    break;
                default:
                    colorValor = "#ff4d4d";
                    break;
            }

            String nombreEstado = estados[i].name().replace("_", " ");
            VerticalLayout tarjetaEstado = crearTarjetaEst(nombreEstado, String.valueOf(contador), colorValor);
            tarjetaEstado.getStyle().set("background-color", "#111111");
            tarjetaEstado.getStyle().set("min-width", "130px");
            grillaEstados.add(tarjetaEstado);
        }

        panel.add(grillaEstados);
        return panel;
    }
       // Panel con los 3 géneros que más se repiten
    private VerticalLayout crearPanelPorGenero(ElementoMultimedia[] elementos) {
        VerticalLayout panel = crearPanel("Top 3 Géneros");

        // Arreglos paralelos: el género en generos[i] tiene cantidades[i] elementos
        String[] generos = funciones.generosUnicos(elementos);
        int[] cantidades = new int[generos.length];
        for (int i = 0; i < generos.length; i++) {
            for (int j = 0; j < elementos.length; j++) {
                if (elementos[j].genero.equalsIgnoreCase(generos[i])) {
                    cantidades[i]++;
                }
            }
        }

        // Burbuja de mayor a menor cantidad, moviendo los dos arreglos a la vez
        for (int i = 0; i < generos.length - 1; i++) {
            for (int j = 0; j < generos.length - i - 1; j++) {
                if (cantidades[j] < cantidades[j + 1]) {
                    int tempCantidad = cantidades[j];
                    cantidades[j] = cantidades[j + 1];
                    cantidades[j + 1] = tempCantidad;

                    String tempGenero = generos[j];
                    generos[j] = generos[j + 1];
                    generos[j + 1] = tempGenero;
                }
            }
        }

        // Se muestran 3, o menos si hay menos de 3 géneros
        int cantidadTop = 3;
        if (generos.length < 3) {
            cantidadTop = generos.length;
        }

        for (int i = 0; i < cantidadTop; i++) {
            panel.add(crearFilaConBarra(generos[i], cantidades[i], elementos.length, "#e50914"));
        }
        
        // Si hay más de 3 géneros, el resto se ve al desplegar este Details
        if (generos.length > 3) {
            VerticalLayout restoGeneros = new VerticalLayout();
            restoGeneros.setPadding(false);
            restoGeneros.setSpacing(true);
            for (int i = 3; i < generos.length; i++) {
                restoGeneros.add(crearFilaConBarra(generos[i], cantidades[i], elementos.length, "#e50914"));
            }

            Span lblSummary = new Span("Ver todos los géneros (" + generos.length + ")");
            lblSummary.getStyle().set("color", "#ffffff");
            lblSummary.getStyle().set("font-size", "0.85rem");
            lblSummary.getStyle().set("font-weight", "500");

            Details detallesGeneros = new Details(lblSummary, restoGeneros);
            detallesGeneros.getElement().getStyle().set("--lumo-secondary-text-color", "#ffffff");
            detallesGeneros.getStyle().set("width", "100%");
            panel.add(detallesGeneros);
        }
        return panel;
    }

    // Panel con los 5 elementos mejor calificados
    private VerticalLayout crearPanelTop(ElementoMultimedia[] elementos) {
        VerticalLayout panel = crearPanel("Top 5 mejor calificados");

        // Se ordena una copia, para no cambiar el orden del arreglo original
        ElementoMultimedia[] ordenados = new ElementoMultimedia[elementos.length];
        for (int i = 0; i < elementos.length; i++) {
            ordenados[i] = elementos[i];
        }

        // Burbuja de mayor a menor calificación
        for (int i = 0; i < ordenados.length - 1; i++) {
            for (int j = 0; j < ordenados.length - i - 1; j++) {
                if (ordenados[j].calificacion < ordenados[j + 1].calificacion) {
                    ElementoMultimedia temp = ordenados[j];
                    ordenados[j] = ordenados[j + 1];
                    ordenados[j + 1] = temp;
                }
            }
        }

        if (ordenados.length == 0) {
            Span lblVacio = new Span("Aún no hay elementos en tu colección.");
            lblVacio.getStyle().set("color", "#888888");
            lblVacio.getStyle().set("font-size", "0.85rem");
            panel.add(lblVacio);
        }

        // Se muestran 5, o menos si la colección tiene menos de 5
        int cantidadTop = 5;
        if (ordenados.length < 5) {
            cantidadTop = ordenados.length;
        }

        for (int i = 0; i < cantidadTop; i++) {
            ElementoMultimedia e = ordenados[i];

            Span lblTitulo = new Span((i + 1) + ". " + e.titulo);
            lblTitulo.getStyle().set("color", "#dddddd");
            lblTitulo.getStyle().set("font-size", "0.85rem");

            Span lblInfo = new Span(e.tipo + "  |  " + e.genero);
            lblInfo.getStyle().set("color", "#888888");
            lblInfo.getStyle().set("font-size", "0.8rem");

            VerticalLayout textos = new VerticalLayout(lblTitulo, lblInfo);
            textos.setPadding(false);
            textos.setSpacing(false);

            HorizontalLayout fila = new HorizontalLayout(textos, new Estrellas(e.calificacion, false));
            fila.setWidthFull();
            fila.setJustifyContentMode(JustifyContentMode.BETWEEN);
            fila.setAlignItems(Alignment.CENTER);
            panel.add(fila);
        }
        return panel;
    }
    // Caja oscura con título que usan los dos paneles
    private VerticalLayout crearPanel(String titulo) {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.getStyle().set("background-color", "#181818");
        panel.getStyle().set("border", "1px solid #2a2a2a");
        panel.getStyle().set("border-radius", "12px");
        panel.getStyle().set("flex", "1");
        panel.getStyle().set("min-width", "min(300px, 100%)");

        H4 tituloPanel = new H4(titulo);
        tituloPanel.getStyle().set("color", "#ffffff");
        tituloPanel.getStyle().set("margin", "0 0 10px 0");
        panel.add(tituloPanel);
        return panel;
    }

    // Fila con el nombre, la cantidad, el porcentaje y una barra que se llena según el porcentaje
    private VerticalLayout crearFilaConBarra(String etiqueta, int cantidad, int total, String colorBarra) {
        double porcentaje = 0.0;
        if (total > 0) {
            porcentaje = (double) cantidad / total * 100.0;
        }

        Span lblTexto = new Span(etiqueta + " (" + cantidad + ")");
        lblTexto.getStyle().set("color", "#dddddd");
        lblTexto.getStyle().set("font-size", "0.85rem");

        Span lblPorcentaje = new Span(String.format("%.0f%%", porcentaje));
        lblPorcentaje.getStyle().set("color", "#888888");
        lblPorcentaje.getStyle().set("font-size", "0.85rem");

        HorizontalLayout encabezadoFila = new HorizontalLayout(lblTexto, lblPorcentaje);
        encabezadoFila.setWidthFull();
        encabezadoFila.setJustifyContentMode(JustifyContentMode.BETWEEN);

        FlexLayout barraFondo = new FlexLayout();
        barraFondo.setWidthFull();
        barraFondo.getStyle().set("height", "8px");
        barraFondo.getStyle().set("background-color", "#2a2a2a");
        barraFondo.getStyle().set("border-radius", "4px");
        barraFondo.getStyle().set("margin-top", "4px");
        barraFondo.getStyle().set("overflow", "hidden");

        FlexLayout barraRelleno = new FlexLayout();
        barraRelleno.setWidth(porcentaje + "%");
        barraRelleno.getStyle().set("height", "100%");
        barraRelleno.getStyle().set("background-color", colorBarra);
        barraFondo.add(barraRelleno);

        VerticalLayout fila = new VerticalLayout(encabezadoFila, barraFondo);
        fila.setPadding(false);
        fila.setSpacing(false);
        fila.setWidthFull();
        return fila;
    }
}
