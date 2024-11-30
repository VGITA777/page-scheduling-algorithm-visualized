package com.prince.schedulingalgogui.views.homePage;

import com.prince.schedulingalgogui.schedulingAlgorithms.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.NonNull;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML
    private TableView<SchedulerResult> dataTable;

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

    // Default of a FIFO Scheduler Factory
    private ObjectProperty<ISchedulerFactory> schedulerFactoryProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<SchedulerResult> schedulerResultProperty = new SimpleObjectProperty<>();

    private final ChangeListener<Object> realTimeSolverListener = (_, _, _) -> {
        if (realTimeResultCheckboxProperty.get()) {
            startSolver();
        }
    };

    private final Stage stage;

    private final ToggleGroup currentAlgorithmGroup = new ToggleGroup();

    private final Schedulers schedulerToUse;

    private static final String REFERENCE_INPUT_SEPARATOR = ",";

    public HomePageController(@NonNull Stage stage, @NonNull Schedulers schedulerToUse) {
        this.stage = stage;
        this.schedulerToUse = schedulerToUse;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setup();

        setSchedulerProperty(schedulerToUse);

        schedulerResultProperty.addListener((_, _, result) -> Arrays.stream(result.getResult()).toList().forEach(d -> {
            StringBuilder stringBuilder = new StringBuilder();
            Arrays.stream(d).forEach(e -> stringBuilder.append((e == null) ? "x" : e).append(" "));
            System.out.println(stringBuilder);
        }));
    }

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
        final Object[] referenceArray = getReference().orElse(new Object[0]);
        final int frameCount = frameCountSpinner.getValue();
        final Scheduler scheduler = schedulerFactoryProperty.get().createScheduler(frameCount, referenceArray);
        final SchedulerResult result = scheduler.start();
        schedulerResultProperty.set(result);
        isCurrentlySolvingProperty.set(false);
    }

    @NonNull
    private Optional<Object[]> getReference() {
        final String[] referenceString = stringReferenceInputProperty.get().split(REFERENCE_INPUT_SEPARATOR);
        final Object[] referenceArray = new Object[referenceString.length];
        System.arraycopy(referenceString, 0, referenceArray, 0, referenceString.length);
        return Optional.of(referenceArray);
    }


    /*
     *
     *   Setup Methods Up Ahead
     *
     * */
    private void setup() {
        setUpCurrentAlgorithmRadioButtons();
        setUpFrameCountSpinner();
        setupProperties();
        setupSchedulerFactory();
        setUpRealTimeResults();
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
    private void setUpRealTimeResults() {
        frameCountProperty.addListener(realTimeSolverListener);
        schedulerFactoryProperty.addListener(realTimeSolverListener);
        stringReferenceInputProperty.addListener(realTimeSolverListener);
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
    private void setupTableView() {

    }

    @NonNull
    private TableColumn<SchedulerResult, Integer> createFrameNumberColumn(SchedulerResult schedulerResult) {
        return null;
    }
}

