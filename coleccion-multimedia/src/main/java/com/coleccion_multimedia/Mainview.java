package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.funciones.funciones;
import com.coleccion_multimedia.icon.Estrellas;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Span;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mi Colección")
public class MainView extends VerticalLayout {

    public MainView() {
        setClassName("main-view");

        //crecacion del boton para agregar nuevos elementos

        Dialog dialogFormulario = new Dialog();
        dialogFormulario.setHeaderTitle("Agregar Nuevo Elemento");

       //creaacion de los campos de entrada de texto
        TextField txtTitulo = new TextField("Título");
        ComboBox<TipoMultimedia> cbTipo = new ComboBox<>("Tipo");
        cbTipo.setItems(TipoMultimedia.values());

        TextField txtGenero = new TextField("Género");
        TextField txtAnio = new TextField("Año");
        TextField txtCreador = new TextField("Creador (Director/Artista)");
        Estrellas ratingCalificacion = new Estrellas();
        Span lblCalificacion = new Span("Calificación");
        lblCalificacion.getStyle().set("font-size", "var(--lumo-font-size-s)");
        lblCalificacion.getStyle().set("color", "var(--lumo-secondary-text-color)");
        lblCalificacion.getStyle().set("font-weight", "500");

        // 3. Empaquetar la etiqueta y las estrellas en una cajita vertical local
        VerticalLayout campoCalificacion = new VerticalLayout(lblCalificacion, ratingCalificacion);
        campoCalificacion.setPadding(false);
        campoCalificacion.setSpacing(false);
        
        TextField txtEstado = new TextField("Estado");

        //Crear el botón Guardar para el interior del Dialog
        Button btnGuardar = new Button("Guardar", event -> {

    //Validaciones al presionar el boton guardar
    if (txtTitulo.isEmpty() || cbTipo.getValue() == null || txtGenero.isEmpty() || 
        txtAnio.isEmpty() || txtCreador.isEmpty() || ratingCalificacion.valor == 0 || txtEstado.isEmpty()) {
        Notification.show("Por favor, completa todos los campos del formulario.");
        return; // Detiene la ejecución si falta algún dato
    }

    try {
        
        int anio = Integer.parseInt(txtAnio.getValue());
        int calificacion = ratingCalificacion.valor;

        
        if (calificacion < 1 || calificacion > 5) {
            Notification.show("La calificación debe ser un número entre 1 y 5.");
            return;
        }

        
        if (anio < 1800 || anio > 2026) {
            Notification.show("Por favor, ingresa un año válido.");
            return;
        }

        // Si todas las validaciones pasan, se asigna ID y se crea el objeto
        int nuevoIdNumero = funciones.leerElementos().length + 1;
        String id = String.valueOf(nuevoIdNumero);

        ElementoMultimedia nuevo = new ElementoMultimedia(
            id,
            txtTitulo.getValue(),
            cbTipo.getValue(),
            txtGenero.getValue(),
            anio,
            txtCreador.getValue(),
            calificacion,
            txtEstado.getValue()
        );

        funciones.guardarElemento(nuevo);
        Notification.show("¡Elemento guardado en el archivo!");

        // Limpiar campos y cerrar diálogo
        txtTitulo.clear();
        cbTipo.clear();
        txtGenero.clear();
        txtAnio.clear();
        txtCreador.clear();
        ratingCalificacion.limpiar();
        txtEstado.clear();

        dialogFormulario.close();

    } catch (NumberFormatException ex) {
        Notification.show("Error: 'Año' y 'Calificación' deben ser números enteros.");
    }
     });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> dialogFormulario.close());

        // Meter los campos y botones adentro del Dialog
            FormLayout formulario = new FormLayout();
            formulario.add(txtTitulo, cbTipo, txtGenero, txtAnio, txtCreador, campoCalificacion, txtEstado);
        dialogFormulario.add(formulario);
        dialogFormulario.getFooter().add(btnCancelar, btnGuardar);

        // Crear la barra principal de la vista
        H2 titulo = new H2("Mi Colección");
        Button btnNuevo = new Button("+ Nuevo Elemento", e -> dialogFormulario.open());
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, btnNuevo);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(Alignment.CENTER);

        add(barraSuperior);
    }
}