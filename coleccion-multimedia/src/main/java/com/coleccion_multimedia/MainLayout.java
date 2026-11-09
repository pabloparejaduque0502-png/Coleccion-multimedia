package com.coleccion_multimedia;

import com.vaadin.flow.component.applayout.AppLayout;  //  componente de diseño de aplicación que proporciona una estructura básica para la interfaz de usuario, incluyendo una barra de navegación y un área de contenido principal.
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink; // importacion que permite crear enlaces que lleva a otra vista de la app
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.UI; //representa la pestaña del navegaador del usuario

public class MainLayout extends AppLayout {

    public MainLayout() {
        // pone el tema oscuro de lumo en el <html> de la pagina, asi aplica a todas las vistas, aunque el tema oscuro es como gris
        UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', 'dark')"); 
        //aca el fondo se cambia de gris a negro
        getElement().getStyle().set("background-color", "#000000");

        H1 logo = new H1("☁️ Akatsuki Media");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM); //usa tamaños predefinidos
        logo.getStyle().set("color", "#ffffff");

        //enlaces que llevan a las otras vistas
        RouterLink linkHome = crearEnlaceNavegacion("Mi colección", MainView.class);
        RouterLink linkPrestamos = crearEnlaceNavegacion("Préstamos", PrestamosView.class);
        RouterLink linkListas = crearEnlaceNavegacion("Listas", ListasView.class);
        RouterLink linkEstadisticas = crearEnlaceNavegacion("Estadísticas", EstadisticasView.class);

        HorizontalLayout navLinks = new HorizontalLayout(linkHome, linkPrestamos, linkListas, linkEstadisticas);
        navLinks.setAlignItems(HorizontalLayout.Alignment.CENTER);
        navLinks.setSpacing(true);
        navLinks.getStyle().set("flex-wrap", "wrap"); //si en el celular no caben, pasan a la linea de abajo

        HorizontalLayout header = new HorizontalLayout(logo, navLinks);
        header.setWidthFull();
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN); //hace que el logo quede a la izq y los navlinks al otro extremo
        
        header.getStyle().set("background-color", "#121212");
        header.getStyle().set("border-bottom", "1px solid #2a2a2a");
        header.getStyle().set("flex-wrap", "wrap");
        header.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header); //sse agrega a la barra superior que trae applayout
    }
    //recibe el texto que saldra en el routerlink, y la vista a la que lleva
    private RouterLink crearEnlaceNavegacion(String texto, Class<? extends com.vaadin.flow.component.Component> vista) { //vista solo puede ser una clase que herede de Component
        RouterLink link = new RouterLink(texto, vista);
        link.getStyle().set("color", "#e0e0e0");
        link.getStyle().set("text-decoration", "none"); //para que no salga subrayado
        link.getStyle().set("font-weight", "500");
        link.getStyle().set("padding", "8px 14px"); //hace que el area clickeable sea mas grande
        return link;
    }
}