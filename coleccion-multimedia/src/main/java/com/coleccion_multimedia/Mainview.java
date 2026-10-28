package com.coleccion_multimedia;

import com.coleccion_multimedia.clases.ElementoMultimedia;
import com.coleccion_multimedia.clases.TipoMultimedia;
import com.coleccion_multimedia.clases.EstadoMultimedia;
import com.coleccion_multimedia.funciones.funciones;
import com.coleccion_multimedia.icon.Estrellas;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.IntegerField; 
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.DownloadHandler;
import java.io.File;
import java.io.FileOutputStream;
import com.vaadin.flow.component.details.Details;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mi Colección")
public class MainView extends VerticalLayout {

    public FlexLayout contenedorTarjetas = new FlexLayout();
    public TextField txtBuscar = new TextField();

    public MainView() {
        setClassName("main-view");
        setSizeFull();
        getStyle().set("background-color", "#000000");

        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.getStyle().set("flex-wrap", "wrap");
        contenedorTarjetas.getStyle().set("gap", "20px");

        txtBuscar.setPlaceholder("Buscar por título, género, creador o año...");
        txtBuscar.setWidth("600px");
        txtBuscar.setClearButtonVisible(true);
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> cargarTarjetas(e.getValue()));

        Dialog dialogFormulario = new Dialog();
        dialogFormulario.setHeaderTitle("Agregar Nuevo Elemento");

        TextField txtTitulo = new TextField("Título");
        ComboBox<TipoMultimedia> cbTipo = new ComboBox<>("Tipo");
        cbTipo.setItems(TipoMultimedia.values());

        TextField txtGenero = new TextField("Género");
        IntegerField txtAnio = new IntegerField("Año"); 
        TextField txtCreador = new TextField("Creador (Director/Artista)");
        
        Estrellas ratingCalificacion = new Estrellas();
        Span lblCalificacion = new Span("Calificación");
        lblCalificacion.getStyle().set("font-size", "var(--lumo-font-size-s)");
        lblCalificacion.getStyle().set("color", "var(--lumo-secondary-text-color)");
        lblCalificacion.getStyle().set("font-weight", "500");

        VerticalLayout campoCalificacion = new VerticalLayout(lblCalificacion, ratingCalificacion);
        campoCalificacion.setPadding(false);
        campoCalificacion.setSpacing(false);
        
        ComboBox<EstadoMultimedia> cbEstado = new ComboBox<>("Estado");
        cbEstado.setItems(EstadoMultimedia.values());

        final String[] nombreArchivoSubido = new String[]{ "default.png" };
        final byte[][] bytesImagenSubida = new byte[1][];

        UploadHandler handlerImagen = UploadHandler.inMemory((metadata, data) -> {
            nombreArchivoSubido[0] = metadata.fileName();
            bytesImagenSubida[0] = data;
        });

        Upload uploadPortada = new Upload(handlerImagen);
        
        String[] extensionesPermitidas = new String[]{ ".jpg", ".jpeg", ".png" };
        uploadPortada.setAcceptedFileExtensions(extensionesPermitidas);
        uploadPortada.setMaxFileSize(2 * 1024 * 1024);
        uploadPortada.setMaxFiles(1);

        Button btnGuardar = new Button("Guardar", event -> {

            if (txtTitulo.isEmpty() || cbTipo.getValue() == null || txtGenero.isEmpty() || 
                txtAnio.isEmpty() || txtCreador.isEmpty() || ratingCalificacion.valor == 0 || cbEstado.getValue() == null) {
                Notification.show("Por favor, completa todos los campos del formulario.");
                return;
            }

            int anio = txtAnio.getValue();
            int calificacion = ratingCalificacion.valor;

            if (anio < 1800 || anio > 2026) {
                Notification.show("Por favor, ingresa un año válido.");
                return;
            }

            String nombreImagenFinal = "default.png";

            if (bytesImagenSubida[0] != null && bytesImagenSubida[0].length > 0) {
                try {
                    File carpetaPortadas = new File("portadas");
                    if (!carpetaPortadas.exists()) {
                        carpetaPortadas.mkdirs();
                    }

                    String nombreOriginal = nombreArchivoSubido[0];
                    nombreImagenFinal = System.currentTimeMillis() + "_" + nombreOriginal;

                    File archivoDestino = new File(carpetaPortadas, nombreImagenFinal);
                    FileOutputStream output = new FileOutputStream(archivoDestino);
                    output.write(bytesImagenSubida[0]);
                    output.close();

                } catch (Exception ex) {
                    Notification.show("Error al guardar el archivo de la imagen.");
                }
            }

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
                cbEstado.getValue(),
                nombreImagenFinal
            );

            funciones.guardarElemento(nuevo);
            Notification.show("¡Elemento guardado en el archivo!");

            cargarTarjetas();

            txtTitulo.clear();
            cbTipo.clear();
            txtGenero.clear();
            txtAnio.clear();
            txtCreador.clear();
            ratingCalificacion.limpiar();
            cbEstado.clear();
            uploadPortada.clearFileList();
            
            nombreArchivoSubido[0] = "default.png";
            bytesImagenSubida[0] = null;

            dialogFormulario.close();
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // Botón Cancelar en estilo terciario con texto en ROJO
        Button btnCancelar = new Button("Cancelar", e -> dialogFormulario.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCancelar.getStyle().set("color", "#ff4d4d");

        FormLayout formulario = new FormLayout();
        formulario.add(txtTitulo, cbTipo, txtGenero, txtAnio, txtCreador, campoCalificacion, cbEstado, uploadPortada);
        formulario.setColspan(uploadPortada, 2);

        dialogFormulario.add(formulario);
        dialogFormulario.getFooter().add(btnCancelar, btnGuardar);

        H2 titulo = new H2("Mi Colección");
        titulo.getStyle().set("color", "#ffffff");

        Button btnNuevo = new Button("+ Nuevo Elemento", e -> dialogFormulario.open());
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, txtBuscar, btnNuevo);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(Alignment.CENTER);

        add(barraSuperior, contenedorTarjetas);
        cargarTarjetas();
    }

    public void cargarTarjetas() {
        cargarTarjetas("");
    }

    public void cargarTarjetas(String filtro) {
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
                String generoLower = e.genero.toLowerCase();
                String creadorLower = e.creador.toLowerCase();
                String anioStr = String.valueOf(e.anio);

                // Evalúa si la búsqueda coincide con título, género, creador o año
                boolean coincideTitulo = tituloLower.contains(busqueda);
                boolean coincideGenero = generoLower.contains(busqueda);
                boolean coincideCreador = creadorLower.contains(busqueda);
                boolean coincideAnio = anioStr.contains(busqueda);

                if (!coincideTitulo && !coincideGenero && !coincideCreador && !coincideAnio) {
                    continue; // Descarta la tarjeta si no coincide con ninguno de los 4 campos
                }
            }

            VerticalLayout tarjeta = crearTarjeta(e);
            contenedorTarjetas.add(tarjeta);
            tarjetasMostradas++;
        }

        if (tarjetasMostradas == 0) {
            String textoMensaje;

            if (filtro != null && !filtro.trim().isEmpty()) {
                textoMensaje = "No se encontraron resultados para '" + filtro + "'.";
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
        tarjeta.setWidth("200px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);

        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");
        tarjeta.getStyle().set("cursor", "pointer");

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

        Estrellas estrellasLectura = new Estrellas(e.calificacion, false);

        tarjeta.add(imgPortada, lblTitulo,  estrellasLectura);
        tarjeta.addClickListener(event -> abrirFichaDetalles(e));
        Details ficha = abrirFichaDetalles(e);
        tarjeta.add(imgPortada, lblTitulo, estrellasLectura, ficha);

        return tarjeta;
    }

        public Details abrirFichaDetalles(ElementoMultimedia e) {

        // 2. Todos los campos de información con colores para fondo oscuro
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

        // 3. Botón de eliminación integrando la función existente
        Button btnEliminar = new Button("Eliminar", eventDel -> {
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Eliminar elemento?");

            Span msgConfirmacion = new Span("¿Estás seguro de que deseas eliminar '" + e.titulo + "'? Esta acción no se puede deshacer.");

            Button btnConfirmarEliminar = new Button("Sí, eliminar", eventSi -> {
                if (!e.imagen.equals("default.png")) {
                    File fotoFisica = new File("portadas", e.imagen);
                    if (fotoFisica.exists()) {
                        fotoFisica.delete();
                    }
                }

                // Invocación de la función de eliminación
                funciones.eliminarElemento(e.id);
                Notification.show("Elemento eliminado de tu colección.");

                cargarTarjetas(txtBuscar.getValue());
                dialogConfirmacion.close();
            });
            btnConfirmarEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            Button btnCancelarConf = new Button("Cancelar", eventNo -> dialogConfirmacion.close());
            btnCancelarConf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnCancelarConf.getStyle().set("color", "#ff4d4d");

            dialogConfirmacion.add(msgConfirmacion);
            dialogConfirmacion.getFooter().add(btnCancelarConf, btnConfirmarEliminar);

            dialogConfirmacion.open();
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnEliminar.setWidthFull();

        // 4. Agrupación de todos los elementos dentro del contenedor del acordeón
        VerticalLayout layoutContenido = new VerticalLayout(
            lblDetalles, 
            lblCreadorAnio, 
            lblEstado, 
            btnEliminar
        );
        layoutContenido.setPadding(false);
        layoutContenido.setSpacing(true);
        layoutContenido.getStyle().set("margin-top", "10px");

        // 5. Creación y retorno del componente Details
       Span lblSummary = new Span("Ver detalles");
        lblSummary.getStyle().set("color", "#ffffff"); // Texto en blanco puro (o #ff4d4d si lo quieres en rojo Akatsuki)
        lblSummary.getStyle().set("font-size", "0.85rem");
        lblSummary.getStyle().set("font-weight", "500");

        // 2. Pasamos el Span como primer argumento del constructor
        Details details = new Details(lblSummary, layoutContenido);

        // 3. Forzamos a la flecha desplegable (>) a pintarse en blanco
        details.getElement().getStyle().set("--lumo-secondary-text-color", "#ffffff");
        details.getStyle().set("width", "100%");

        return details;
    }
}