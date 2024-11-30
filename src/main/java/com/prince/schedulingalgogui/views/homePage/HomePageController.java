package com.prince.schedulingalgogui.views.homePage;

import com.prince.schedulingalgogui.schedulingAlgorithms.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.NonNull;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML
    private TableView<Object[]> dataTable;

    @FXML
    private RadioButton fifoRadioButton;

    @FXML
    private Spinner<Integer> frameCountSpinner;

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

    private StringProperty stringReferenceInputProperty;

    private ReadOnlyObjectProperty<Integer> frameCountProperty;

    private BooleanProperty realTimeResultCheckboxProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty isCurrentlySolvingProperty = new SimpleBooleanProperty(false);

    private final ObjectProperty<Schedulers> currentAlgorithmProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<ISchedulerFactory> schedulerFactoryProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<SchedulerResult> schedulerResultProperty = new SimpleObjectProperty<>();


    private final Stage stage;

    private final ToggleGroup currentAlgorithmGroup = new ToggleGroup();

    private final Schedulers schedulerToUse;

    private final PauseTransition debounce = new PauseTransition(Duration.millis(DEBOUNCE_DURATION_MILLIS));

    private static final String REFERENCE_INPUT_SEPARATOR = ",";

    private static final int DEBOUNCE_DURATION_MILLIS = 500;

    public HomePageController(@NonNull Stage stage, @NonNull Schedulers schedulerToUse) {
        this.stage = stage;
        this.schedulerToUse = schedulerToUse;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setup();
        setSchedulerProperty(schedulerToUse);

        // For debugging purposes
/*        schedulerResultProperty.addListener((_, _, result) -> Arrays.stream(result.getResult()).toList().forEach(d -> {
            StringBuilder stringBuilder = new StringBuilder();
            Arrays.stream(d).forEach(e -> stringBuilder.append((e == null) ? "x" : e).append(" "));
            System.out.println(stringBuilder);
        }));*/
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
        Platform.runLater(this::solve);
    }

    private void solve() {
        isCurrentlySolvingProperty.set(true);
        final Object[] referenceArray = getReferenceStringAsAnArrayOfObjects().orElse(new Object[0]);
        final int frameCount = frameCountSpinner.getValue();
        final Scheduler scheduler = schedulerFactoryProperty.get().createScheduler(frameCount, referenceArray);
        final SchedulerResult result = scheduler.start();
        schedulerResultProperty.set(result);
        isCurrentlySolvingProperty.set(false);
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
    }

    private void setupProperties() {
        stringReferenceInputProperty = stringReferenceInput.textProperty();
        realTimeResultCheckboxProperty = realTimeResultCheckbox.selectedProperty();
        frameCountProperty = frameCountSpinner.valueProperty();
        currentAlgorithmGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
            if (newToggle == fifoRadioButton) {
                currentAlgorithmProperty.set(Schedulers.FIFO);
            } else {
                currentAlgorithmProperty.set(Schedulers.LRU);
            }
        });
    }

    /*
     *   Should be called after the properties are set up.
     * */
    private void setupSchedulerFactory() {
        currentAlgorithmProperty.addListener((_, _, newScheduler) -> {
            setSchedulerProperty(newScheduler);
        });
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
        ChangeListener<Object> realTimeSolverListener = (_, _, _) -> {
            // Checks if realtime updates are enabled.
            if (realTimeResultCheckboxProperty.get()) {
                debounce.setOnFinished(_ -> startSolver());
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
            }
        };

        schedulerResultProperty.addListener(realTimeResultListener);
    }

    /*
     *  For Spinner Setup
     * */

    private void setUpFrameCountSpinner() {
        final SpinnerValueFactory<Integer> frameCountSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 3);
        frameCountSpinner.setValueFactory(frameCountSpinnerValueFactory);
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

    /*
     *  For Table View Setup
     * */
    private void populateDataForTableView(@NonNull SchedulerResult schedulerResult) {
        // Ensure result is valid
        if (schedulerResult.getResult() == null || schedulerResult.getResult().length == 0) {
            return;
        }

        Platform.runLater(() -> {
            // Clear existing columns and data
            dataTable.getColumns().clear();
            dataTable.getItems().clear();

            // Add Frame Number Column
            TableColumn<Object[], String> frameColumn = getStringTableColumn();
            frameColumn.setSortable(false); // Disable sorting
            dataTable.getColumns().add(frameColumn);

            // Create Additional Columns for Object[] Data
            Object[][] result = schedulerResult.getResult();
            Object[] reference = schedulerResult.getStringReference();
            PageResultStatus[] statuses = schedulerResult.getPageResultStatuses();
            int columnCount = result[0].length;

            for (int i = 0; i < columnCount; i++) {
                final int columnIndex = i; // Required for lambda scope
                TableColumn<Object[], String> column = new TableColumn<>(reference[i].toString());
                column.setCellValueFactory(cellData -> {
                    Object[] row = cellData.getValue();
                    return new SimpleStringProperty(row[columnIndex] != null ? row[columnIndex].toString() : ""); // Handle nulls
                });
                column.setSortable(false); // Disable sorting
                dataTable.getColumns().add(column);
            }

            // Populate TableView items
            ObservableList<Object[]> data = FXCollections.observableArrayList(result);

            // Add synthetic row for PageResultStatus
            Object[] statusRow = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                statusRow[i] = (i < statuses.length)
                        ? (statuses[i] == PageResultStatus.PAGE_HIT ? "^" : "*") // Add symbol for Page Hit or Page Fault
                        : ""; // Leave blank if no status is available
            }
            data.add(statusRow);

            dataTable.setItems(data);
        });
    }

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
     *  Helper Methods Up Ahead
     * */
    private void setSchedulerProperty(@NonNull Schedulers schedulerToUse) {
        switch (schedulerToUse) {
            case FIFO:
                schedulerFactoryProperty.set((pageFrames, reference) -> new FifoScheduler(pageFrames, reference, null));
                break;
            case LRU:
                schedulerFactoryProperty.set((pageFrames, reference) -> new LruScheduler(pageFrames, reference, null));
                break;
        }
    }

    @NonNull
    private Optional<Object[]> getReferenceStringAsAnArrayOfObjects() {
        final String[] referenceString = Arrays.stream(stringReferenceInputProperty.get().split(REFERENCE_INPUT_SEPARATOR)).map(String::trim).filter(d -> !d.isBlank()).toArray(String[]::new);
        final Object[] referenceArray = new Object[referenceString.length];
        System.arraycopy(referenceString, 0, referenceArray, 0, referenceString.length);
        return Optional.of(referenceArray);
    }

}

