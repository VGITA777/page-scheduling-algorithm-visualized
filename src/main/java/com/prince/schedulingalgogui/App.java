package com.prince.schedulingalgogui;

import com.prince.schedulingalgogui.utils.ViewLocator;
import com.prince.schedulingalgogui.utils.ViewsLocation;
import com.prince.schedulingalgogui.views.loginPage.LoginPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.swing.*;

public class App extends Application {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 600;

    private static void setupStage(@NonNull Stage stage) {
        stage.setTitle("Scheduling Algorithm Visualizer");
        stage.setMinWidth(WIDTH);
        stage.setMinHeight(HEIGHT);
        stage.setOnCloseRequest(_ -> JOptionPane.showMessageDialog(null, "Goodbye, user!"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @SneakyThrows
    @Override
    public void start(Stage stage) {
        final FXMLLoader loader = ViewLocator.getLoader(ViewsLocation.LOGIN_PAGE);
        loader.setControllerFactory(_ -> new LoginPageController(stage));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        setupStage(stage);
        stage.show();
    }
}
