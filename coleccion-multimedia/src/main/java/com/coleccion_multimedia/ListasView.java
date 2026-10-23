package com.coleccion_multimedia;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("Mis listas")
@PageTitle ("Mis listas")

public class ListasView extends VerticalLayout {
    public ListasView() {
        //permite editar esta vista con css despues
        setClassName("listas-view");
     }

}
