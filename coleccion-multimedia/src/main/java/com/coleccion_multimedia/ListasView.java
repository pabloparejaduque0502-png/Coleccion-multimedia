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

import java.io.File;
import java.io.FileOutputStream;

@Route(value = "listas", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mis Listas")
public class ListasView extends VerticalLayout {

    public FlexLayout contenedorListas = new FlexLayout();
    public TextField txtBuscar = new TextField();

    public ListasView() {
        setClassName("listas-view");
        setSizeFull();
        getStyle().set("background-color", "#000000");

        contenedorListas.setWidthFull();
        contenedorListas.getStyle().set("flex-wrap", "wrap");
        contenedorListas.getStyle().set("gap", "20px");

        // 1. Barra de búsqueda central
        txtBuscar.setPlaceholder("Buscar listas por nombre o descripción...");
        txtBuscar.setWidth("350px");
        txtBuscar.setClearButtonVisible(true);
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> cargarListas(e.getValue()));

        // 2. Diálogo formulario para crear listas
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

        final String[] nombreArchivoSubido = new String[]{ "default.png" };
        final byte[][] bytesImagenSubida = new byte[1][];

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

            String nombreImagenFinal = "default.png";

            if (bytesImagenSubida[0] != null && bytesImagenSubida[0].length > 0) {
                try {
                    File carpetaPortadas = new File("portadas");
                    if (!carpetaPortadas.exists()) {
                        carpetaPortadas.mkdirs();
                    }

                    nombreImagenFinal = System.currentTimeMillis() + "_lista_" + nombreArchivoSubido[0];
                    File archivoDestino = new File(carpetaPortadas, nombreImagenFinal);
                    try (FileOutputStream output = new FileOutputStream(archivoDestino)) {
                        output.write(bytesImagenSubida[0]);
                    }
                } catch (Exception ex) {
                    Notification.show("Error al guardar la imagen de la lista.");
                }
            }

            Object[] seleccionados = mcbElementos.getValue().toArray();
            String[] idsElementos = new String[seleccionados.length];
            for (int i = 0; i < seleccionados.length; i++) {
                idsElementos[i] = ((ElementoMultimedia) seleccionados[i]).id;
            }

            String idLista = String.valueOf(System.currentTimeMillis());
            ListaPersonalizada nuevaLista = new ListaPersonalizada(idLista, txtNombreLista.getValue(), txtDescripcion.getValue(), nombreImagenFinal, idsElementos);

            funciones.guardarLista(nuevaLista);
            Notification.show("Lista creada correctamente.");

            txtNombreLista.clear();
            txtDescripcion.clear();
            mcbElementos.clear();
            uploadPortada.clearFileList();
            nombreArchivoSubido[0] = "default.png";
            bytesImagenSubida[0] = null;

            dialogFormulario.close();
            cargarListas(txtBuscar.getValue());
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

        // 3. Barra superior
        H2 titulo = new H2("Mis Listas");
        titulo.getStyle().set("color", "#ffffff");

        Button btnNueva = new Button("+ Nueva Lista", e -> dialogFormulario.open());
        btnNueva.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraSuperior = new HorizontalLayout(titulo, txtBuscar, btnNueva);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(Alignment.CENTER);

        add(barraSuperior, contenedorListas);
        cargarListas("");
    }

    // 4. Carga y filtrado reactivo desde listas.txt
    public void cargarListas(String filtro) {
        contenedorListas.removeAll();

        ListaPersonalizada[] listas = funciones.leerListas();
        int listasMostradas = 0;

        for (int i = 0; i < listas.length; i++) {
            ListaPersonalizada l = listas[i];
            if (l == null) continue;

            if (filtro != null && !filtro.trim().isEmpty()) {
                String busqueda = filtro.toLowerCase().trim();
                boolean coincideNombre = l.nombre.toLowerCase().contains(busqueda);
                boolean coincideDesc = l.descripcion.toLowerCase().contains(busqueda);

                if (!coincideNombre && !coincideDesc) {
                    continue;
                }
            }

            VerticalLayout tarjeta = crearTarjetaLista(l);
            contenedorListas.add(tarjeta);
            listasMostradas++;
        }

        if (listasMostradas == 0) {
            String textoMensaje = (filtro != null && !filtro.trim().isEmpty())
                ? "No se encontraron listas que coincidan con '" + filtro + "'."
                : "Aún no has creado ninguna lista personalizada. Utiliza el botón '+ Nueva Lista'.";

            Span msgVacio = new Span(textoMensaje);
            msgVacio.getStyle().set("color", "#aaaaaa");
            msgVacio.getStyle().set("font-style", "italic");
            msgVacio.getStyle().set("padding", "20px");
            contenedorListas.add(msgVacio);
        }
    }

    // 5. Fábrica de tarjetas con visualización de portada física
    public VerticalLayout crearTarjetaLista(ListaPersonalizada l) {
        VerticalLayout tarjeta = new VerticalLayout();
        tarjeta.setWidth("320px");
        tarjeta.setPadding(true);
        tarjeta.setSpacing(true);

        tarjeta.getStyle().set("border", "1px solid #2a2a2a");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "#181818");
        tarjeta.getStyle().set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");

        File archivo = new File("portadas", l.imagen);
        if (!archivo.exists()) {
            archivo = new File("portadas", "default.png");
        }

        DownloadHandler handlerImagen = DownloadHandler.forFile(archivo);
        Image imgPortada = new Image(handlerImagen, l.nombre);
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

        Details detallesLista = crearDetallesLista(l);

        tarjeta.add(imgPortada, lblNombre, lblDesc, detallesLista);

        return tarjeta;
    }

    // 6. Contenido del acordeón Details con ítems incluidos y acciones en listas.txt
    public Details crearDetallesLista(ListaPersonalizada l) {
        VerticalLayout contenidoElementos = new VerticalLayout();
        contenidoElementos.setPadding(false);
        contenidoElementos.setSpacing(true);
        contenidoElementos.getStyle().set("margin-top", "10px");

        // Sección A: Renderizado de los elementos guardados con diálogo de confirmación para quitar cada uno
        Span lblTituloContenido = new Span("Contenido de la lista (" + l.idsElementos.length + "):");
        lblTituloContenido.getStyle().set("color", "#ffffff");
        lblTituloContenido.getStyle().set("font-weight", "bold");
        lblTituloContenido.getStyle().set("font-size", "0.85rem");
        contenidoElementos.add(lblTituloContenido);

        if (l.idsElementos.length == 0) {
            Span lblVacio = new Span("Esta lista no tiene elementos asignados.");
            lblVacio.getStyle().set("color", "#888888");
            lblVacio.getStyle().set("font-style", "italic");
            lblVacio.getStyle().set("font-size", "0.8rem");
            contenidoElementos.add(lblVacio);
        } else {
            for (int i = 0; i < l.idsElementos.length; i++) {
                String idElem = l.idsElementos[i];
                ElementoMultimedia elem = funciones.buscarElementoPorId(idElem);

                if (elem != null) {
                    Span lblItem = new Span("• " + elem.titulo + " (" + elem.tipo + ")");
                    lblItem.getStyle().set("color", "#cccccc");
                    lblItem.getStyle().set("font-size", "0.85rem");

                    // Botón "✕" con diálogo de confirmación
                    Button btnQuitarItem = new Button("✕", eventQuitar -> {
                        Dialog dialogConfirmacion = new Dialog();
                        dialogConfirmacion.setHeaderTitle("¿Quitar elemento de la lista?");

                        Span msgConfirmacion = new Span("¿Estás seguro de que deseas quitar '" + elem.titulo + "' de la lista '" + l.nombre + "'?");

                        Button btnConfirmarQuitar = new Button("Sí, quitar", eventSi -> {
                            // 1. Reconstrucción del arreglo excluyendo el ID seleccionado
                            String[] nuevosIds = new String[l.idsElementos.length - 1];
                            int idx = 0;
                            for (int j = 0; j < l.idsElementos.length; j++) {
                                if (!l.idsElementos[j].equals(elem.id)) {
                                    nuevosIds[idx] = l.idsElementos[j];
                                    idx++;
                                }
                            }
                            l.idsElementos = nuevosIds;

                            // 2. Persistir el cambio en listas.txt y recargar la pantalla
                            funciones.actualizarLista(l);
                            Notification.show("'" + elem.titulo + "' fue eliminado de '" + l.nombre + "'.");
                            cargarListas(txtBuscar.getValue());
                            dialogConfirmacion.close();
                        });
                        btnConfirmarQuitar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

                        Button btnCancelarConf = new Button("Cancelar", eventNo -> dialogConfirmacion.close());
                        btnCancelarConf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                        btnCancelarConf.getStyle().set("color", "#ff4d4d");

                        dialogConfirmacion.add(msgConfirmacion);
                        dialogConfirmacion.getFooter().add(btnCancelarConf, btnConfirmarQuitar);
                        dialogConfirmacion.open();
                    });
                    btnQuitarItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
                    btnQuitarItem.getStyle().set("color", "#ff4d4d");
                    btnQuitarItem.getStyle().set("padding", "0 6px");
                    btnQuitarItem.getStyle().set("min-width", "auto");
                    btnQuitarItem.getStyle().set("height", "24px");

                    HorizontalLayout filaItem = new HorizontalLayout(lblItem, btnQuitarItem);
                    filaItem.setWidthFull();
                    filaItem.setJustifyContentMode(JustifyContentMode.BETWEEN);
                    filaItem.setAlignItems(Alignment.CENTER);

                    contenidoElementos.add(filaItem);
                }
            }
        }

        // Sección B: Agregar nuevos elementos a la lista dinámicamente con MultiSelectComboBox
        MultiSelectComboBox<ElementoMultimedia> mcbAgregarElementos = new MultiSelectComboBox<>("Añadir elementos a esta lista");
        mcbAgregarElementos.setItems(funciones.leerElementos());
        mcbAgregarElementos.setItemLabelGenerator(e -> e.titulo + " (" + e.tipo + ")");
        mcbAgregarElementos.setPlaceholder("Selecciona uno o varios elementos...");
        mcbAgregarElementos.setWidthFull();

        Button btnAgregar = new Button("+ Añadir", event -> {
            if (mcbAgregarElementos.isEmpty()) {
                Notification.show("Selecciona al menos un elemento.");
                return;
            }

            Object[] seleccionados = mcbAgregarElementos.getValue().toArray();
            
            // 1. Filtrar elementos duplicados (los que ya pertenezcan a la lista)
            int cantidadNuevos = 0;
            ElementoMultimedia[] elementosNuevos = new ElementoMultimedia[seleccionados.length];

            for (int i = 0; i < seleccionados.length; i++) {
                ElementoMultimedia elem = (ElementoMultimedia) seleccionados[i];
                boolean yaExiste = false;

                for (int j = 0; j < l.idsElementos.length; j++) {
                    if (l.idsElementos[j].equals(elem.id)) {
                        yaExiste = true;
                        break;
                    }
                }

                if (!yaExiste) {
                    elementosNuevos[cantidadNuevos] = elem;
                    cantidadNuevos++;
                }
            }

            if (cantidadNuevos == 0) {
                Notification.show("Todos los elementos seleccionados ya forman parte de esta lista.");
                return;
            }

            // 2. Redimensionamiento del arreglo estático para añadir todos los nuevos IDs
            String[] nuevosIds = new String[l.idsElementos.length + cantidadNuevos];
            
            // Copiar IDs previos
            for (int i = 0; i < l.idsElementos.length; i++) {
                nuevosIds[i] = l.idsElementos[i];
            }
            
            // Agregar los nuevos IDs filtrados al final
            for (int i = 0; i < cantidadNuevos; i++) {
                nuevosIds[l.idsElementos.length + i] = elementosNuevos[i].id;
            }

            l.idsElementos = nuevosIds;

            // 3. Persistir cambios en listas.txt y recargar pantalla
            funciones.actualizarLista(l);
            Notification.show(cantidadNuevos + " elemento(s) añadido(s) a '" + l.nombre + "'.");
            cargarListas(txtBuscar.getValue());
        });
        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout barraAgregar = new HorizontalLayout(mcbAgregarElementos, btnAgregar);
        barraAgregar.setWidthFull();
        barraAgregar.setAlignItems(Alignment.BASELINE);

        // Sección C: Eliminación de la lista completa
        Button btnEliminarLista = new Button("Eliminar Lista", eventDel -> {
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Eliminar lista?");

            Span msgConfirmacion = new Span("¿Estás seguro de que deseas eliminar '" + l.nombre + "'?");

            Button btnConfirmarEliminar = new Button("Sí, eliminar", eventSi -> {
                if (!l.imagen.equals("default.png")) {
                    File fotoFisica = new File("portadas", l.imagen);
                    if (fotoFisica.exists()) {
                        fotoFisica.delete();
                    }
                }

                funciones.eliminarLista(l.id);
                Notification.show("Lista eliminada correctamente.");
                cargarListas(txtBuscar.getValue());
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
        btnEliminarLista.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnEliminarLista.setWidthFull();

        contenidoElementos.add(barraAgregar, btnEliminarLista);

        Span lblSummary = new Span("Ver contenido de la lista");
        lblSummary.getStyle().set("color", "#ffffff");
        lblSummary.getStyle().set("font-size", "0.85rem");
        lblSummary.getStyle().set("font-weight", "500");

        Details details = new Details(lblSummary, contenidoElementos);
        details.getElement().getStyle().set("--lumo-secondary-text-color", "#ffffff");
        details.getStyle().set("width", "100%");

        return details;
    }
}