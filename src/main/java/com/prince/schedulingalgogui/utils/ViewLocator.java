package com.prince.schedulingalgogui.utils;

import com.prince.schedulingalgogui.App;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import lombok.NonNull;
import lombok.SneakyThrows;

public class ViewLocator {

  @SneakyThrows
  @NonNull
  public static FXMLLoader getLoader(@NonNull ViewsLocation location) {
    return new FXMLLoader(Objects.requireNonNull(App.class.getResource(location.getLocation())));
  }

  @SneakyThrows
  @NonNull
  public static FXMLLoader getLoader(@NonNull ViewsLocation location,
      @NonNull ResourceBundle resourceBundle) {
    return new FXMLLoader(Objects.requireNonNull(App.class.getResource(location.getLocation())),
        resourceBundle);
  }
}
