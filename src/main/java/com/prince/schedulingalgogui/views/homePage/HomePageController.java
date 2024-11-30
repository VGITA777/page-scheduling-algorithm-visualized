package com.prince.schedulingalgogui.views.homePage;

import com.prince.schedulingalgogui.schedulingAlgorithms.Schedulers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.NonNull;

import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML
    private TableView<?> dataTable;

    @FXML
    private TableColumn<?, ?> dataTableFramesColumn;

    @FXML
    private RadioButton fifoRadioButton;

    @FXML
    private Spinner<?> frameCountSpinner;

    @FXML
    private Button homeButton;

    @FXML
    private RadioButton lruRadioButton;

    @FXML
    private CheckBox realTimeResultCheckbox;

    @FXML
    private Button resetButton;

    @FXML
    private Button solveButton;

    @FXML
    private TextField stringReferenceInput;

    private final Stage stage;

    private final ToggleGroup currentAlgorithmGroup = new ToggleGroup();

    private final Schedulers schedulerToUse;

    public HomePageController(@NonNull Stage stage, @NonNull Schedulers schedulerToUse) {
        this.stage = stage;
        this.schedulerToUse = schedulerToUse;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpCurrentAlgorithmRadioButtons();
    }

    /*
    *   For Current Algorithm Radio Buttons Setup
    * */
    private void setUpCurrentAlgorithmRadioButtons() {
        addCurrentAlgorithmRadioButtonsToToggleGroup();
        determineWhichRadioButtonShouldBeSelectedInAlgorithmRadioButtons();
    }

    private void addCurrentAlgorithmRadioButtonsToToggleGroup() {
        fifoRadioButton.setToggleGroup(currentAlgorithmGroup);
        lruRadioButton.setToggleGroup(currentAlgorithmGroup);
    }

    private void determineWhichRadioButtonShouldBeSelectedInAlgorithmRadioButtons() {
        switch (schedulerToUse) {
            case FIFO:
                fifoRadioButton.setSelected(true);
                break;
            case LRU:
                lruRadioButton.setSelected(true);
                break;
        }
    }

}

