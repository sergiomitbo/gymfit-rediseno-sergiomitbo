package es.medac.sergiomitbo.gymfit;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

public class MainController {

    // Menús y barras
    @FXML private ToolBar barraHerramientas;
    @FXML private HBox barraEstado;
    @FXML private CheckMenuItem chkVerHerramientas;
    @FXML private CheckMenuItem chkVerEstado;
    @FXML private TextField txtBuscar;
    @FXML private Label lblEstado;

    @FXML
    private void initialize() {
        lblEstado.setText("Listo");
    }

    // TODO: formulario de alta y tabla de socios
    @FXML
    private void darDeAlta() {
        noDisponible();
    }

    @FXML
    private void nuevoSocio() {
        noDisponible();
    }

    @FXML
    private void editarSocio() {
        noDisponible();
    }

    @FXML
    private void renovarCuota() {
        noDisponible();
    }

    @FXML
    private void darDeBaja() {
        noDisponible();
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
        informar("Manual de uso", "Pendiente de escribir.");
    }

    @FXML
    private void mostrarAtajos() {
        informar("Atajos de teclado",
                "Ctrl+N  Nuevo socio\nCtrl+S  Guardar\nCtrl+Intro  Dar de alta\nCtrl+E  Editar socio\n"
                + "Ctrl+R  Renovar cuota\nCtrl+F  Buscar socio\nF1  Ayuda");
    }

    @FXML
    private void acercaDe() {
        informar("Acerca de GymFit Manager", "GymFit Manager 3.0 (rediseño)\nPrototipo para la Tarea 6-7 de Desarrollo de Interfaces.");
    }

    private void informar(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}
