package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.ListaPersonalizada;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.funciones.funciones;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route(value = "estadisticas", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Estadísticas")
public class EstadisticasView extends VerticalLayout {

    public EstadisticasView() {
        setClassName("estadisticas-view");
        setSizeFull();
        getStyle().set("background-color", "#000000");
        setPadding(true);
        setSpacing(true);

        // 1. Encabezado principal (Reciclado de MainView / ListasView)
        H2 titulo = new H2("Estadísticas de tu Colección");
        titulo.getStyle().set("color", "#ffffff");
        add(titulo);

        // 2. Carga directa de datos desde archivos planos
        ElementoMultimedia[] elementos = funciones.leerElementos();
        Prestamo[] prestamos = funciones.leerPrestamos();
        ListaPersonalizada[] listas = funciones.leerListas();

        HorizontalLayout barraEst = crearBarraEst(elementos, prestamos, listas);
        add(barraEst);

        // Los componentes visuales de KPIs, Tipos y Estados se irán acoplando aquí paso a paso
    }
       // --- SECCIÓN 1: MÉTRICAS GLOBALES (EST) ---
    private HorizontalLayout crearBarraEst(ElementoMultimedia[] elementos, Prestamo[] prestamos, ListaPersonalizada[] listas) {
        HorizontalLayout barra = new HorizontalLayout();
        barra.setWidthFull();
        barra.getStyle().set("gap", "15px");
        barra.getStyle().set("flex-wrap", "wrap");

        int totalElementos = elementos.length;
        int totalPrestamos = prestamos.length;
        int totalListas = listas.length;

        // Recorrido manual con arreglo tradicional para acumular calificaciones
        double sumaCalificaciones = 0;
        for (int i = 0; i < elementos.length; i++) {
            if (elementos[i] != null) {
                sumaCalificaciones += elementos[i].calificacion;
            }
        }

        // Prevención de división por cero si la colección está vacía
        double promedio = (totalElementos > 0) ? (sumaCalificaciones / totalElementos) : 0.0;
        String promedioTexto = String.format("%.1f ★", promedio);

        // Ensamblado de las 4 tarjetas Est
        barra.add(
            crearTarjetaEst("Total Colección", String.valueOf(totalElementos), "#e50914"),
            crearTarjetaEst("Préstamos Activos", String.valueOf(totalPrestamos), "#ff4d4d"),
            crearTarjetaEst("Listas Creadas", String.valueOf(totalListas), "#ffffff"),
            crearTarjetaEst("Calificación Promedio", promedioTexto, "#ffb400")
        );

        return barra;
    }

    private VerticalLayout crearTarjetaEst(String etiqueta, String valor, String colorValor) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("flex", "1");
        tarjeta.getStyle().set("min-width", "180px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(false);

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
}