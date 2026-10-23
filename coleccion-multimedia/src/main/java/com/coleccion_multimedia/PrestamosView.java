package com.coleccion_multimedia;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "prestamos", layout = MainLayout.class)
@PageTitle("Mis préstamos")
public class PrestamosView extends VerticalLayout {
    public PrestamosView() {
        // Permite editar esta vista con CSS después
        setClassName("prestamos-view");
    }
}
