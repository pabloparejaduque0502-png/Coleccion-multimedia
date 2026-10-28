package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
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

import java.io.File;
import java.time.LocalDate;

@Route(value = "prestamos", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Préstamos")
public class PrestamosView extends VerticalLayout {

    public FlexLayout contenedorTarjetas = new FlexLayout();
    public TextField txtBuscar = new TextField();

    public PrestamosView() {
        setClassName("prestamos-view");
        setSizeFull();
        getStyle().set("background-color", "#000000");

        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.getStyle().set("flex-wrap", "wrap");
        contenedorTarjetas.getStyle().set("gap", "20px");

        txtBuscar.setPlaceholder("Buscar por elemento, prestado a...");
        txtBuscar.setWidth("350px");
        txtBuscar.setClearButtonVisible(true);
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> cargarTarjetasPrestamo(e.getValue()));

        Dialog dialogFormulario = new Dialog();
        dialogFormulario.setHeaderTitle("Registrar Nuevo Préstamo");

        ComboBox<ElementoMultimedia> cbElemento = new ComboBox<>("Elemento Multimedia");
        TextField txtPrestadoA = new TextField("Prestado a (Persona)");

        // 1. Componentes DatePicker para manejo de fechas
        DatePicker dpFechaPrestamo = new DatePicker("Fecha de préstamo");
        dpFechaPrestamo.setValue(LocalDate.now()); // Valor por defecto: fecha actual

        DatePicker dpFechaDevolucion = new DatePicker("Fecha acordada de devolución (Opcional)");

        Button btnGuardar = new Button("Guardar", event -> {
            // 2. Validación obligatoria que EXCLUYE dpFechaDevolucion
            if (cbElemento.isEmpty() || txtPrestadoA.isEmpty() || dpFechaPrestamo.isEmpty()) {
                Notification.show("Por favor, completa los campos obligatorios.");
                return;
            }

            // Lectura de la fecha opcional (puede ser null)
            LocalDate fechaDevolucion = dpFechaDevolucion.getValue();
            String textoDevolucion = (fechaDevolucion != null) ? fechaDevolucion.toString() : "Sin fecha límite";

            Notification.show("Préstamo registrado correctamente.");
            cargarTarjetasPrestamo(txtBuscar.getValue());

            cbElemento.clear();
            txtPrestadoA.clear();
            dpFechaPrestamo.setValue(LocalDate.now());
            dpFechaDevolucion.clear();
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

        H2 titulo = new H2("Préstamos");
        titulo.getStyle().set("color", "#ffffff");

        Button btnNuevo = new Button("+ Nuevo Préstamo", e -> dialogFormulario.open());
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, txtBuscar, btnNuevo);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(Alignment.CENTER);

        add(barraSuperior, contenedorTarjetas);
        cargarTarjetasPrestamo("");
    }

    public void cargarTarjetasPrestamo(String filtro) {
        contenedorTarjetas.removeAll();

        ElementoMultimedia[] elementos = funciones.leerElementos();
        int tarjetasMostradas = 0;

        for (int i = 0; i < elementos.length; i++) {
            ElementoMultimedia e = elementos[i];

            if (e == null) {
                continue;
            }

            if (filtro != null && !filtro.trim().isEmpty()) {
                String busqueda = filtro.toLowerCase().trim();
                String tituloLower = e.titulo.toLowerCase();

                if (!tituloLower.contains(busqueda)) {
                    continue;
                }
            }

            VerticalLayout tarjeta = crearTarjetaPrestamo(e);
            contenedorTarjetas.add(tarjeta);
            tarjetasMostradas++;
        }

        if (tarjetasMostradas == 0) {
            String textoMensaje = (filtro != null && !filtro.trim().isEmpty())
                ? "No se encontraron préstamos para '" + filtro + "'."
                : "No hay préstamos registrados actualmente.";

            Span msgVacio = new Span(textoMensaje);
            msgVacio.getStyle().set("color", "#aaaaaa");
            msgVacio.getStyle().set("font-style", "italic");
            msgVacio.getStyle().set("padding", "20px");
            contenedorTarjetas.add(msgVacio);
        }
    }

    public VerticalLayout crearTarjetaPrestamo(ElementoMultimedia e) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.setWidth("240px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);

        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");

        File archivo = new File("portadas", e.imagen);
        if (!archivo.exists()) {
            archivo = new File("portadas", "default.png");
        }

        DownloadHandler handlerImagen = DownloadHandler.forFile(archivo);
        Image imgPortada = new Image(handlerImagen, e.titulo);
        imgPortada.setWidth("100%");
        imgPortada.setHeight("200px");
        imgPortada.getStyle().set("object-fit", "cover");
        imgPortada.getStyle().set("border-radius", "8px");

        H4 lblTitulo = new H4(e.titulo);
        lblTitulo.getStyle().set("margin", "0");
        lblTitulo.getStyle().set("font-size", "1rem");
        lblTitulo.getStyle().set("color", "#ffffff");

        Details ficha = crearDetallesPrestamo(e);

        tarjeta.add(imgPortada, lblTitulo, ficha);

        return tarjeta;
    }

    public Details crearDetallesPrestamo(ElementoMultimedia e) {
        Span lblPrestadoA = new Span("Prestado a: Usuario de prueba");
        lblPrestadoA.getStyle().set("color", "#e0e0e0");
        lblPrestadoA.getStyle().set("font-size", "0.85rem");

        Span lblFechaPrestamo = new Span("Fecha préstamo: " + LocalDate.now().toString());
        lblFechaPrestamo.getStyle().set("color", "#cccccc");
        lblFechaPrestamo.getStyle().set("font-size", "0.85rem");

        Span lblFechaDevolucion = new Span("Devolución acordada: Pendiente / Sin fecha");
        lblFechaDevolucion.getStyle().set("color", "#aaaaaa");
        lblFechaDevolucion.getStyle().set("font-size", "0.85rem");

        Button btnDevolver = new Button("Marcar Devuelto", eventDev -> {
            Notification.show("Elemento marcado como devuelto.");
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