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
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField; 
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import java.io.File;
import java.io.FileOutputStream;

// --- NUEVO: Importaciones actualizadas para Vaadin 25 (DownloadHandler, FlexLayout, H4, Image) ---
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.server.streams.DownloadHandler;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Akatsuki Media - Mi Colección")
public class MainView extends VerticalLayout {

    // --- NUEVO: Contenedor flexible para organizar los recuadros de la colección en cuadrícula ---
    public FlexLayout contenedorTarjetas = new FlexLayout();

    public MainView() {
        setClassName("main-view");

        // --- NUEVO: Configuración de la rejilla de tarjetas (Flexbox) ---
        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.getStyle().set("flex-wrap", "wrap"); // Salta de línea automáticamente al llenar el ancho
        contenedorTarjetas.getStyle().set("gap", "20px");       // Espaciado de 20px entre cada tarjeta

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
        
        ComboBox<EstadoMultimedia> cbEstado = new ComboBox<>("Estado");
        cbEstado.setItems(EstadoMultimedia.values());

        // Arreglos tradicionales de 1 posición para guardar temporalmente la imagen subida
        final String[] nombreArchivoSubido = new String[]{ "default.png" };
        final byte[][] bytesImagenSubida = new byte[1][];

        // UploadHandler de Vaadin 25 (Reemplaza a MemoryBuffer)
        UploadHandler handlerImagen = UploadHandler.inMemory((metadata, data) -> {
            nombreArchivoSubido[0] = metadata.fileName();
            bytesImagenSubida[0] = data;
        });

        Upload uploadPortada = new Upload(handlerImagen);
        
        // API de filtrado actualizada en Vaadin 25.2 (usa extensiones exactas)
        String[] extensionesPermitidas = new String[]{ ".jpg", ".jpeg", ".png" };
        uploadPortada.setAcceptedFileExtensions(extensionesPermitidas);
        uploadPortada.setMaxFileSize(2 * 1024 * 1024); // Máximo 2 MB
        uploadPortada.setMaxFiles(1); // Restringe el formulario a 1 sola portada

        // Crear el botón Guardar para el interior del Dialog
        Button btnGuardar = new Button("Guardar", event -> {

            // Validaciones al presionar el botón guardar
            if (txtTitulo.isEmpty() || cbTipo.getValue() == null || txtGenero.isEmpty() || 
                txtAnio.isEmpty() || txtCreador.isEmpty() || ratingCalificacion.valor == 0 || cbEstado.getValue() == null) {
                Notification.show("Por favor, completa todos los campos del formulario.");
                return; // Detiene la ejecución si falta algún dato
            }

            int anio = txtAnio.getValue();
            int calificacion = ratingCalificacion.valor;

            if (anio < 1800 || anio > 2026) {
                Notification.show("Por favor, ingresa un año válido.");
                return;
            }

            // Escritura física del archivo de portada en el disco duro
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
                cbEstado.getValue(),
                nombreImagenFinal
            );

            funciones.guardarElemento(nuevo);
            Notification.show("¡Elemento guardado en el archivo!");

            // --- NUEVO: Refrescar la cuadrícula de tarjetas en pantalla al guardar ---
            cargarTarjetas();

            // Limpiar campos y cerrar diálogo
            txtTitulo.clear();
            cbTipo.clear();
            txtGenero.clear();
            txtAnio.clear();
            txtCreador.clear();
            ratingCalificacion.limpiar();
            cbEstado.clear();
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
        formulario.add(txtTitulo, cbTipo, txtGenero, txtAnio, txtCreador, campoCalificacion, cbEstado, uploadPortada);
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

        // --- NUEVO: Agregar la rejilla de tarjetas a la vista y llamar la carga inicial ---
        add(barraSuperior, contenedorTarjetas);
        cargarTarjetas();
    }

    
// Método para leer los registros y construir los recuadros usando DownloadHandler de Vaadin 25
    public void cargarTarjetas() {
        // 1. Limpia el contenido previo para evitar duplicar recuadros al refrescar
        contenedorTarjetas.removeAll();

        // 2. Lee el arreglo tradicional de objetos desde el archivo plano mediante funciones
        ElementoMultimedia[] elementos = funciones.leerElementos();

        // 3. Ciclo for tradicional sobre el arreglo de elementos
        for (int i = 0; i < elementos.length; i++) {
            ElementoMultimedia e = elementos[i];

            // --- NUEVO: Protección anti-null para ignorar registros corruptos o vacíos ---
            if (e == null) {
                continue; // Salta esta iteración de forma segura y evita el NullPointerException
            }

            // 4. Crear el contenedor individual de la tarjeta (recuadro vertical)
            VerticalLayout tarjeta = new VerticalLayout();
            tarjeta.setWidth("200px");
            tarjeta.setPadding(true);
            tarjeta.setSpacing(true);

            // Estilos CSS para el aspecto visual (borde, sombra y esquinas redondeadas)
            tarjeta.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
            tarjeta.getStyle().set("border-radius", "12px");
            tarjeta.getStyle().set("background-color", "var(--lumo-base-color)");
            tarjeta.getStyle().set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.05)");
            tarjeta.getStyle().set("cursor", "pointer");

            // 5. Verificar si la imagen física existe en el disco duro o conmutar a la imagen por defecto
            File archivo = new File("portadas", e.imagen);
            if (!archivo.exists()) {
                archivo = new File("portadas", "default.png");
            }

            // 6. API Moderna de Vaadin 25: Crear el gestor de descarga para archivos locales
            DownloadHandler handlerImagen = DownloadHandler.forFile(archivo);

            // 7. Constructor moderno de Vaadin 25: Image(DownloadHandler, String)
            Image imgPortada = new Image(handlerImagen, e.titulo);
            imgPortada.setWidth("100%");
            imgPortada.setHeight("200px");
            imgPortada.getStyle().set("object-fit", "cover"); // Escala la foto proporcionalmente sin deformarla
            imgPortada.getStyle().set("border-radius", "8px");

            // 8. Textos descriptivos de la tarjeta
            H4 lblTitulo = new H4(e.titulo);
            lblTitulo.getStyle().set("margin", "0");
            lblTitulo.getStyle().set("font-size", "1rem");

            Span lblSubtitulo = new Span(e.creador + " • " + e.anio);
            lblSubtitulo.getStyle().set("font-size", "0.85rem");
            lblSubtitulo.getStyle().set("color", "var(--lumo-secondary-text-color)");

            // 9. Componente de Estrellas en modo solo lectura (esEditable = false)
            Estrellas estrellasLectura = new Estrellas(e.calificacion, false);

            // 10. Empaquetar todo en la tarjeta y agregarla a la cuadrícula principal
            tarjeta.add(imgPortada, lblTitulo, lblSubtitulo, estrellasLectura);
           tarjeta.addClickListener(event -> abrirFichaDetalles(e));

            contenedorTarjetas.add(tarjeta);
        }
    }

    // --- NUEVO: Método que despliega la Ficha de Detalles en una ventana emergente (Dialog) ---
    public void abrirFichaDetalles(ElementoMultimedia e) {
        Dialog dialogDetalles = new Dialog();
        dialogDetalles.setHeaderTitle(e.titulo);
        dialogDetalles.setWidth("450px");

        // 1. Preparar la imagen de portada en tamaño grande
        File archivo = new File("portadas", e.imagen);
        if (!archivo.exists()) {
            archivo = new File("portadas", "default.png");
        }

        DownloadHandler handlerImagen = DownloadHandler.forFile(archivo);
        Image imgGrande = new Image(handlerImagen, e.titulo);
        imgGrande.setWidth("100%");
        imgGrande.setMaxHeight("300px");
        imgGrande.getStyle().set("object-fit", "cover");
        imgGrande.getStyle().set("border-radius", "8px");

        // 2. Componentes de texto con la información detallada del elemento

        Span lblDetalles = new Span("Tipo: " + e.tipo + "  |  Género: " + e.genero);
        Span lblCreadorAnio = new Span("Creador: " + e.creador + "  |  Año: " + e.anio);
        Span lblEstado = new Span("Estado actual: " + e.estado);
        lblEstado.getStyle().set("font-weight", "bold");

        Estrellas estrellasDetalle = new Estrellas(e.calificacion, false);

        VerticalLayout layoutContenido = new VerticalLayout(
            imgGrande,  lblDetalles, lblCreadorAnio, lblEstado, estrellasDetalle
        );
        layoutContenido.setPadding(false);
        layoutContenido.setSpacing(true);

        // 3. Botón de Eliminación de Registro
        Button btnEliminar = new Button("Eliminar", eventDel -> {
            
            // Requisito Bonus: Si la portada NO es default.png, borra la imagen física del disco duro
            Dialog dialogConfirmacion = new Dialog();
            dialogConfirmacion.setHeaderTitle("¿Eliminar elemento?");

            Span msgConfirmacion = new Span("¿Estás seguro de que deseas eliminar '" + e.titulo + "'? Esta acción no se puede deshacer.");

            Button btnConfirmarEliminar = new Button("Sí, eliminar", eventSi -> {
                // 1. Borrar la imagen física en portadas/ si no es la por defecto
                if (!e.imagen.equals("default.png")) {
                    File fotoFisica = new File("portadas", e.imagen);
                    if (fotoFisica.exists()) {
                        fotoFisica.delete();
                    }
                }

                // 2. Eliminar el registro del archivo elementos.txt
                funciones.eliminarElemento(e.id);
                Notification.show("Elemento eliminado de tu colección.");

                // 3. Refrescar la pantalla y cerrar ambas ventanas
                cargarTarjetas();
                dialogConfirmacion.close();
                dialogDetalles.close();
            });
            btnConfirmarEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            Button btnCancelarConf = new Button("Cancelar", eventNo -> dialogConfirmacion.close());

            dialogConfirmacion.add(msgConfirmacion);
            dialogConfirmacion.getFooter().add(btnCancelarConf, btnConfirmarEliminar);
            
            dialogConfirmacion.open();
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCerrar = new Button("Cerrar", eventClose -> dialogDetalles.close());

        dialogDetalles.add(layoutContenido);
        dialogDetalles.getFooter().add(btnCerrar, btnEliminar);

        dialogDetalles.open();
    }
}
 