package com.coleccion_multimedia.icon;
//trae la claase de icono
import com.vaadin.flow.component.icon.Icon;
//trae la lista de iconos de Vaadin
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Estrellas extends HorizontalLayout {

    // Atributos públicos accesibles directamente
    public int valor = 0; 
    public boolean esEditable = true; 
    public Icon[] estrellas = new Icon[5]; 

    // Constructor 1: Sin parámetros (se usa en el formulario)
    public Estrellas() {
        this.valor = 0;
        this.esEditable = true;
        iniciar();
    }

    // Constructor 2: Con parámetros (se usa para la tabla o lectura)
    public Estrellas(int valorInicial, boolean esEditable) {
        this.valor = valorInicial;
        this.esEditable = esEditable;
        iniciar();
    }

    // Método tradicional que crea las 5 estrellas y las agrega a la pantalla
    public void iniciar() {
        setSpacing(true);

        for (int i = 0; i < 5; i++) {
            final int puntuacion = i + 1;
            Icon estrella = VaadinIcon.STAR.create();

            if (this.esEditable) {
                estrella.getStyle().set("cursor", "pointer");
                estrella.addClickListener(event -> cambiarPuntuacion(puntuacion));
            }

            estrellas[i] = estrella;
            add(estrella);
        }

        actualizarVisualizacion();
    }

    // Asigna el valor directamente al atributo público y refresca el color
    public void cambiarPuntuacion(int nuevaPuntuacion) {
        this.valor = nuevaPuntuacion;
        actualizarVisualizacion();
    }

    // Dibuja las estrellas de color dorado o tenue
    public void actualizarVisualizacion() {
        for (int i = 0; i < 5; i++) {
            if (i < valor) {
                estrellas[i].getStyle().set("color", "gold");
                estrellas[i].getStyle().set("opacity", "1.0");
            } else {
                estrellas[i].getStyle().set("color", "var(--lumo-contrast-30pct)");
                estrellas[i].getStyle().set("opacity", "0.4");
            }
        }
    }

    // Reinicia el valor a 0
    public void limpiar() {
        this.valor = 0;
        actualizarVisualizacion();
    }
}