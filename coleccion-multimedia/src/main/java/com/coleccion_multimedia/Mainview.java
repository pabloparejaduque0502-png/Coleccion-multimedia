package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.funciones.funciones;
import com.coleccion_multimedia.icon.Estrellas;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.UploadHandler;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mi Colección")
public class MainView extends VerticalLayout {

    public FlexLayout contenedorTarjetas = new FlexLayout();
    public TextField txtBuscar = new TextField();
    public Select<TipoMultimedia> filtroTipo = new Select<>();

    // Formulario (es el mismo para crear y para editar)
    public Dialog dialogFormulario = new Dialog();
    public TextField txtTitulo = new TextField("Título");
    public ComboBox<TipoMultimedia> cbTipo = new ComboBox<>("Tipo");
    public ComboBox<String> cbGenero = new ComboBox<>("Género");
    public IntegerField txtAnio = new IntegerField("Año");
    public TextField txtCreador = new TextField("Creador (Director/Artista)");
    public Estrellas ratingCalificacion = new Estrellas();
    public ComboBox<EstadoMultimedia> cbEstado = new ComboBox<>("Estado");
    public Upload uploadPortada;
    public String[] nombreArchivoSubido = new String[]{ funciones.PORTADA_ELEMENTO };
    public byte[][] bytesImagenSubida = new byte[1][];

    // Si es null se está creando un elemento, si tiene un id se está editando ese elemento
    public String idEditando = null;
    public String imagenActual = funciones.PORTADA_ELEMENTO;

    public MainView() {
        setSizeFull();
        getStyle().set("background-color", "#000000");

        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.getStyle().set("flex-wrap", "wrap");
        contenedorTarjetas.getStyle().set("gap", "20px");
        contenedorTarjetas.getStyle().set("align-items", "flex-start");

        // Buscador y filtro por tipo
        txtBuscar.setPlaceholder("Buscar por título, género, creador o año...");
        txtBuscar.setMaxWidth("600px");
        txtBuscar.setWidthFull();
        txtBuscar.setClearButtonVisible(true);
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> cargarTarjetas());

        filtroTipo.setItems(TipoMultimedia.values());
        filtroTipo.setPlaceholder("Todos los tipos");
        filtroTipo.setEmptySelectionAllowed(true);
        filtroTipo.setEmptySelectionCaption("Todos los tipos");
        filtroTipo.setWidth("200px");
        filtroTipo.getStyle().set("background-color", "#2a2a2a");
        filtroTipo.getStyle().set("border-radius", "8px");
        filtroTipo.addValueChangeListener(e -> cargarTarjetas());

        // Campos del formulario
        cbTipo.setItems(TipoMultimedia.values());
        cbEstado.setItems(EstadoMultimedia.values());
        
        // Sugiere los géneros que ya existen, pero deja escribir uno nuevo
        cbGenero.setAllowCustomValue(true);
        cbGenero.setPlaceholder("Elige uno o escribe uno nuevo");
        cbGenero.addCustomValueSetListener(e -> cbGenero.setValue(e.getDetail().trim()));

        Span lblCalificacion = new Span("Calificación");
        lblCalificacion.getStyle().set("font-size", "var(--lumo-font-size-s)");
        lblCalificacion.getStyle().set("color", "var(--lumo-secondary-text-color)");
        lblCalificacion.getStyle().set("font-weight", "500");

        VerticalLayout campoCalificacion = new VerticalLayout(lblCalificacion, ratingCalificacion);
        campoCalificacion.setPadding(false);
        campoCalificacion.setSpacing(false);

        UploadHandler handlerImagen = UploadHandler.inMemory((metadata, data) -> {
            nombreArchivoSubido[0] = metadata.fileName();
            bytesImagenSubida[0] = data;
        });

        uploadPortada = new Upload(handlerImagen);
        uploadPortada.setAcceptedFileExtensions(".jpg", ".jpeg", ".png");
        uploadPortada.setMaxFileSize(2 * 1024 * 1024);
        uploadPortada.setMaxFiles(1);

        Button btnGuardar = new Button("Guardar", event -> guardarElemento());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCancelar = new Button("Cancelar", e -> dialogFormulario.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCancelar.getStyle().set("color", "#ff4d4d");

        FormLayout formulario = new FormLayout();
        formulario.add(txtTitulo, cbTipo, cbGenero, txtAnio, txtCreador, campoCalificacion, cbEstado, uploadPortada);
        formulario.setColspan(uploadPortada, 2);

        dialogFormulario.add(formulario);
        dialogFormulario.getFooter().add(btnCancelar, btnGuardar);

        // Barra superior
        H2 titulo = new H2("Mi Colección");
        titulo.getStyle().set("color", "#ffffff");

        Button btnNuevo = new Button("+ Nuevo Elemento", e -> {
            limpiarFormulario();
            dialogFormulario.setHeaderTitle("Agregar Nuevo Elemento");
            dialogFormulario.open();
        });
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, txtBuscar, filtroTipo, btnNuevo);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(Alignment.CENTER);
        barraSuperior.getStyle().set("flex-wrap", "wrap");

        add(barraSuperior, contenedorTarjetas);
        cargarTarjetas();
    }

    public void guardarElemento() {
        if (txtTitulo.isEmpty() || cbTipo.getValue() == null || cbGenero.isEmpty() ||
            txtAnio.isEmpty() || txtCreador.isEmpty() || ratingCalificacion.valor == 0 || cbEstado.getValue() == null) {
            Notification.show("Por favor, completa todos los campos del formulario.");
            return;
        }

        int anio = txtAnio.getValue();
        if (anio < 1800 || anio > 2026) {
            Notification.show("Por favor, ingresa un año válido.");
            return;
        }

        // Si no se subió una imagen nueva, se conserva la que ya tenía
        String nombreImagen = imagenActual;
        if (bytesImagenSubida[0] != null) {
            nombreImagen = funciones.guardarPortada(bytesImagenSubida[0], nombreArchivoSubido[0]);
            if (nombreImagen == null) {
                Notification.show("Error al guardar el archivo de la imagen.");
                return;
            }
        }

        if (idEditando == null) {
            String id = String.valueOf(System.currentTimeMillis());
            ElementoMultimedia nuevo = new ElementoMultimedia(id, txtTitulo.getValue(), cbTipo.getValue(),
                cbGenero.getValue(), anio, txtCreador.getValue(), ratingCalificacion.valor, cbEstado.getValue(), nombreImagen);

            funciones.guardarElemento(nuevo);
            Notification.show("¡Elemento guardado en el archivo!");
        } else {
            // Si se cambió la portada, la vieja se borra para no dejar imágenes sin usar
            if (!nombreImagen.equals(imagenActual)) {
                funciones.borrarPortada(imagenActual);
            }

            ElementoMultimedia editado = new ElementoMultimedia(idEditando, txtTitulo.getValue(), cbTipo.getValue(),
                cbGenero.getValue(), anio, txtCreador.getValue(), ratingCalificacion.valor, cbEstado.getValue(), nombreImagen);

            funciones.actualizarElemento(editado);
            Notification.show("¡Elemento actualizado!");
        }

        cargarTarjetas();
        limpiarFormulario();
        dialogFormulario.close();
    }

    public void limpiarFormulario() {
        txtTitulo.clear();
        cbTipo.clear();
        cbGenero.clear();
        txtAnio.clear();
        txtCreador.clear();
        ratingCalificacion.limpiar();
        cbEstado.clear();
        cbGenero.setItems(funciones.generosUnicos(funciones.leerElementos()));
        uploadPortada.clearFileList();

        nombreArchivoSubido[0] = funciones.PORTADA_ELEMENTO;
        bytesImagenSubida[0] = null;

        idEditando = null;
        imagenActual = funciones.PORTADA_ELEMENTO;
    }

    public void cargarTarjetas() {
        contenedorTarjetas.removeAll();

        String busqueda = txtBuscar.getValue().toLowerCase().trim();
        ElementoMultimedia[] elementos = funciones.leerElementos();
        int tarjetasMostradas = 0;

        for (int i = 0; i < elementos.length; i++) {
            ElementoMultimedia e = elementos[i];

            // Filtro por tipo
            if (filtroTipo.getValue() != null && e.tipo != filtroTipo.getValue()) {
                continue;
            }

            // Búsqueda por título, género, creador o año
            if (!busqueda.isEmpty()) {
                boolean coincide = e.titulo.toLowerCase().contains(busqueda)
                                || e.genero.toLowerCase().contains(busqueda)
                                || e.creador.toLowerCase().contains(busqueda)
                                || String.valueOf(e.anio).contains(busqueda);
                if (!coincide) {
                    continue;
                }
            }

            contenedorTarjetas.add(crearTarjeta(e));
            tarjetasMostradas++;
        }

        if (tarjetasMostradas == 0) {
            String textoMensaje;
            if (filtroTipo.getValue() != null || !busqueda.isEmpty()) {
                textoMensaje = "No se encontraron resultados con esa búsqueda o filtro.";
            } else {
                textoMensaje = "Tu colección está vacía. Agrega tu primer elemento con el botón '+ Nuevo Elemento'.";
            }

            Span msgVacio = new Span(textoMensaje);
            msgVacio.getStyle().set("color", "#aaaaaa");
            msgVacio.getStyle().set("font-style", "italic");
            msgVacio.getStyle().set("padding", "20px");
            contenedorTarjetas.add(msgVacio);
        }
    }

    public VerticalLayout crearTarjeta(ElementoMultimedia e) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.setWidthFull();
        tarjeta.setMaxWidth("240px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);
        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");
        tarjeta.getStyle().set("cursor", "pointer");

        Image imgPortada = new Image(DownloadHandler.forFile(funciones.archivoPortada(e.imagen, funciones.PORTADA_ELEMENTO)), e.titulo);
        imgPortada.setWidth("100%");
        imgPortada.setHeight("200px");
        imgPortada.getStyle().set("object-fit", "cover");
        imgPortada.getStyle().set("border-radius", "8px");

        H4 lblTitulo = new H4(e.titulo);
        lblTitulo.getStyle().set("margin", "0");
        lblTitulo.getStyle().set("font-size", "1rem");
        lblTitulo.getStyle().set("color", "#ffffff");

        Estrellas estrellasLectura = new Estrellas(e.calificacion, false);

        tarjeta.add(imgPortada, lblTitulo, estrellasLectura, crearDetalles(e));
        return tarjeta;
    }

    public Details crearDetalles(ElementoMultimedia e) {
        Span lblDetalles = new Span("Tipo: " + e.tipo + "  |  Género: " + e.genero);
        lblDetalles.getStyle().set("color", "#e0e0e0");
        lblDetalles.getStyle().set("font-size", "0.85rem");

        Span lblCreadorAnio = new Span("Creador: " + e.creador + "  |  Año: " + e.anio);
        lblCreadorAnio.getStyle().set("color", "#cccccc");
        lblCreadorAnio.getStyle().set("font-size", "0.85rem");

        Span lblEstado = new Span("Estado actual: " + e.estado);
        lblEstado.getStyle().set("font-weight", "bold");
        lblEstado.getStyle().set("color", "#ffffff");
        lblEstado.getStyle().set("font-size", "0.85rem");

        // Editar: llena el formulario con los datos del elemento y lo abre
        Button btnEditar = new Button("Editar", eventEdit -> {
            limpiarFormulario();

            txtTitulo.setValue(e.titulo);
            cbTipo.setValue(e.tipo);
            cbGenero.setValue(e.genero);
            txtAnio.setValue(e.anio);
            txtCreador.setValue(e.creador);
            ratingCalificacion.cambiarPuntuacion(e.calificacion);
            cbEstado.setValue(e.estado);

            idEditando = e.id;
            imagenActual = e.imagen;

            dialogFormulario.setHeaderTitle("Editar Elemento");
            dialogFormulario.open();
        });
        btnEditar.getStyle().set("color", "#ff4d4d");
        btnEditar.getStyle().set("background-color", "#2a2a2a");
        btnEditar.setWidthFull();

        // Eliminar: pide confirmación antes de borrar
        Button btnEliminar = new Button("Eliminar", eventDel -> {
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Eliminar elemento?");

            Span msgConfirmacion = new Span("¿Estás seguro de que deseas eliminar '" + e.titulo + "'? Esta acción no se puede deshacer.");

            Button btnConfirmar = new Button("Sí, eliminar", eventSi -> {
                funciones.eliminarElemento(e.id);
                Notification.show("Elemento eliminado de tu colección.");
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

        VerticalLayout layoutContenido = new VerticalLayout(lblDetalles, lblCreadorAnio, lblEstado, btnEditar, btnEliminar);
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
