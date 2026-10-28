package com.coleccion_multimedia;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // Tema oscuro nativo global de Vaadin Lumo
        UI.getCurrent().getElement().setAttribute("theme", "dark");
        getElement().getStyle().set("background-color", "#000000");

        H1 logo = new H1("☁️ Akatsuki Media");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
        logo.getStyle().set("color", "#ffffff");

        RouterLink linkHome = crearEnlaceNavegacion("Mi colección", MainView.class);
        RouterLink linkPrestamos = crearEnlaceNavegacion("Préstamos", PrestamosView.class);
        RouterLink linkListas = crearEnlaceNavegacion("Listas", ListasView.class);
        RouterLink linkEstadisticas = crearEnlaceNavegacion("Estadísticas", EstadisticasView.class);

        HorizontalLayout navLinks = new HorizontalLayout(linkHome, linkPrestamos, linkListas, linkEstadisticas);
        navLinks.setAlignItems(HorizontalLayout.Alignment.CENTER);
        navLinks.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(logo, navLinks);
        header.setWidthFull();
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        
        header.getStyle().set("background-color", "#121212");
        header.getStyle().set("border-bottom", "1px solid #2a2a2a");
        header.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private RouterLink crearEnlaceNavegacion(String texto, Class<? extends com.vaadin.flow.component.Component> vista) {
        RouterLink link = new RouterLink(texto, vista);
        link.getStyle().set("color", "#e0e0e0");
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("font-weight", "500");
        link.getStyle().set("padding", "8px 14px");
        link.getStyle().set("border-radius", "6px");
        return link;
    }
}