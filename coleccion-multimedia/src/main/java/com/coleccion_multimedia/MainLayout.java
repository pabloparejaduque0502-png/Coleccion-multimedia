package com.coleccion_multimedia;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {
    //en este archivo se define la barra superior que se mantiene en todas las vistas de la aplicacion para no tener
    //que repetir el mismo codigo en todas las vistas
    public MainLayout() {
        //logo y nombre de la aplicacion
        H1 logo = new H1("☁️ Akatsuki Media");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

        // Buscador
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar película, serie...");
        searchField.setClearButtonVisible(true);
        searchField.setWidth("600px");

        //enlaces que llevaran a las otras vistas
        RouterLink linkHome = new RouterLink("Catálogo", MainView.class);
        RouterLink linkPrestamos = new RouterLink("Préstamos", PrestamosView.class);
        RouterLink linkListas = new RouterLink("Listas", ListasView.class);
        RouterLink linkEstadisticas = new RouterLink("Estadísticas", EstadisticasView.class);

        HorizontalLayout navLinks = new HorizontalLayout(linkHome, linkPrestamos, linkListas, linkEstadisticas);
        navLinks.setAlignItems(HorizontalLayout.Alignment.CENTER);
        navLinks.setSpacing(true);



        // contenedor completo de todo lo que se vera en la baarra superior
        HorizontalLayout header = new HorizontalLayout(logo, searchField, navLinks);
        header.setWidthFull();
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        //reparte los espacios entre los elementos de la baarra superior
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        //le da estetica con un margen a los extremos de la barra y una sombra sutil debajo de laa barra que hace que parezca flotando
        header.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM, LumoUtility.BoxShadow.SMALL);

        //fija permanentemente la barra en la parte superior de la aplicación web
        addToNavbar(header);

    }
}