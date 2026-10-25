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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField; 
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// --- NUEVO: API de carga de archivos actualizada para Vaadin 25 ---
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import java.io.File;
import java.io.FileOutputStream;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mi Colección")
public class MainView extends VerticalLayout {

    public MainView() {
        setClassName("main-view");

        // Crear el botón para agregar nuevos elementos
        Dialog dialogFormulario = new Dialog();
        dialogFormulario.setHeaderTitle("Agregar Nuevo Elemento");

        // Creación de los campos de entrada
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

        // Empaquetar la etiqueta y las estrellas en una cajita vertical local
        VerticalLayout campoCalificacion = new VerticalLayout(lblCalificacion, ratingCalificacion);
        campoCalificacion.setPadding(false);
        campoCalificacion.setSpacing(false);
        
        TextField txtEstado = new TextField("Estado");

        // --- NUEVO: Arreglos tradicionales de 1 posición para guardar temporalmente la imagen subida ---
        final String[] nombreArchivoSubido = new String[]{ "default.png" };
        final byte[][] bytesImagenSubida = new byte[1][];

        // --- NUEVO: UploadHandler de Vaadin 25 (Reemplaza a MemoryBuffer) ---
        UploadHandler handlerImagen = UploadHandler.inMemory((metadata, data) -> {
            nombreArchivoSubido[0] = metadata.fileName();
            bytesImagenSubida[0] = data;
        });

        Upload uploadPortada = new Upload(handlerImagen);
        
        // --- NUEVO: API de filtrado actualizada en Vaadin 25.2 (usa extensiones exactas) ---
        String[] extensionesPermitidas = new String[]{ ".jpg", ".jpeg", ".png" };
        uploadPortada.setAcceptedFileExtensions(extensionesPermitidas);
        uploadPortada.setMaxFileSize(2 * 1024 * 1024); // Máximo 2 MB
        uploadPortada.setMaxFiles(1); // Restringe el formulario a 1 sola portada

        // Crear el botón Guardar para el interior del Dialog
        Button btnGuardar = new Button("Guardar", event -> {

            // Validaciones al presionar el botón guardar
            if (txtTitulo.isEmpty() || cbTipo.getValue() == null || txtGenero.isEmpty() || 
                txtAnio.isEmpty() || txtCreador.isEmpty() || ratingCalificacion.valor == 0 || txtEstado.isEmpty()) {
                Notification.show("Por favor, completa todos los campos del formulario.");
                return; // Detiene la ejecución si falta algún dato
            }

            int anio = txtAnio.getValue();
            int calificacion = ratingCalificacion.valor;

            if (anio < 1800 || anio > 2026) {
                Notification.show("Por favor, ingresa un año válido.");
                return;
            }

            // --- NUEVO: Escritura física del archivo de portada en el disco duro ---
            String nombreImagenFinal = "default.png";

            if (bytesImagenSubida[0] != null && bytesImagenSubida[0].length > 0) {
                try {
                    File carpetaPortadas = new File("portadas");
                    if (!carpetaPortadas.exists()) {
                        carpetaPortadas.mkdirs(); // Crea la carpeta en la raíz si no existe
                    }

                    // Generamos un nombre único con el timestamp
                    String nombreOriginal = nombreArchivoSubido[0];
                    nombreImagenFinal = System.currentTimeMillis() + "_" + nombreOriginal;

                    // Escribimos directamente los bytes capturados por el handler
                    File archivoDestino = new File(carpetaPortadas, nombreImagenFinal);
                    FileOutputStream output = new FileOutputStream(archivoDestino);
                    output.write(bytesImagenSubida[0]);
                    output.close();

                } catch (Exception ex) {
                    Notification.show("Error al guardar el archivo de la imagen.");
                }
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
                txtEstado.getValue(),
                nombreImagenFinal
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
            uploadPortada.clearFileList();
            
            // Reiniciar las variables temporales de la imagen
            nombreArchivoSubido[0] = "default.png";
            bytesImagenSubida[0] = null;

            dialogFormulario.close();
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> dialogFormulario.close());

        // Meter los campos y botones adentro del Dialog
        FormLayout formulario = new FormLayout();
        formulario.add(txtTitulo, cbTipo, txtGenero, txtAnio, txtCreador, campoCalificacion, txtEstado, uploadPortada);
        formulario.setColspan(uploadPortada, 2);

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