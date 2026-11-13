package es.medac.sergiomitbo.gymfit;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class MainController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Menús y barras
    @FXML private ToolBar barraHerramientas;
    @FXML private HBox barraEstado;
    @FXML private CheckMenuItem chkVerHerramientas;
    @FXML private CheckMenuItem chkVerEstado;
    @FXML private TextField txtBuscar;
    @FXML private Label lblEstado;

    // Formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private ToggleGroup grupoSexo;
    @FXML private ComboBox<String> cbProvincia;
    @FXML private ToggleGroup grupoCuota;
    @FXML private FlowPane panelClases;
    @FXML private TextField txtIban;
    @FXML private HBox cajaError;
    @FXML private Label lblErrorTitulo;
    @FXML private Label lblErrorTexto;

    // Tabla
    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, String> colNombre;
    @FXML private TableColumn<Socio, String> colFecha;
    @FXML private TableColumn<Socio, String> colCuota;
    @FXML private TableColumn<Socio, String> colProvincia;
    @FXML private TableColumn<Socio, String> colClases;
    @FXML private Label lblContador;

    private final ObservableList<Socio> socios = FXCollections.observableArrayList();
    private FilteredList<Socio> sociosFiltrados;
    private Socio socioEditando = null;

    @FXML
    private void initialize() {
        cbProvincia.getItems().addAll("Almería", "Cádiz", "Córdoba", "Granada", "Huelva",
                "Jaén", "Málaga", "Sevilla", "Otra provincia");

        // El DatePicker muestra y acepta las fechas como dd/mm/aaaa
        dpFechaNacimiento.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : fecha.format(FORMATO);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), FORMATO);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaTexto"));
        colCuota.setCellValueFactory(new PropertyValueFactory<>("cuota"));
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colClases.setCellValueFactory(new PropertyValueFactory<>("clasesTexto"));

        cargarSociosDeEjemplo();
        sociosFiltrados = new FilteredList<>(socios, s -> true);
        tablaSocios.setItems(sociosFiltrados);

        // Buscador de la barra de herramientas
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> {
            String texto = nuevo.trim().toLowerCase();
            sociosFiltrados.setPredicate(s -> texto.isEmpty()
                    || s.getNombreCompleto().toLowerCase().contains(texto));
            actualizarContador();
        });

        actualizarContador();
    }

    private void cargarSociosDeEjemplo() {
        socios.add(new Socio("Ana", "Ruiz Pérez", LocalDate.of(1999, 3, 12), "Mujer", "Sevilla",
                "Premium", List.of("Spinning", "Yoga"), "ES1200491500051234567892"));
        socios.add(new Socio("Luis", "Martín Gil", LocalDate.of(2001, 7, 21), "Hombre", "Sevilla",
                "Básica", List.of("Crossfit"), "ES7921000813610123456789"));
        socios.add(new Socio("Marta", "López Sanz", LocalDate.of(1988, 11, 30), "Mujer", "Huelva",
                "Plus", List.of("Pilates", "Zumba"), "ES6000491500051234567892"));
        socios.add(new Socio("José", "Navarro Díaz", LocalDate.of(2005, 2, 2), "Hombre", "Málaga",
                "Joven", List.of("Boxeo"), "ES9121000418450200051332"));
    }

    // ===================== ALTA DE SOCIOS =====================

    @FXML
    private void darDeAlta() {
        ocultarError();
        if (!validarFormulario()) {
            return;
        }

        Socio nuevo = new Socio(
                txtNombre.getText().trim(),
                txtApellidos.getText().trim(),
                dpFechaNacimiento.getValue(),
                textoSeleccionado(grupoSexo),
                cbProvincia.getValue(),
                cuotaSeleccionada(),
                clasesSeleccionadas(),
                txtIban.getText().replace(" ", "").toUpperCase());

        if (socioEditando != null) {
            socios.set(socios.indexOf(socioEditando), nuevo);
            lblEstado.setText("Se han guardado los cambios de " + nuevo.getNombreCompleto() + ".");
            socioEditando = null;
        } else {
            socios.add(nuevo);
            lblEstado.setText(nuevo.getNombreCompleto() + " se ha dado de alta correctamente.");
        }

        tablaSocios.getSelectionModel().select(nuevo);
        limpiarCampos();
        actualizarContador();
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isBlank()) {
            mostrarError("falta el nombre.", "Escribe el nombre del socio, por ejemplo «Laura».", txtNombre);
            return false;
        }
        if (txtApellidos.getText().isBlank()) {
            mostrarError("faltan los apellidos.", "Escribe los dos apellidos, por ejemplo «Gómez Ruiz».", txtApellidos);
            return false;
        }

        // Si se ha escrito la fecha a mano, la leemos del texto del editor
        LocalDate fecha = dpFechaNacimiento.getConverter().fromString(dpFechaNacimiento.getEditor().getText());
        dpFechaNacimiento.setValue(fecha);
        if (fecha == null) {
            mostrarError("la fecha de nacimiento no es válida.",
                    "Elígela en el calendario o escríbela como dd/mm/aaaa, por ejemplo 14/05/2003.",
                    dpFechaNacimiento);
            return false;
        }
        if (fecha.isAfter(LocalDate.now())) {
            mostrarError("la fecha de nacimiento es posterior a hoy.",
                    "Revisa el año: tiene que ser una fecha pasada, por ejemplo 14/05/2003.", dpFechaNacimiento);
            return false;
        }

        String cuota = cuotaSeleccionada();
        if (cuota == null) {
            mostrarError("no has elegido el tipo de cuota.", "Marca una de las cuotas (Básica, Plus, Premium, Joven o Senior).", null);
            return false;
        }
        int edad = Period.between(fecha, LocalDate.now()).getYears();
        if (cuota.equals("Joven") && edad > 25) {
            mostrarError("la cuota Joven es para socios de hasta 25 años.",
                    "Este socio tiene " + edad + " años. Elige otra cuota, por ejemplo Básica o Plus.", null);
            return false;
        }
        if (cuota.equals("Senior") && edad < 65) {
            mostrarError("la cuota Senior es para socios de 65 años o más.",
                    "Este socio tiene " + edad + " años. Elige otra cuota, por ejemplo Básica o Plus.", null);
            return false;
        }

        // El IBAN no es obligatorio (se puede añadir más tarde), pero si se escribe tiene que estar bien
        String iban = txtIban.getText().replace(" ", "").toUpperCase();
        if (!iban.isEmpty() && !iban.matches("ES\\d{22}")) {
            mostrarError("el IBAN no es correcto.",
                    "Debe empezar por ES y llevar 22 números después (24 caracteres en total). Puedes copiarlo de un recibo del banco.",
                    txtIban);
            return false;
        }
        return true;
    }

    private void mostrarError(String queHaPasado, String comoSolucionarlo, Control campo) {
        lblErrorTitulo.setText("No se ha podido dar de alta: " + queHaPasado);
        lblErrorTexto.setText(comoSolucionarlo);
        cajaError.setVisible(true);
        cajaError.setManaged(true);
        if (campo != null) {
            campo.getStyleClass().add("campo-error");
            campo.requestFocus();
        }
        lblEstado.setText("Revisa el formulario: hay un dato que corregir.");
    }

    private void ocultarError() {
        cajaError.setVisible(false);
        cajaError.setManaged(false);
        txtNombre.getStyleClass().remove("campo-error");
        txtApellidos.getStyleClass().remove("campo-error");
        dpFechaNacimiento.getStyleClass().remove("campo-error");
        txtIban.getStyleClass().remove("campo-error");
    }

    private String textoSeleccionado(ToggleGroup grupo) {
        Toggle t = grupo.getSelectedToggle();
        return t == null ? null : ((RadioButton) t).getText();
    }

    private String cuotaSeleccionada() {
        String texto = textoSeleccionado(grupoCuota);
        if (texto == null) {
            return null;
        }
        // "Joven (hasta 25 años)" -> "Joven"
        return texto.split(" ")[0];
    }

    private List<String> clasesSeleccionadas() {
        List<String> clases = new ArrayList<>();
        for (Node n : panelClases.getChildren()) {
            CheckBox c = (CheckBox) n;
            if (c.isSelected()) {
                clases.add(c.getText());
            }
        }
        return clases;
    }

    private boolean formularioTieneDatos() {
        return !txtNombre.getText().isBlank() || !txtApellidos.getText().isBlank()
                || dpFechaNacimiento.getValue() != null || !txtIban.getText().isBlank()
                || grupoCuota.getSelectedToggle() != null || !clasesSeleccionadas().isEmpty();
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellidos.clear();
        dpFechaNacimiento.setValue(null);
        dpFechaNacimiento.getEditor().clear();
        grupoSexo.selectToggle(null);
        cbProvincia.getSelectionModel().clearSelection();
        grupoCuota.selectToggle(null);
        for (Node n : panelClases.getChildren()) {
            ((CheckBox) n).setSelected(false);
        }
        txtIban.clear();
        ocultarError();
        txtNombre.requestFocus();
    }

    @FXML
    private void limpiarFormulario() {
        if (!formularioTieneDatos()) {
            limpiarCampos();
            return;
        }
        if (confirmar("Limpiar formulario", "¿Borrar los datos del formulario?",
                "Se borrará lo que has escrito en el formulario. Los socios ya guardados no se tocan.",
                "Borrar datos")) {
            limpiarCampos();
            socioEditando = null;
            lblEstado.setText("Formulario vacío.");
        }
    }

    @FXML
    private void cancelar() {
        limpiarFormulario();
    }

    @FXML
    private void nuevoSocio() {
        limpiarFormulario();
    }

    // ===================== ACCIONES SOBRE LA TABLA =====================

    private Socio socioSeleccionado() {
        Socio s = tablaSocios.getSelectionModel().getSelectedItem();
        if (s == null) {
            informar("Ningún socio seleccionado", "Selecciona primero un socio en la tabla de la derecha.");
        }
        return s;
    }

    @FXML
    private void teclaEnTabla(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) {
            darDeBaja();
        }
    }

    @FXML
    private void verFicha() {
        Socio s = socioSeleccionado();
        if (s != null) {
            informar("Ficha de " + s.getNombreCompleto(),
                    "Fecha de nacimiento: " + s.getFechaTexto()
                    + "\nSexo: " + (s.getSexo() == null ? "—" : s.getSexo())
                    + "\nProvincia: " + s.getProvincia()
                    + "\nCuota: " + s.getCuota()
                    + "\nClases: " + s.getClasesTexto()
                    + "\nIBAN: " + (s.getIban().isEmpty() ? "pendiente" : s.getIban()));
        }
    }

    @FXML
    private void editarSocio() {
        Socio s = socioSeleccionado();
        if (s != null) {
            cargarEnFormulario(s);
            socioEditando = s;
            lblEstado.setText("Editando a " + s.getNombreCompleto() + ". Pulsa «Dar de alta» para guardar los cambios.");
        }
    }

    @FXML
    private void duplicarSocio() {
        Socio s = socioSeleccionado();
        if (s != null) {
            cargarEnFormulario(s);
            txtNombre.clear();
            txtApellidos.clear();
            socioEditando = null;
            lblEstado.setText("Datos copiados de " + s.getNombreCompleto() + ". Escribe el nombre del nuevo socio.");
        }
    }

    @FXML
    private void copiarNombre() {
        Socio s = socioSeleccionado();
        if (s != null) {
            ClipboardContent contenido = new ClipboardContent();
            contenido.putString(s.getNombreCompleto());
            Clipboard.getSystemClipboard().setContent(contenido);
            lblEstado.setText("Nombre copiado: " + s.getNombreCompleto());
        }
    }

    @FXML
    private void renovarCuota() {
        Socio s = socioSeleccionado();
        if (s != null) {
            lblEstado.setText("Cuota de " + s.getNombreCompleto() + " renovada hasta el "
                    + LocalDate.now().plusMonths(1).format(FORMATO) + ".");
        }
    }

    @FXML
    private void darDeBaja() {
        Socio s = socioSeleccionado();
        if (s == null) {
            return;
        }
        if (confirmar("Dar de baja", "¿Dar de baja a " + s.getNombreCompleto() + "?",
                "Su ficha se quitará de la lista de socios. Esta acción no se puede deshacer.",
                "Dar de baja")) {
            socios.remove(s);
            actualizarContador();
            lblEstado.setText(s.getNombreCompleto() + " se ha dado de baja.");
        }
    }

    private void cargarEnFormulario(Socio s) {
        limpiarCampos();
        txtNombre.setText(s.getNombre());
        txtApellidos.setText(s.getApellidos());
        dpFechaNacimiento.setValue(s.getFechaNacimiento());
        seleccionarPorTexto(grupoSexo, s.getSexo());
        cbProvincia.setValue(s.getProvincia().equals("—") ? null : s.getProvincia());
        for (Toggle t : grupoCuota.getToggles()) {
            if (((RadioButton) t).getText().startsWith(s.getCuota())) {
                grupoCuota.selectToggle(t);
            }
        }
        for (Node n : panelClases.getChildren()) {
            CheckBox c = (CheckBox) n;
            c.setSelected(s.getClases().contains(c.getText()));
        }
        txtIban.setText(s.getIban());
    }

    private void seleccionarPorTexto(ToggleGroup grupo, String texto) {
        for (Toggle t : grupo.getToggles()) {
            if (((RadioButton) t).getText().equals(texto)) {
                grupo.selectToggle(t);
            }
        }
    }

    private void actualizarContador() {
        int n = sociosFiltrados == null ? socios.size() : sociosFiltrados.size();
        lblContador.setText(n == 1 ? "1 socio" : n + " socios");
    }

    // ===================== MENÚS =====================

    @FXML
    private void guardar() {
        lblEstado.setText("Cambios guardados.");
    }

    @FXML
    private void noDisponible() {
        lblEstado.setText("Esta opción todavía no está disponible en el prototipo.");
    }

    @FXML
    private void salir() {
        Platform.exit();
    }

    @FXML
    private void deshacer() {
        if (txtBuscar.getScene().getFocusOwner() instanceof TextInputControl campo) {
            campo.undo();
        }
    }

    @FXML
    private void cortar() {
        if (txtBuscar.getScene().getFocusOwner() instanceof TextInputControl campo) {
            campo.cut();
        }
    }

    @FXML
    private void copiar() {
        if (txtBuscar.getScene().getFocusOwner() instanceof TextInputControl campo) {
            campo.copy();
        } else if (tablaSocios.getSelectionModel().getSelectedItem() != null) {
            copiarNombre();
        }
    }

    @FXML
    private void pegar() {
        if (txtBuscar.getScene().getFocusOwner() instanceof TextInputControl campo) {
            campo.paste();
        }
    }

    @FXML
    private void buscar() {
        txtBuscar.requestFocus();
    }

    @FXML
    private void cambiarBarraHerramientas() {
        barraHerramientas.setVisible(chkVerHerramientas.isSelected());
        barraHerramientas.setManaged(chkVerHerramientas.isSelected());
    }

    @FXML
    private void cambiarBarraEstado() {
        barraEstado.setVisible(chkVerEstado.isSelected());
        barraEstado.setManaged(chkVerEstado.isSelected());
    }

    @FXML
    private void mostrarAyuda() {
        informar("Manual de uso",
                "Para dar de alta un socio rellena el formulario de la izquierda y pulsa «Dar de alta».\n"
                + "Los campos con * son obligatorios.\n\n"
                + "Para editar, duplicar o dar de baja un socio, haz clic derecho sobre él en la tabla.");
    }

    @FXML
    private void mostrarAtajos() {
        informar("Atajos de teclado",
                "Ctrl+N  Nuevo socio\nCtrl+S  Guardar\nCtrl+Intro  Dar de alta\nCtrl+E  Editar socio\n"
                + "Ctrl+R  Renovar cuota\nCtrl+F  Buscar socio\nSupr  Dar de baja (en la tabla)\nF1  Ayuda");
    }

    @FXML
    private void acercaDe() {
        informar("Acerca de GymFit Manager", "GymFit Manager 3.0 (rediseño)\nPrototipo para la Tarea 6-7 de Desarrollo de Interfaces.");
    }

    // ===================== DIÁLOGOS =====================

    private void informar(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    private boolean confirmar(String titulo, String pregunta, String explicacion, String textoBotonAccion) {
        ButtonType accion = new ButtonType(textoBotonAccion, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.WARNING, explicacion, cancelar, accion);
        alerta.setTitle(titulo);
        alerta.setHeaderText(pregunta);
        // Por seguridad, el botón por defecto es Cancelar
        ((Button) alerta.getDialogPane().lookupButton(accion)).setDefaultButton(false);
        ((Button) alerta.getDialogPane().lookupButton(cancelar)).setDefaultButton(true);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == accion;
    }
}
