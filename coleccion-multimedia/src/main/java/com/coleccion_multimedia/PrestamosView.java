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

        // 1. Leer los elementos multimedia almacenados en el archivo plano
        ElementoMultimedia[] elementosDisponibles = funciones.leerElementos();
        ComboBox<ElementoMultimedia> cbElemento = new ComboBox<>("Elemento Multimedia");
        cbElemento.setItems(elementosDisponibles);
        cbElemento.setItemLabelGenerator(e -> e.titulo + " (" + e.tipo + ")");
        cbElemento.setWidthFull();
        TextField txtPrestadoA = new TextField("Prestado a (Persona)");

        // 1. Componentes DatePicker para manejo de fechas
        DatePicker dpFechaPrestamo = new DatePicker("Fecha de préstamo");
        dpFechaPrestamo.setValue(LocalDate.now()); // Valor por defecto: fecha actual

        DatePicker dpFechaDevolucion = new DatePicker("Fecha acordada de devolución (Opcional)");

        Button btnGuardar = new Button("Guardar", event -> {
    if (cbElemento.isEmpty() || txtPrestadoA.isEmpty() || dpFechaPrestamo.isEmpty()) {
        Notification.show("Por favor, completa los campos obligatorios.");
        return;
    }

        // 1. Generar ID único para el préstamo basado en timestamp
        String idPrestamo = String.valueOf(System.currentTimeMillis());
        String idElemento = cbElemento.getValue().id;
        String persona = txtPrestadoA.getValue().trim();
        String fechaPrestamo = dpFechaPrestamo.getValue().toString();
        LocalDate fechaDev = dpFechaDevolucion.getValue();
        String fechaDevolucionStr = (fechaDev != null) ? fechaDev.toString() : "Sin fecha";

        // 2. Instanciar y guardar en prestamos.txt
        Prestamo nuevoPrestamo = new Prestamo(idPrestamo, idElemento, persona, fechaPrestamo, fechaDevolucionStr);
        funciones.guardarPrestamo(nuevoPrestamo);

        Notification.show("Préstamo registrado correctamente.");

        // 3. Limpiar campos y refrescar interfaz
        cbElemento.clear();
        txtPrestadoA.clear();
        dpFechaPrestamo.setValue(LocalDate.now());
        dpFechaDevolucion.clear();
        dialogFormulario.close();

        cargarTarjetasPrestamo(txtBuscar.getValue());
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

        // 1. Leer préstamos reales desde el archivo plano
        Prestamo[] prestamos = funciones.leerPrestamos();
        int tarjetasMostradas = 0;

        for (int i = 0; i < prestamos.length; i++) {
            Prestamo p = prestamos[i];
            if (p == null) continue;

            // 2. Vincular el préstamo con su ElementoMultimedia
            ElementoMultimedia elemento = funciones.buscarElementoPorId(p.idElemento);
            if (elemento == null) continue; // Si el elemento original fue eliminado, se omite

            // 3. Filtrar por título del elemento o por nombre de la persona
            if (filtro != null && !filtro.trim().isEmpty()) {
                String busqueda = filtro.toLowerCase().trim();
                String tituloLower = elemento.titulo.toLowerCase();
                String personaLower = p.persona.toLowerCase();

                if (!tituloLower.contains(busqueda) && !personaLower.contains(busqueda)) {
                    continue;
                }
            }

            // 4. Crear y añadir la tarjeta a la cuadrícula
            VerticalLayout tarjeta = crearTarjetaPrestamo(p, elemento);
            contenedorTarjetas.add(tarjeta);
            tarjetasMostradas++;
        }

        // Mensaje en caso de no haber registros
        if (tarjetasMostradas == 0) {
            String textoMensaje = (filtro != null && !filtro.trim().isEmpty())
                ? "No se encontraron préstamos que coincidan con '" + filtro + "'."
                : "No hay préstamos activos registrados actualmente.";

            Span msgVacio = new Span(textoMensaje);
            msgVacio.getStyle().set("color", "#aaaaaa");
            msgVacio.getStyle().set("font-style", "italic");
            msgVacio.getStyle().set("padding", "20px");
            contenedorTarjetas.add(msgVacio);
        }
    }
    public VerticalLayout crearTarjetaPrestamo(Prestamo p, ElementoMultimedia e) {
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

        // Ficha desplegable con los datos reales del préstamo
        Details ficha = crearDetallesPrestamo(p, e);

        tarjeta.add(imgPortada, lblTitulo, ficha);

        return tarjeta;
    }

    public Details crearDetallesPrestamo(Prestamo p, ElementoMultimedia e) {
        Span lblPrestadoA = new Span("Prestado a: " + p.persona);
        lblPrestadoA.getStyle().set("color", "#e0e0e0");
        lblPrestadoA.getStyle().set("font-size", "0.85rem");

        Span lblFechaPrestamo = new Span("Fecha préstamo: " + p.fechaPrestamo);
        lblFechaPrestamo.getStyle().set("color", "#cccccc");
        lblFechaPrestamo.getStyle().set("font-size", "0.85rem");

        Span lblFechaDevolucion = new Span("Devolución acordada: " + p.fechaDevolucion);
        lblFechaDevolucion.getStyle().set("color", "#aaaaaa");
        lblFechaDevolucion.getStyle().set("font-size", "0.85rem");

        // Botón de devolución con diálogo de confirmación
        Button btnDevolver = new Button("Marcar Devuelto", eventDev -> {
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Confirmar devolución?");

            Span msgConfirmacion = new Span("¿Deseas marcar como devuelto '" + e.titulo + "' prestado a " + p.persona + "?");

            Button btnConfirmarDevolucion = new Button("Sí, marcar devuelto", eventSi -> {
                // Elimina el préstamo de prestamos.txt y recarga las tarjetas
                funciones.eliminarPrestamo(p.id);
                Notification.show("Elemento devuelto e historia de préstamo actualizada.");
                cargarTarjetasPrestamo(txtBuscar.getValue());
                dialogConfirmacion.close();
            });
            btnConfirmarDevolucion.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            Button btnCancelarConf = new Button("Cancelar", eventNo -> dialogConfirmacion.close());
            btnCancelarConf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnCancelarConf.getStyle().set("color", "#ff4d4d");

            dialogConfirmacion.add(msgConfirmacion);
            dialogConfirmacion.getFooter().add(btnCancelarConf, btnConfirmarDevolucion);
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