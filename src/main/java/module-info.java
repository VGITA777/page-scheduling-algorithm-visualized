module com.prince.schedulingalgogui {
    requires javafx.controls;
    requires javafx.fxml;

    // Open the fxml packages
    opens com.prince.schedulingalgogui to javafx.fxml;
    opens com.prince.schedulingalgogui.views.homePage to javafx.fxml;
    opens com.prince.schedulingalgogui.views.loginPage to javafx.fxml;

    exports com.prince.schedulingalgogui;
}