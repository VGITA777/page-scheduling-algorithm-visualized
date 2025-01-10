package com.prince.schedulingalgogui.utils;

import com.prince.schedulingalgogui.schedulingAlgorithms.Schedulers;
import com.prince.schedulingalgogui.views.homePage.HomePageController;
import com.prince.schedulingalgogui.views.loginPage.LoginPageController;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.NonNull;
import lombok.SneakyThrows;

public class Navigator {

  private static Navigator INSTANCE = null;

  private Navigator() {
  }

  public static Navigator getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Navigator();
    }
    return INSTANCE;
  }

  @SneakyThrows
  public void navigateToLoginPage(@NonNull Stage stage) {
    navigateTo(stage, ViewsLocation.LOGIN_PAGE, clazz -> new LoginPageController(stage));
  }

  public void navigateToHomePageWithFifo(@NonNull Stage stage) {
    navigateToHomePage(stage, Schedulers.FIFO);
  }

  public void navigateToHomePageWithLru(@NonNull Stage stage) {
    navigateToHomePage(stage, Schedulers.LRU);
  }

  @SneakyThrows
  private void navigateToHomePage(@NonNull Stage stage, @NonNull Schedulers scheduler) {
    navigateTo(stage, ViewsLocation.HOME_PAGE, clazz -> new HomePageController(stage, scheduler));
  }

  @SneakyThrows
  private void navigateTo(@NonNull Stage stage, @NonNull ViewsLocation viewsLocation,
      @NonNull Callback<Class<?>, Object> controllerFactory) {
    Platform.runLater(() -> {
      final FXMLLoader loader = ViewLocator.getLoader(viewsLocation);
      loader.setControllerFactory(controllerFactory);
      final Parent root;
      try {
        root = loader.load();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      final Scene scene = new Scene(root);
      stage.setScene(scene);
    });
  }
}
