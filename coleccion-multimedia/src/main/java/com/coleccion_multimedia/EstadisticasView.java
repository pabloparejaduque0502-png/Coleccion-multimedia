package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.ListaPersonalizada;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.funciones.funciones;
import com.vaadin.flow.component.html.H2;
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

      // 4. Contenedor flexible para los paneles de desglose
        FlexLayout contenedorPaneles = new FlexLayout();
        contenedorPaneles.setWidthFull();
        contenedorPaneles.getStyle().set("flex-wrap", "wrap");
        contenedorPaneles.getStyle().set("gap", "20px");
        contenedorPaneles.getStyle().set("margin-top", "10px");

        // Renderizado e inclusión del panel de tipos
        VerticalLayout panelTipos = crearPanelPorTipo(elementos);
        VerticalLayout panelEstados = crearPanelPorEstado(elementos);
        contenedorPaneles.add(panelTipos,panelEstados);
        

        add(contenedorPaneles);


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
            crearTarjetaEst("Listas Creadas", String.valueOf(totalListas), "#ff4d4d"),
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
    // --- SECCIÓN 2: DESGLOSE POR TIPO DE MULTIMEDIA ---
    private VerticalLayout crearPanelPorTipo(ElementoMultimedia[] elementos) {
        VerticalLayout panel = new VerticalLayout();
        panel.getStyle().set("background-color", "#181818");
        panel.getStyle().set("border", "1px solid #2a2a2a");
        panel.getStyle().set("border-radius", "12px");
        panel.getStyle().set("flex", "1");
        panel.getStyle().set("min-width", "300px");
        panel.setPadding(true);
        panel.setSpacing(true);

        H4 tituloPanel = new H4("Distribución por Tipo");
        tituloPanel.getStyle().set("color", "#ffffff");
        tituloPanel.getStyle().set("margin", "0 0 10px 0");
        panel.add(tituloPanel);

        TipoMultimedia[] tipos = TipoMultimedia.values();

        // Recorrido de los tipos del enum
        for (int i = 0; i < tipos.length; i++) {
            TipoMultimedia t = tipos[i];
            int contador = 0;

            // Conteo manual en el arreglo plano
            for (int j = 0; j < elementos.length; j++) {
                if (elementos[j] != null && elementos[j].tipo == t) {
                    contador++;
                }
            }

            panel.add(crearFilaConBarra(t.name(), contador, elementos.length, "#e50914"));
        }

        return panel;
    }

    // --- COMPONENTE AUXILIAR: BARRA DE PROGRESO EN CSS ---
    private VerticalLayout crearFilaConBarra(String etiqueta, int cantidad, int totalGlobal, String colorBarra) {
        VerticalLayout contenedorFila = new VerticalLayout();
        contenedorFila.setPadding(false);
        contenedorFila.setSpacing(false);
        contenedorFila.setWidthFull();

        double porcentaje = (totalGlobal > 0) ? ((double) cantidad / totalGlobal) * 100.0 : 0.0;

        Span lblTexto = new Span(etiqueta + " (" + cantidad + ")");
        lblTexto.getStyle().set("color", "#dddddd");
        lblTexto.getStyle().set("font-size", "0.85rem");

        Span lblPorcentaje = new Span(String.format("%.0f%%", porcentaje));
        lblPorcentaje.getStyle().set("color", "#888888");
        lblPorcentaje.getStyle().set("font-size", "0.85rem");

        HorizontalLayout encabezadoFila = new HorizontalLayout(lblTexto, lblPorcentaje);
        encabezadoFila.setWidthFull();
        encabezadoFila.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Contenedor base gris oscuro de la barra
        FlexLayout barraFondo = new FlexLayout();
        barraFondo.setWidthFull();
        barraFondo.getStyle().set("height", "8px");
        barraFondo.getStyle().set("background-color", "#2a2a2a");
        barraFondo.getStyle().set("border-radius", "4px");
        barraFondo.getStyle().set("margin-top", "4px");
        barraFondo.getStyle().set("overflow", "hidden");

        // Relleno de color proporcional al porcentaje
        FlexLayout barraRelleno = new FlexLayout();
        barraRelleno.setWidth(porcentaje + "%");
        barraRelleno.getStyle().set("height", "100%");
        barraRelleno.getStyle().set("background-color", colorBarra);

        barraFondo.add(barraRelleno);
        contenedorFila.add(encabezadoFila, barraFondo);

        return contenedorFila;
    }
    private VerticalLayout crearPanelPorEstado(ElementoMultimedia[] elementos) {
    VerticalLayout panel = new VerticalLayout();
    panel.getStyle().set("background-color", "#181818");
    panel.getStyle().set("border", "1px solid #2a2a2a");
    panel.getStyle().set("border-radius", "12px");
    panel.getStyle().set("flex", "1");
    panel.getStyle().set("min-width", "300px");
    panel.setPadding(true);
    panel.setSpacing(true);

    H4 tituloPanel = new H4("Estado de Consumo");
    tituloPanel.getStyle().set("color", "#ffffff");
    tituloPanel.getStyle().set("margin", "0 0 10px 0");
    panel.add(tituloPanel);

    // Grilla flexible para ubicar las tarjetas internas
    FlexLayout grillaEstados = new FlexLayout();
    grillaEstados.setWidthFull();
    grillaEstados.getStyle().set("gap", "12px");
    grillaEstados.getStyle().set("flex-wrap", "wrap");

    EstadoMultimedia[] estados = EstadoMultimedia.values();

    for (int i = 0; i < estados.length; i++) {
        EstadoMultimedia est = estados[i];
        int contador = 0;

        // Conteo manual en el arreglo plano
        for (int j = 0; j < elementos.length; j++) {
            if (elementos[j] != null && elementos[j].estado == est) {
                contador++;
            }
        }

        String colorValor;
        switch (est) {
            case COMPLETADO:
                colorValor = "#4caf50"; // Verde
                break;
            case EN_PROCESO:
                colorValor = "#2196f3"; // Azul
                break;
            case PENDIENTE:
                colorValor = "#ff9800"; // Naranja
                break;
            default:
                colorValor = "#ff4d4d"; // Rojo Akatsuki para ABANDONADO u otros
                break;
        }

        String nombreEstado = est.name().replace("_", " ");
        
        // Se reutiliza la estructura de tarjetas 'Est' adaptando el fondo e indicando min-width
        VerticalLayout tarjetaEstado = crearTarjetaEst(nombreEstado, String.valueOf(contador), colorValor);
        tarjetaEstado.getStyle().set("background-color", "#111111");
        tarjetaEstado.getStyle().set("min-width", "130px");
        
        grillaEstados.add(tarjetaEstado);
    }

    panel.add(grillaEstados);
    return panel;
}
}