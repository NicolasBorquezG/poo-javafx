package cl.elprofecarreno.app.controller;

import java.util.stream.Collectors;

import cl.elprofecarreno.app.controller.dto.AudioLibroDTO;
import cl.elprofecarreno.app.controller.dto.LibroDTO;
import cl.elprofecarreno.app.controller.dto.MaterialDTO;
import cl.elprofecarreno.app.dao.GestorBiblioteca;
import cl.elprofecarreno.app.dao.entity.AudioLibro;
import cl.elprofecarreno.app.dao.entity.Libro;
import cl.elprofecarreno.app.dao.entity.Material;
import cl.elprofecarreno.app.dao.excepcion.DAOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class HomeController {

    private static final String TIPO_LIBRO = "Libro";
    private static final String TIPO_AUDIOLIBRO = "AudioLibro";

    private final GestorBiblioteca gestor = new GestorBiblioteca();
    private final ObservableList<MaterialDTO> materiales = FXCollections.observableArrayList();

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAnio;
    @FXML private TextField txtCopias;
    @FXML private ComboBox<String> cmbTipoMaterial;

    @FXML private TitledPane tpLibro;
    @FXML private TextField txtAutor;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private CheckBox chkReserva;

    @FXML private TitledPane tpAudioLibro;
    @FXML private TextField txtDuracion;

    @FXML private TableView<MaterialDTO> tblMateriales;
    @FXML private TableColumn<MaterialDTO, String> colTitulo;
    @FXML private TableColumn<MaterialDTO, String> colAnio;
    @FXML private TableColumn<MaterialDTO, String> colTipo;
    @FXML private TableColumn<MaterialDTO, String> colDetalle;
    @FXML private TableColumn<MaterialDTO, String> colCosto;
    @FXML private TableColumn<MaterialDTO, String> colDisponible;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    @FXML
    private void initialize() {
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colAnio.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAnioPublicacion())));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(esLibro(c.getValue()) ? "Libro" : "AudioLibro"));
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(detalle(c.getValue())));
        colCosto.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("$%,.0f", c.getValue().getCostoPrestamo())));
        colDisponible.setCellValueFactory(c -> new SimpleStringProperty(disponible(c.getValue())));
        tblMateriales.setItems(materiales);

        btnEliminar.disableProperty().bind(
                tblMateriales.getSelectionModel().selectedItemProperty().isNull());

        cmbTipoMaterial.valueProperty().addListener((obs, anterior, tipo) -> actualizarSecciones(tipo));
        actualizarSecciones(null);

        recargar();
    }

    private boolean esLibro(MaterialDTO dto) {
        return dto instanceof LibroDTO;
    }

    private String detalle(MaterialDTO dto) {
        if (dto instanceof LibroDTO l) {
            return l.getAutor() + " - " + l.getEstadoConservacion();
        }
        return ((AudioLibroDTO) dto).getDuracionMinutos() + " min";
    }

    private String disponible(MaterialDTO dto) {
        if (dto instanceof LibroDTO l) {
            return l.isDisponibleParaReserva() ? "Si" : "No";
        }
        return "-";
    }

    private void recargar() {
        materiales.setAll(gestor.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    private void actualizarSecciones(String tipo) {
        boolean libro = TIPO_LIBRO.equals(tipo);
        boolean audio = TIPO_AUDIOLIBRO.equals(tipo);
        tpLibro.setDisable(!libro);
        tpAudioLibro.setDisable(!audio);
    }

    @FXML
    private void eliminar() {
        MaterialDTO seleccionada = tblMateriales.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(AlertType.WARNING, "Eliminar", "Seleccione un registro de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(AlertType.CONFIRMATION,
                "Desea eliminar \"" + seleccionada.getTitulo() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Eliminar");
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        try {
            gestor.delete(seleccionada.getTitulo());
            recargar();
        } catch (DAOException e) {
            mostrarAlerta(AlertType.ERROR, "Error al eliminar", e.getMessage());
        }
    }

    @FXML
    private void guardar() {
        String error = validar();
        if (error != null) {
            mostrarAlerta(AlertType.WARNING, "Validacion", error);
            return;
        }
        try {
            gestor.save(construirEntidad());
            recargar();
            limpiarFormulario();
            mostrarAlerta(AlertType.INFORMATION, "Guardar", "Registro guardado correctamente.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(AlertType.WARNING, "Validacion", e.getMessage());
        } catch (DAOException e) {
            mostrarAlerta(AlertType.ERROR, "Error al guardar", e.getMessage());
        }
    }

    private String validar() {
        if (txtTitulo.getText().trim().isEmpty()) {
            return "Ingrese el titulo.";
        }
        try {
            if (Integer.parseInt(txtAnio.getText().trim()) <= 0) {
                return "El anio de publicacion debe ser mayor a 0.";
            }
        } catch (NumberFormatException e) {
            return "El anio debe ser un numero entero.";
        }
        try {
            if (Integer.parseInt(txtCopias.getText().trim()) < 0) {
                return "Las copias disponibles no pueden ser negativas.";
            }
        } catch (NumberFormatException e) {
            return "Las copias deben ser un numero entero.";
        }
        String tipo = cmbTipoMaterial.getValue();
        if (tipo == null) {
            return "Seleccione el tipo de material.";
        }
        if (TIPO_LIBRO.equals(tipo)) {
            if (txtAutor.getText().trim().isEmpty()) {
                return "Ingrese el autor.";
            }
            if (cmbEstado.getValue() == null) {
                return "Seleccione el estado de conservacion.";
            }
        } else {
            try {
                if (Integer.parseInt(txtDuracion.getText().trim()) <= 0) {
                    return "La duracion debe ser mayor a 0.";
                }
            } catch (NumberFormatException e) {
                return "La duracion debe ser un numero entero.";
            }
        }
        return null;
    }

    private Material construirEntidad() {
        String titulo = txtTitulo.getText().trim();
        int anio = Integer.parseInt(txtAnio.getText().trim());
        int copias = Integer.parseInt(txtCopias.getText().trim());

        if (TIPO_LIBRO.equals(cmbTipoMaterial.getValue())) {
            return new Libro(titulo, anio, copias,
                    txtAutor.getText().trim(),
                    cmbEstado.getValue(),
                    chkReserva.isSelected());
        }
        return new AudioLibro(titulo, anio, copias,
                Integer.parseInt(txtDuracion.getText().trim()));
    }

    private MaterialDTO toDTO(Material material) {
        if (material instanceof Libro libro) {
            return LibroDTO.builder()
                    .titulo(libro.getTitulo())
                    .anioPublicacion(libro.getAnioPublicacion())
                    .copiasDisponibles(libro.getCopiasDisponibles())
                    .costoPrestamo(libro.calcularCostoPrestamo())
                    .autor(libro.getAutor())
                    .estadoConservacion(libro.getEstadoConservacion())
                    .disponibleParaReserva(libro.isDisponibleParaReserva())
                    .build();
        }
        AudioLibro audio = (AudioLibro) material;
        return AudioLibroDTO.builder()
                .titulo(audio.getTitulo())
                .anioPublicacion(audio.getAnioPublicacion())
                .copiasDisponibles(audio.getCopiasDisponibles())
                .costoPrestamo(audio.calcularCostoPrestamo())
                .duracionMinutos(audio.getDuracionMinutos())
                .build();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        txtTitulo.clear();
        txtAnio.clear();
        txtCopias.clear();
        cmbTipoMaterial.setValue(null);
        txtAutor.clear();
        cmbEstado.setValue(null);
        chkReserva.setSelected(false);
        txtDuracion.clear();
    }
}