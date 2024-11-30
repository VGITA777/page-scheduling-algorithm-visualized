package com.prince.schedulingalgogui;

import com.prince.schedulingalgogui.utils.ViewLocator;
import com.prince.schedulingalgogui.utils.ViewsLocation;
import com.prince.schedulingalgogui.views.homePage.HomePageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.SneakyThrows;

public class App extends Application {
    private static void setupStage(@NonNull Stage stage) {
        stage.setTitle("Scheduling Algorithm Visualizer");
        stage.setMinWidth(1100);
        stage.setMinHeight(600);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @SneakyThrows
    @Override
    public void start(Stage stage) {
        final FXMLLoader loader = ViewLocator.getLoader(ViewsLocation.LOGIN_PAGE);
        loader.setControllerFactory(_ -> new HomePageController(stage));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        setupStage(stage);
        stage.show();
    }
}
