package com.coleccion_multimedia;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("Mis prestamos")
@PageTitle ("Mis prestamos")
public class PrestamosView extends VerticalLayout {
    public PrestamosView() { 
        //permite editar esta vista con css despues
        setClassName("prestamos-view");
    }


}
