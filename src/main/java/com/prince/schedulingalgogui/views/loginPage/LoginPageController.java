package com.prince.schedulingalgogui.views.loginPage;

import com.prince.schedulingalgogui.utils.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class LoginPageController {

  private static final Navigator navigator = Navigator.getInstance();
  private final Stage stage;

  public LoginPageController(Stage stage) {
    this.stage = stage;
  }

  @FXML
  void onFifoButtonClick(ActionEvent event) {
    navigator.navigateToHomePageWithFifo(stage);
  }

  @FXML
  void onLruButtonClick(ActionEvent event) {
    navigator.navigateToHomePageWithLru(stage);
  }
}
