module com.prince.schedulingalgogui {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;

    // Open the fxml packages
    opens com.prince.schedulingalgogui to javafx.fxml;
    opens com.prince.schedulingalgogui.views.homePage to javafx.fxml;
    opens com.prince.schedulingalgogui.views.loginPage to javafx.fxml;
    opens com.prince.schedulingalgogui.fonts to javafx.fxml;

    exports com.prince.schedulingalgogui;
}