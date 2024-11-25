module com.prince.schedulingalgogui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.prince.schedulingalgogui to javafx.fxml;
    exports com.prince.schedulingalgogui;
}