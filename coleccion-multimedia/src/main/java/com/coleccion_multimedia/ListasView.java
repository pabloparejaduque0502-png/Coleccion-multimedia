package com.coleccion_multimedia;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="Mis listas",layout = MainLayout.class)
@PageTitle ("Mis listas")

public class ListasView extends VerticalLayout {
    public ListasView() {
        //permite editar esta vista con css despues
        setClassName("listas-view");
     }

}
