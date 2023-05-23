package phestrix.factoryapplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import phestrix.Factory.GUI.FXMLS.MainWindowController;
import phestrix.Factory.GUI.core.UICore;

import java.util.Objects;

public class FactoryApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        stage.setTitle("Factory");
        stage.setScene(new Scene(root));
        MainWindowController controller = loader.getController();

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("factory.png")).toExternalForm()));
        stage.setResizable(false);
        stage.setOnCloseRequest((e) -> {
            UICore.disableFactory();
            Platform.exit();
        });
        stage.show();

        controller.initSliders();
        controller.setRootStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}

