package com.coleccion_multimedia;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route(value = "", layout = MainLayout.class)
@PageTitle("Akatsuki Media")
public class MainView extends VerticalLayout {
    public MainView() {
        // Permite editar esta vista con CSS después
        setClassName("main-view");
        
    }
}