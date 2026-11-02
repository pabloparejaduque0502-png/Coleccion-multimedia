package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.ListaPersonalizada;
import com.coleccion_multimedia.funciones.funciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.UploadHandler;

@Route(value = "listas", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mis Listas")
public class ListasView extends VerticalLayout {

    public FlexLayout contenedorTarjetas = new FlexLayout();
    public TextField txtBuscar = new TextField();

    public ListasView() {
        setWidthFull();
        setMinHeight("100%");
        getStyle().set("background-color", "#000000");

        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.getStyle().set("flex-wrap", "wrap");
        contenedorTarjetas.getStyle().set("gap", "20px");
        contenedorTarjetas.getStyle().set("align-items", "flex-start");

        // Buscador
        txtBuscar.setPlaceholder("Buscar listas por nombre o descripción...");
        txtBuscar.setMaxWidth("350px");
        txtBuscar.setWidthFull();
        txtBuscar.setClearButtonVisible(true);
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> cargarTarjetas());

        // Formulario
        Dialog dialogFormulario = new Dialog();
        dialogFormulario.setHeaderTitle("Crear Nueva Lista");

        TextField txtNombreLista = new TextField("Nombre de la Lista");
        TextArea txtDescripcion = new TextArea("Descripción (Opcional)");
        txtDescripcion.setPlaceholder("Ej. Mis películas favoritas de fin de semana...");

        MultiSelectComboBox<ElementoMultimedia> mcbElementos = new MultiSelectComboBox<>("Seleccionar Elementos");
        mcbElementos.setItems(funciones.leerElementos());
        mcbElementos.setItemLabelGenerator(e -> e.titulo + " (" + e.tipo + ")");
        mcbElementos.setPlaceholder("Selecciona uno o varios elementos...");
        mcbElementos.setWidthFull();

        String[] nombreArchivoSubido = new String[]{ funciones.PORTADA_LISTA };
        byte[][] bytesImagenSubida = new byte[1][];

        UploadHandler handlerImagen = UploadHandler.inMemory((metadata, data) -> {
            nombreArchivoSubido[0] = metadata.fileName();
            bytesImagenSubida[0] = data;
        });

        Upload uploadPortada = new Upload(handlerImagen);
        uploadPortada.setAcceptedFileExtensions(".jpg", ".jpeg", ".png");
        uploadPortada.setMaxFileSize(2 * 1024 * 1024);
        uploadPortada.setMaxFiles(1);

        Button btnGuardar = new Button("Guardar Lista", event -> {
            if (txtNombreLista.isEmpty()) {
                Notification.show("Por favor, ingresa un nombre para la lista.");
                return;
            }

            String nombreImagen = funciones.PORTADA_LISTA;
            if (bytesImagenSubida[0] != null) {
                nombreImagen = funciones.guardarPortada(bytesImagenSubida[0], nombreArchivoSubido[0]);
                if (nombreImagen == null) {
                    Notification.show("Error al guardar la imagen de la lista.");
                    return;
                }
            }

            // Se pasan los elementos elegidos a un arreglo con sus ids
            String[] idsElementos = new String[mcbElementos.getValue().size()];
            int idx = 0;
            for (ElementoMultimedia elemento : mcbElementos.getValue()) {
                idsElementos[idx] = elemento.id;
                idx++;
            }

            String id = String.valueOf(System.currentTimeMillis());
            ListaPersonalizada nueva = new ListaPersonalizada(id, txtNombreLista.getValue(), txtDescripcion.getValue(), nombreImagen, idsElementos);

            funciones.guardarLista(nueva);
            Notification.show("Lista creada correctamente.");

            txtNombreLista.clear();
            txtDescripcion.clear();
            mcbElementos.clear();
            uploadPortada.clearFileList();
            nombreArchivoSubido[0] = funciones.PORTADA_LISTA;
            bytesImagenSubida[0] = null;

            cargarTarjetas();
            dialogFormulario.close();
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCancelar = new Button("Cancelar", e -> dialogFormulario.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCancelar.getStyle().set("color", "#ff4d4d");

        FormLayout formulario = new FormLayout();
        formulario.add(txtNombreLista, txtDescripcion, mcbElementos, uploadPortada);
        formulario.setColspan(mcbElementos, 2);
        formulario.setColspan(uploadPortada, 2);

        dialogFormulario.add(formulario);
        dialogFormulario.getFooter().add(btnCancelar, btnGuardar);

        // Barra superior
        H2 titulo = new H2("Mis Listas");
        titulo.getStyle().set("color", "#ffffff");

        Button btnNueva = new Button("+ Nueva Lista", e -> dialogFormulario.open());
        btnNueva.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, txtBuscar, btnNueva);
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
        ListaPersonalizada[] listas = funciones.leerListas();
        int tarjetasMostradas = 0;

        for (int i = 0; i < listas.length; i++) {
            ListaPersonalizada l = listas[i];

            // Búsqueda por nombre o descripción
            if (!busqueda.isEmpty()) {
                boolean coincide = l.nombre.toLowerCase().contains(busqueda)
                                || l.descripcion.toLowerCase().contains(busqueda);
                if (!coincide) {
                    continue;
                }
            }

            contenedorTarjetas.add(crearTarjeta(l));
            tarjetasMostradas++;
        }

        if (tarjetasMostradas == 0) {
            String textoMensaje;
            if (!busqueda.isEmpty()) {
                textoMensaje = "No se encontraron listas que coincidan con '" + txtBuscar.getValue() + "'.";
            } else {
                textoMensaje = "Aún no has creado ninguna lista personalizada. Utiliza el botón '+ Nueva Lista'.";
            }

            Span msgVacio = new Span(textoMensaje);
            msgVacio.getStyle().set("color", "#aaaaaa");
            msgVacio.getStyle().set("font-style", "italic");
            msgVacio.getStyle().set("padding", "20px");
            contenedorTarjetas.add(msgVacio);
        }
    }

    public VerticalLayout crearTarjeta(ListaPersonalizada l) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.setWidthFull();
        tarjeta.setMaxWidth("320px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);
        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");

        Image imgPortada = new Image(DownloadHandler.forFile(funciones.archivoPortada(l.imagen, funciones.PORTADA_LISTA)), l.nombre);
        imgPortada.setWidth("100%");
        imgPortada.setHeight("160px");
        imgPortada.getStyle().set("object-fit", "cover");
        imgPortada.getStyle().set("border-radius", "8px");

        H4 lblNombre = new H4(l.nombre);
        lblNombre.getStyle().set("margin", "0");
        lblNombre.getStyle().set("font-size", "1.1rem");
        lblNombre.getStyle().set("color", "#ffffff");

        Span lblDesc = new Span(l.descripcion);
        lblDesc.getStyle().set("color", "#aaaaaa");
        lblDesc.getStyle().set("font-size", "0.85rem");

        tarjeta.add(imgPortada, lblNombre, lblDesc, crearDetalles(l));
        return tarjeta;
    }

    public Details crearDetalles(ListaPersonalizada l) {
        VerticalLayout layoutContenido = new VerticalLayout();
        layoutContenido.setPadding(false);
        layoutContenido.setSpacing(true);
        layoutContenido.getStyle().set("margin-top", "10px");

        // Elementos que tiene la lista, cada uno con su botón para quitarlo
        Span lblTituloContenido = new Span("Contenido de la lista (" + l.idsElementos.length + "):");
        lblTituloContenido.getStyle().set("color", "#ffffff");
        lblTituloContenido.getStyle().set("font-weight", "bold");
        lblTituloContenido.getStyle().set("font-size", "0.85rem");
        layoutContenido.add(lblTituloContenido);

        if (l.idsElementos.length == 0) {
            Span lblVacio = new Span("Esta lista no tiene elementos asignados.");
            lblVacio.getStyle().set("color", "#888888");
            lblVacio.getStyle().set("font-style", "italic");
            lblVacio.getStyle().set("font-size", "0.8rem");
            layoutContenido.add(lblVacio);
        }

        for (int i = 0; i < l.idsElementos.length; i++) {
            ElementoMultimedia elem = funciones.buscarElementoPorId(l.idsElementos[i]);
            if (elem == null) {
                continue;
            }

            Span lblItem = new Span("• " + elem.titulo + " (" + elem.tipo + ")");
            lblItem.getStyle().set("color", "#cccccc");
            lblItem.getStyle().set("font-size", "0.85rem");

            Button btnQuitar = new Button("✕", eventQuitar -> {
                Dialog dialogConfirmacion = new Dialog();
                dialogConfirmacion.setHeaderTitle("¿Quitar elemento de la lista?");

                Span msgConfirmacion = new Span("¿Estás seguro de que deseas quitar '" + elem.titulo + "' de la lista '" + l.nombre + "'?");

                Button btnConfirmar = new Button("Sí, quitar", eventSi -> {
                    funciones.quitarDeLista(l, elem.id);
                    funciones.actualizarLista(l);
                    Notification.show("'" + elem.titulo + "' fue eliminado de '" + l.nombre + "'.");
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
            btnQuitar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            btnQuitar.getStyle().set("color", "#ff4d4d");
            btnQuitar.getStyle().set("padding", "0 6px");
            btnQuitar.getStyle().set("min-width", "auto");
            btnQuitar.getStyle().set("height", "24px");

            HorizontalLayout filaItem = new HorizontalLayout(lblItem, btnQuitar);
            filaItem.setWidthFull();
            filaItem.setJustifyContentMode(JustifyContentMode.BETWEEN);
            filaItem.setAlignItems(Alignment.CENTER);
            layoutContenido.add(filaItem);
        }

        // Añadir más elementos a la lista
        MultiSelectComboBox<ElementoMultimedia> mcbAgregar = new MultiSelectComboBox<>("Añadir elementos a esta lista");
        mcbAgregar.setItems(funciones.leerElementos());
        mcbAgregar.setItemLabelGenerator(e -> e.titulo + " (" + e.tipo + ")");
        mcbAgregar.setPlaceholder("Selecciona uno o varios elementos...");
        mcbAgregar.setWidthFull();

        Button btnAgregar = new Button("+ Añadir", event -> {
            if (mcbAgregar.isEmpty()) {
                Notification.show("Selecciona al menos un elemento.");
                return;
            }

            // Primero se guardan solo los ids que todavía no están en la lista
            String[] idsNuevos = new String[mcbAgregar.getValue().size()];
            int cantidadNuevos = 0;
            for (ElementoMultimedia elemento : mcbAgregar.getValue()) {
                if (!funciones.listaContiene(l, elemento.id)) {
                    idsNuevos[cantidadNuevos] = elemento.id;
                    cantidadNuevos++;
                }
            }

            if (cantidadNuevos == 0) {
                Notification.show("Todos los elementos seleccionados ya forman parte de esta lista.");
                return;
            }

            // Como los arreglos tienen tamaño fijo, se crea uno más grande con los viejos y los nuevos
            String[] todosLosIds = new String[l.idsElementos.length + cantidadNuevos];
            for (int i = 0; i < l.idsElementos.length; i++) {
                todosLosIds[i] = l.idsElementos[i];
            }
            for (int i = 0; i < cantidadNuevos; i++) {
                todosLosIds[l.idsElementos.length + i] = idsNuevos[i];
            }
            l.idsElementos = todosLosIds;

            funciones.actualizarLista(l);
            Notification.show(cantidadNuevos + " elemento(s) añadido(s) a '" + l.nombre + "'.");
            cargarTarjetas();
        });
        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraAgregar = new HorizontalLayout(mcbAgregar, btnAgregar);
        barraAgregar.setWidthFull();
        barraAgregar.setAlignItems(Alignment.BASELINE);

        // Eliminar la lista completa
        Button btnEliminar = new Button("Eliminar Lista", eventDel -> {
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Eliminar lista?");

            Span msgConfirmacion = new Span("¿Estás seguro de que deseas eliminar '" + l.nombre + "'?");

            Button btnConfirmar = new Button("Sí, eliminar", eventSi -> {
                funciones.eliminarLista(l.id);
                Notification.show("Lista eliminada correctamente.");
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
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnEliminar.setWidthFull();

        layoutContenido.add(barraAgregar, btnEliminar);

        Span lblSummary = new Span("Ver contenido de la lista");
        lblSummary.getStyle().set("color", "#ffffff");
        lblSummary.getStyle().set("font-size", "0.85rem");
        lblSummary.getStyle().set("font-weight", "500");

        Details details = new Details(lblSummary, layoutContenido);
        details.getElement().getStyle().set("--lumo-secondary-text-color", "#ffffff");
        details.getStyle().set("width", "100%");
        return details;
    }
}
