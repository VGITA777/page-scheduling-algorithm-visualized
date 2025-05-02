package com.prince.schedulingalgogui.views.homePage;

import com.prince.schedulingalgogui.schedulingAlgorithms.*;
import com.prince.schedulingalgogui.utils.Navigator;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.NonNull;

import java.net.URL;
import java.util.*;

public class HomePageController implements Initializable {

    private static final Navigator navigator = Navigator.getInstance();
    private static final String REFERENCE_INPUT_SEPARATOR = ",";
    private static final int DEBOUNCE_DURATION_MILLIS = 500;
    private static final SpinnerValueFactory<Integer> FRAME_COUNT_SPINNER_VALUE_FACTORY = new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1, 100, 3);
    private final BooleanProperty isCurrentlySolvingProperty = new SimpleBooleanProperty(false);
    private final ObjectProperty<Schedulers> currentAlgorithmProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ISchedulerFactory> schedulerFactoryProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<SchedulerResult> schedulerResultProperty = new SimpleObjectProperty<>();
    private final Stage stage;
    private final ToggleGroup currentAlgorithmGroup = new ToggleGroup();
    private final Schedulers schedulerToUse;
    private final PauseTransition debounce = new PauseTransition(
            Duration.millis(DEBOUNCE_DURATION_MILLIS));
    @FXML
    private ProgressBar loadingIndicator;
    @FXML
    private TableView<Object[]> dataTable;
    @FXML
    private RadioButton fifoRadioButton;
    @FXML
    private Spinner<Integer> frameCountSpinner;
    @FXML
    private RadioButton lruRadioButton;
    @FXML
    private CheckBox realTimeResultCheckbox;
    @FXML
    private Button homeButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button solveButton;
    @FXML
    private TextField stringReferenceInput;
    @FXML
    private Text hitsText;
    @FXML
    private Text faultsText;
    @FXML
    private Text referenceStringLengthText;
    @FXML
    private Text hitPercentageText;
    @FXML
    private Text faultPercentageText;
    private StringProperty stringReferenceInputProperty;
    private ReadOnlyObjectProperty<Integer> frameCountProperty;
    private BooleanProperty realTimeResultCheckboxProperty = new SimpleBooleanProperty(false);

    public HomePageController(@NonNull Stage stage, @NonNull Schedulers schedulerToUse) {
        this.stage = stage;
        this.schedulerToUse = schedulerToUse;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setup();
        setSchedulerProperty(schedulerToUse);
    }

    /*
     *
     *   Class Methods Up Ahead
     *
     * */

    /*
     *   Should be used for thread safety.
     * */
    private void startSolver() {
        final Thread thread = Thread.ofVirtual().unstarted(this::solve);
        thread.setDaemon(true);
        thread.start();
    }

    private void solve() {
        isCurrentlySolvingProperty.set(true);
        final Object[] referenceArray = getReferenceStringAsAnArrayOfObjects().orElse(new Object[0]);
        final int frameCount = frameCountSpinner.getValue();
        final Scheduler scheduler = schedulerFactoryProperty.get()
                                                            .createScheduler(frameCount, referenceArray);
        final SchedulerResult result = scheduler.start();
        schedulerResultProperty.set(result);
    }

    @FXML
    public void onSolveButtonClicked() {
        startSolver();
    }

    @FXML
    public void onResetButtonClick() {
        startResetAllData();
    }

    @FXML
    public void onHomeButtonClick() {
        navigator.navigateToLoginPage(stage);
    }

    /*
     *
     *   Setup Methods Up Ahead
     *
     * */
    private void setup() {
        setupProperties();
        setupSchedulerFactory();
        setUpCurrentAlgorithmRadioButtons();
        setUpFrameCountSpinner();
        setUpRealTimeSolverListener();
        setUpRealTimeResultListener();
        setupLoadingIndicator();
        setUpSolveButtonSettings();
        setUpResetButtonSettings();
    }

    private void setupProperties() {
        stringReferenceInputProperty = stringReferenceInput.textProperty();
        realTimeResultCheckboxProperty = realTimeResultCheckbox.selectedProperty();
        frameCountProperty = frameCountSpinner.valueProperty();
        currentAlgorithmGroup.selectedToggleProperty()
                             .addListener((observable, oldToggle, newToggle) -> {
                                 if (newToggle == fifoRadioButton) {
                                     currentAlgorithmProperty.set(Schedulers.FIFO);
                                 } else {
                                     currentAlgorithmProperty.set(Schedulers.LRU);
                                 }
                             });
    }

    /*
     *   Setups for the buttons
     * */
    private void setUpSolveButtonSettings() {
        // Check for the initial value of the checkbox.
        // And set the solve button to be disabled if the checkbox is checked.
        solveButton.setDisable(realTimeResultCheckboxProperty.get());

        // Add a listener to the checkbox to disable the solve button if the checkbox is checked.
        realTimeResultCheckboxProperty.addListener(
                (observable, oldValue, newValue) -> solveButton.setDisable(newValue));
    }

    private void setUpResetButtonSettings() {
        // Check for the initial value of the input field.
        resetButton.setDisable(stringReferenceInputProperty.get().isBlank());

        // Add a listener to the input field to disable the reset button if the input field is blank.
        stringReferenceInputProperty.addListener(
                (observable, oldValue, newValue) -> resetButton.setDisable(newValue.isBlank()));
    }

    /*
     *   Should be called after the properties are set up.
     * */
    private void setupSchedulerFactory() {
        currentAlgorithmProperty.addListener(
                (observable, oldValue, newScheduler) -> setSchedulerProperty(newScheduler));
    }

    /*
     *   For Real Time Results Setup
     * */
    private void setUpRealTimeSolverListener() {
        /*
         *   This listener is used to listen to changes in frameCountProperty, schedulerFactoryProperty, and stringReferenceProperty.
         *   If this object is changed, it means that different parameters have been set for the scheduler.
         *   Which means that we must start the solver again.
         * */
        ChangeListener<Object> realTimeSolverListener = (observable, oldValue, newValue) -> {
            // Checks if realtime updates are enabled.
            if (realTimeResultCheckboxProperty.get()) {
                debounce.setOnFinished(actionEvent -> startSolver());
                debounce.playFromStart();
            }
        };

        // If any of this property changes, it means that different parameters have been set for the scheduler.
        // Which means that we must start the solver again.
        frameCountProperty.addListener(realTimeSolverListener);
        schedulerFactoryProperty.addListener(realTimeSolverListener);
        stringReferenceInputProperty.addListener(realTimeSolverListener);
    }

    private void setUpRealTimeResultListener() {
        final ChangeListener<SchedulerResult> realTimeResultListener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateDataForTableView(newValue);
                startUpdateDescriptiveResults(newValue);
            }
        };

        schedulerResultProperty.addListener(realTimeResultListener);
    }

    /*
     *  For Spinner Setup
     * */
    private void setUpFrameCountSpinner() {
        frameCountSpinner.setValueFactory(FRAME_COUNT_SPINNER_VALUE_FACTORY);
    }

    /*
     *   For Current Algorithm Radio Buttons Setup
     * */
    private void setUpCurrentAlgorithmRadioButtons() {
        addCurrentAlgorithmRadioButtonsToToggleGroup();
        determineWhichRadioButtonShouldBeSelectedInAlgorithmRadioButtons();
    }

    /*
     *  For Current Algorithm Radio Buttons Setup
     * */
    private void addCurrentAlgorithmRadioButtonsToToggleGroup() {
        fifoRadioButton.setToggleGroup(currentAlgorithmGroup);
        lruRadioButton.setToggleGroup(currentAlgorithmGroup);
    }

    /*
     *  For Current Algorithm Radio Buttons Setup
     * */
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

    private void setupLoadingIndicator() {
        isCurrentlySolvingProperty.addListener(
                (observableValue, oldIsSolving, isSolving) -> Platform.runLater(() -> {
                    loadingIndicator.setVisible(isSolving);
                    solveButton.setDisable(isSolving || realTimeResultCheckboxProperty.get());
                    resetButton.setDisable(isSolving || stringReferenceInputProperty.get().isBlank());
                    homeButton.setDisable(isSolving);
                }));
    }

    /*
     *  For Table View Data Population
     * */
    private void populateDataForTableView(@NonNull SchedulerResult schedulerResult) {
        if (isResultValid(schedulerResult)) {
            Thread thread = Thread.ofVirtual().unstarted(() -> {
                startClearTableView();
                addFrameNumberColumn();
                addDataColumns(schedulerResult);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                populateTableWithData(schedulerResult);
            });

            thread.setDaemon(true);
            thread.start();
        }
    }

    private boolean isResultValid(@NonNull SchedulerResult schedulerResult) {
        return schedulerResult.getResult() != null && schedulerResult.getResult().length > 0;
    }

    private void addFrameNumberColumn() {
        TableColumn<Object[], String> frameColumn = getStringTableColumn();
        frameColumn.setSortable(false);
        Platform.runLater(() -> dataTable.getColumns().add(frameColumn));
    }

    private void addDataColumns(@NonNull SchedulerResult schedulerResult) {
        Object[][] result = schedulerResult.getResult();
        Object[] reference = schedulerResult.getStringReference();
        int columnCount = result[0].length;
        List<TableColumn<Object[], String>> resultsToAdd = new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {
            final int columnIndex = i;
            TableColumn<Object[], String> column = new TableColumn<>(reference[i].toString());
            column.setCellValueFactory(cellData -> {
                Object[] row = cellData.getValue();
                return new SimpleStringProperty(
                        row[columnIndex] != null ? row[columnIndex].toString() : "");
            });
            column.setSortable(false);
            resultsToAdd.addLast(column);
        }

        Platform.runLater(() -> dataTable.getColumns().addAll(resultsToAdd));
    }

    private void populateTableWithData(@NonNull SchedulerResult schedulerResult) {
        Object[][] result = schedulerResult.getResult();
        PageResultStatus[] statuses = schedulerResult.getPageResultStatuses();
        int columnCount = result[0].length;

        ObservableList<Object[]> data = FXCollections.observableArrayList(result);
        Object[] statusRow = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            statusRow[i] = (i < statuses.length)
                    ? (statuses[i] == PageResultStatus.PAGE_HIT ? "^" : "*")
                    : "";
        }
        data.add(statusRow);

        Platform.runLater(() -> {
            dataTable.setItems(data);
            isCurrentlySolvingProperty.set(false);
        });
    }

    @NonNull
    private TableColumn<Object[], String> getStringTableColumn() {
        TableColumn<Object[], String> frameColumn = new TableColumn<>("Frame #");
        frameColumn.setCellValueFactory(cellData -> {
            ObservableList<Object[]> items = dataTable.getItems();
            int index = items.indexOf(cellData.getValue());

            // Check if it's the last row (synthetic row for statuses)
            if (index == items.size() - 1) {
                return new SimpleStringProperty("Status");
            }

            return new SimpleStringProperty(index != -1 ? "Frame " + (index + 1) : "Unknown");
        });
        return frameColumn;
    }

    /*
     *   Setup for updating Descriptive Results
     * */

    private void startUpdateDescriptiveResults(@NonNull SchedulerResult schedulerResult) {
        Platform.runLater(() -> updateDescriptiveResults(schedulerResult));
    }

    private void updateDescriptiveResults(@NonNull SchedulerResult schedulerResult) {
        hitsText.setText(String.valueOf(schedulerResult.getPageHit()));
        faultsText.setText(String.valueOf(schedulerResult.getPageFault()));
        referenceStringLengthText.setText(String.valueOf(schedulerResult.getStringReference().length));
        hitPercentageText.setText(String.format(
                "%.2f",
                calculateHitPercentage(schedulerResult.getPageHit(), schedulerResult.getPageFault()).isNaN()
                        ? 0 : calculateHitPercentage(
                        schedulerResult.getPageHit(),
                        schedulerResult.getPageFault()
                                                    )
                                               ));
        faultPercentageText.setText(String.format(
                "%.2f",
                calculateFaultPercentage(
                        schedulerResult.getPageHit(),
                        schedulerResult.getPageFault()
                                        ).isNaN() ? 0
                        : calculateFaultPercentage(
                        schedulerResult.getPageHit(),
                        schedulerResult.getPageFault()
                                                  )
                                                 ));
    }

    /*
     *  Helper Methods Up Ahead
     * */

    private void setSchedulerProperty(@NonNull Schedulers schedulerToUse) {
        switch (schedulerToUse) {
            case FIFO:
                schedulerFactoryProperty.set(
                        (pageFrames, reference) -> new FifoScheduler(pageFrames, reference, null));
                break;
            case LRU:
                schedulerFactoryProperty.set(
                        (pageFrames, reference) -> new LruScheduler(pageFrames, reference, null));
                break;
        }
    }

    @NonNull
    private Optional<Object[]> getReferenceStringAsAnArrayOfObjects() {
        final String[] referenceString = Arrays.stream(
                                                       stringReferenceInputProperty.get().split(REFERENCE_INPUT_SEPARATOR)).map(String::trim)
                                               .filter(d -> !d.isBlank()).toArray(String[]::new);
        final Object[] referenceArray = new Object[referenceString.length];
        System.arraycopy(referenceString, 0, referenceArray, 0, referenceString.length);
        return Optional.of(referenceArray);
    }

    /*
     *   Should be used for thread safety.
     * */

    private void startResetAllData() {
        Platform.runLater(this::resetAllData);
    }

    private void resetAllData() {
        hitsText.setText("0");
        faultsText.setText("0");
        referenceStringLengthText.setText("0");
        hitPercentageText.setText("0%");
        faultPercentageText.setText("0%");
        stringReferenceInput.clear();
        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        frameCountSpinner.setValueFactory(FRAME_COUNT_SPINNER_VALUE_FACTORY);
    }

    @NonNull
    private Double calculateHitPercentage(int hits, int faults) {
        return (double) hits / (hits + faults) * 100;
    }

    @NonNull
    private Double calculateFaultPercentage(int hits, int faults) {
        return (double) faults / (hits + faults) * 100;
    }

    private void startClearTableView() {
        Platform.runLater(() -> {
            dataTable.getColumns().clear();
            dataTable.getItems().clear();
        });
    }
}

