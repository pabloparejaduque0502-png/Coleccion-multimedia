package com.coleccion_multimedia;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="Mis estadisticas", layout = MainLayout.class)
@PageTitle ("Mis estadisticas")

public class EstadisticasView extends VerticalLayout {
    public EstadisticasView() { 
        //permite editar esta vista con css despues
        setClassName("estadisticas-view");
    }


}
