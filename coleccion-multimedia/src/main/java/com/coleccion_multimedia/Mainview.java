package com.coleccion_multimedia;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route("")
@PageTitle("Akatsuki Media")

public class Mainview extends VerticalLayout {
    public Mainview() {
        //permite editar esta vista con css despues
        setClassName("main-view");
     }

}
