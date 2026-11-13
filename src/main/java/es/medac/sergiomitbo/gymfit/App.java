package es.medac.sergiomitbo.gymfit;

import java.util.Locale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("main.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 800);
        scene.getStylesheets().add(App.class.getResource("estilos.css").toExternalForm());

        stage.setTitle("GymFit Manager · Alta de socios");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Para que el calendario y las fechas salgan en español
        Locale.setDefault(Locale.of("es", "ES"));
        launch();
    }
}
