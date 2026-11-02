package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.Prestamo;
import com.coleccion_multimedia.funciones.funciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.time.LocalDate;

@Route(value = "prestamos", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Préstamos")
public class PrestamosView extends VerticalLayout {

    public FlexLayout contenedorTarjetas = new FlexLayout();
    public TextField txtBuscar = new TextField();

    public PrestamosView() {
        setWidthFull();
        setMinHeight("100%");
        getStyle().set("background-color", "#000000");

        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.getStyle().set("flex-wrap", "wrap");
        contenedorTarjetas.getStyle().set("gap", "20px");
        contenedorTarjetas.getStyle().set("align-items", "flex-start");

        // Buscador
        txtBuscar.setPlaceholder("Buscar por elemento, prestado a...");
        txtBuscar.setMaxWidth("350px");
        txtBuscar.setWidthFull();
        txtBuscar.setClearButtonVisible(true);
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> cargarTarjetas());

        // Formulario
        Dialog dialogFormulario = new Dialog();
        dialogFormulario.setHeaderTitle("Registrar Nuevo Préstamo");

        ComboBox<ElementoMultimedia> cbElemento = new ComboBox<>("Elemento Multimedia");
        cbElemento.setItems(funciones.leerElementos());
        cbElemento.setItemLabelGenerator(e -> e.titulo + " (" + e.tipo + ")");
        cbElemento.setWidthFull();

        TextField txtPrestadoA = new TextField("Prestado a (Persona)");

        DatePicker dpFechaPrestamo = new DatePicker("Fecha de préstamo");
        dpFechaPrestamo.setValue(LocalDate.now());

        DatePicker dpFechaDevolucion = new DatePicker("Fecha acordada de devolución (Opcional)");

        Button btnGuardar = new Button("Guardar", event -> {
            if (cbElemento.isEmpty() || txtPrestadoA.isEmpty() || dpFechaPrestamo.isEmpty()) {
                Notification.show("Por favor, completa los campos obligatorios.");
                return;
            }
            if (funciones.estaPrestado(cbElemento.getValue().id)) {
                Notification.show("Este elemento ya se encuentra prestado actualmente.");
                return;
            }
            if (dpFechaDevolucion.getValue() != null && dpFechaDevolucion.getValue().isBefore(dpFechaPrestamo.getValue())) {
                Notification.show("La fecha de devolución no puede ser anterior a la fecha de préstamo.");
                return;
            }

            String id = String.valueOf(System.currentTimeMillis());
            String fechaDevolucion = "Sin fecha";
            if (dpFechaDevolucion.getValue() != null) {
                fechaDevolucion = dpFechaDevolucion.getValue().toString();
            }

            Prestamo nuevo = new Prestamo(id, cbElemento.getValue().id, txtPrestadoA.getValue().trim(),
                dpFechaPrestamo.getValue().toString(), fechaDevolucion);

            funciones.guardarPrestamo(nuevo);
            Notification.show("Préstamo registrado correctamente.");

            cbElemento.clear();
            txtPrestadoA.clear();
            dpFechaPrestamo.setValue(LocalDate.now());
            dpFechaDevolucion.clear();

            cargarTarjetas();
            dialogFormulario.close();
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCancelar = new Button("Cancelar", e -> dialogFormulario.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCancelar.getStyle().set("color", "#ff4d4d");

        FormLayout formulario = new FormLayout();
        formulario.add(cbElemento, txtPrestadoA, dpFechaPrestamo, dpFechaDevolucion);

        dialogFormulario.add(formulario);
        dialogFormulario.getFooter().add(btnCancelar, btnGuardar);

        // Barra superior
        H2 titulo = new H2("Préstamos");
        titulo.getStyle().set("color", "#ffffff");

        Button btnNuevo = new Button("+ Nuevo Préstamo", e -> dialogFormulario.open());
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, txtBuscar, btnNuevo);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(Alignment.CENTER);
        barraSuperior.getStyle().set("flex-wrap", "wrap");

        add(barraSuperior, contenedorTarjetas);
        cargarTarjetas();
    }

    public void cargarTarjetas() {
        contenedorTarjetas.removeAll();

        String busqueda = txtBuscar.getValue().toLowerCase().trim();
        Prestamo[] prestamos = funciones.leerPrestamos();
        int tarjetasMostradas = 0;

        for (int i = 0; i < prestamos.length; i++) {
            Prestamo p = prestamos[i];

            // El préstamo solo guarda el id del elemento, así que se busca para tener su título y portada
            ElementoMultimedia e = funciones.buscarElementoPorId(p.idElemento);
            if (e == null) {
                continue;
            }

            // Búsqueda por título del elemento o por la persona
            if (!busqueda.isEmpty()) {
                boolean coincide = e.titulo.toLowerCase().contains(busqueda)
                                || p.persona.toLowerCase().contains(busqueda);
                if (!coincide) {
                    continue;
                }
            }

            contenedorTarjetas.add(crearTarjeta(p, e));
            tarjetasMostradas++;
        }

        if (tarjetasMostradas == 0) {
            String textoMensaje;
            if (!busqueda.isEmpty()) {
                textoMensaje = "No se encontraron préstamos que coincidan con '" + txtBuscar.getValue() + "'.";
            } else {
                textoMensaje = "No hay préstamos activos registrados actualmente.";
            }

            Span msgVacio = new Span(textoMensaje);
            msgVacio.getStyle().set("color", "#aaaaaa");
            msgVacio.getStyle().set("font-style", "italic");
            msgVacio.getStyle().set("padding", "20px");
            contenedorTarjetas.add(msgVacio);
        }
    }

    public VerticalLayout crearTarjeta(Prestamo p, ElementoMultimedia e) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.setWidthFull();
        tarjeta.setMaxWidth("240px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);
        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");

        Image imgPortada = new Image(DownloadHandler.forFile(funciones.archivoPortada(e.imagen, funciones.PORTADA_ELEMENTO)), e.titulo);
        imgPortada.setWidth("100%");
        imgPortada.setHeight("200px");
        imgPortada.getStyle().set("object-fit", "cover");
        imgPortada.getStyle().set("border-radius", "8px");

        H4 lblTitulo = new H4(e.titulo);
        lblTitulo.getStyle().set("margin", "0");
        lblTitulo.getStyle().set("font-size", "1rem");
        lblTitulo.getStyle().set("color", "#ffffff");

        tarjeta.add(imgPortada, lblTitulo, crearDetalles(p, e));
        return tarjeta;
    }

    public Details crearDetalles(Prestamo p, ElementoMultimedia e) {
        Span lblPrestadoA = new Span("Prestado a: " + p.persona);
        lblPrestadoA.getStyle().set("color", "#e0e0e0");
        lblPrestadoA.getStyle().set("font-size", "0.85rem");

        Span lblFechaPrestamo = new Span("Fecha préstamo: " + p.fechaPrestamo);
        lblFechaPrestamo.getStyle().set("color", "#cccccc");
        lblFechaPrestamo.getStyle().set("font-size", "0.85rem");

        Span lblFechaDevolucion = new Span("Devolución acordada: " + p.fechaDevolucion);
        lblFechaDevolucion.getStyle().set("color", "#aaaaaa");
        lblFechaDevolucion.getStyle().set("font-size", "0.85rem");

        // Marcar devuelto: pide confirmación y borra el préstamo
        Button btnDevolver = new Button("Marcar Devuelto", eventDev -> {
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Confirmar devolución?");

            Span msgConfirmacion = new Span("¿Deseas marcar como devuelto '" + e.titulo + "' prestado a " + p.persona + "?");

            Button btnConfirmar = new Button("Sí, marcar devuelto", eventSi -> {
                funciones.eliminarPrestamo(p.id);
                Notification.show("Elemento devuelto e historia de préstamo actualizada.");
                cargarTarjetas();
                dialogConfirmacion.close();
            });
            btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            Button btnCancelarConf = new Button("Cancelar", eventNo -> dialogConfirmacion.close());
            btnCancelarConf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnCancelarConf.getStyle().set("color", "#ff4d4d");

            dialogConfirmacion.add(msgConfirmacion);
            dialogConfirmacion.getFooter().add(btnCancelarConf, btnConfirmar);
            dialogConfirmacion.open();
        });
        btnDevolver.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnDevolver.setWidthFull();

        VerticalLayout layoutContenido = new VerticalLayout(lblPrestadoA, lblFechaPrestamo, lblFechaDevolucion, btnDevolver);
        layoutContenido.setPadding(false);
        layoutContenido.setSpacing(true);
        layoutContenido.getStyle().set("margin-top", "10px");

        Span lblSummary = new Span("Ver detalles");
        lblSummary.getStyle().set("color", "#ffffff");
        lblSummary.getStyle().set("font-size", "0.85rem");
        lblSummary.getStyle().set("font-weight", "500");

        Details details = new Details(lblSummary, layoutContenido);
        details.getElement().getStyle().set("--lumo-secondary-text-color", "#ffffff");
        details.getStyle().set("width", "100%");
        return details;
    }
}
