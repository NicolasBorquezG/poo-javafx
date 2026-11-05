package cl.elprofecarreno.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static final double HOME_WIDTH = 850;
    public static final double HOME_HEIGHT = 650;

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(getLoadFXML("/views/home"), HOME_WIDTH, HOME_HEIGHT);
        stage.setTitle("Biblioteca");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void setRoot(Parent parent) {
        scene.setRoot(parent);
    }

    public static void setRootAndResize(Parent parent, double width, double height) {
        scene.setRoot(parent);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
    }

    public static Parent getLoadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}