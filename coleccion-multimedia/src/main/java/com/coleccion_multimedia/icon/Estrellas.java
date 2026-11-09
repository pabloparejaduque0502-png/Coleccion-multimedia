package com.coleccion_multimedia.icon;
//trae la claase de icono, un componente que se dibuja en pantalla y se le puede poner color, tamaño,etc
import com.vaadin.flow.component.icon.Icon;
//trae la lista de iconos de Vaadin, la cual es un enum de los iconos posibles que se pueden usar, en este caso se usa el icono de estrella
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
            //es final int para qque no se pueda modificar el valor de la puntuacion, ya que se usa dentro de un lambda y no se puede usar una variable que no sea final
            final int puntuacion = i + 1;   //se le aasigna un valor a cada estrella de 1 a 5, para que cuando se haga click en una estrella se pueda asignar el valor correspondiente a la puntuacion
            Icon estrella = VaadinIcon.STAR.create();

            if (this.esEditable) {
                estrella.getStyle().set("cursor", "pointer");
                estrella.addClickListener(event -> cambiarPuntuacion(puntuacion));
            }

            estrellas[i] = estrella; //aca se llena el arreglo de iconos con las estrellas creadas
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
                estrellas[i].getStyle().set("color", "var(--lumo-contrast-30pct)"); //color contraario al fondo, 30pct, el 30% de intensidad
                estrellas[i].getStyle().set("opacity", "0.4"); //opacidad de 0.4 para que se vea un poco transparentoso
            }
        }
    }

    // Reinicia el valor a 0 para los formularios, para que no se cargue con los valores anteriores
    public void limpiar() {
        this.valor = 0;
        actualizarVisualizacion();
    }
}