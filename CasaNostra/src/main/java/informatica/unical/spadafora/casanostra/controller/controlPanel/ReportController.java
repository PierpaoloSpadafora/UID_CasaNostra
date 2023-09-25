package informatica.unical.spadafora.casanostra.controller.controlPanel;

import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.ChartManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ReportController {

    // ===================== Costanti per messaggi e percorsi =====================

    private static final String GO_BACK_PATH = "controlPanelView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Componenti UI =====================

    @FXML
    private DatePicker endDate;
    @FXML
    private DatePicker startDate;
    @FXML
    private AnchorPane chartPane;
    @FXML
    private VBox elencoIncassi;
    @FXML
    private VBox elencoSpese;
    @FXML
    private ScrollPane scollPaneTransazioni1;
    @FXML
    private ScrollPane scollPaneTransazioni2;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final ChartManager chartManager = new ChartManager();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        initializeDatePickers();
        initializeScrollPanes();
        initializeReports();
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    // ===================== Azioni Principali =====================

    private void initializeDatePickers() {
        startDate.setValue(java.time.LocalDate.now().withDayOfMonth(1));
        endDate.setValue(java.time.LocalDate.now());
    }

    private void initializeScrollPanes() {
        scollPaneTransazioni1.setMinHeight(200);
        scollPaneTransazioni2.setMinHeight(200);
        sceneHandler.addHeightListenerToScrollPane(scollPaneTransazioni1, 200);
        sceneHandler.addHeightListenerToScrollPane(scollPaneTransazioni2, 200);
    }

    private void initializeReports() {
        Platform.runLater(() -> {
            String date1 = startDate.getValue().toString();
            String date2 = endDate.getValue().toString();
            generaGrafico(date1, date2);
            chartManager.reportTestualeSpese(elencoSpese);
            chartManager.reportTestualeIncassi(elencoIncassi);
        });
    }

    private void updateReports() {
        String date1 = startDate.getValue().toString();
        String date2 = endDate.getValue().toString();
        generaGrafico(date1, date2);
        chartManager.reportTestualeSpese(elencoSpese);
        chartManager.reportTestualeIncassi(elencoIncassi);
    }

    // ===================== Utility =====================

    private void generaGrafico(String date1, String date2) {
        StackedBarChart<String, Number> newChart = chartManager.generateBarChart(date1, date2);
        newChart.getStyleClass().add("transactionChart");
        chartPane.getChildren().clear();
        chartPane.getChildren().add(newChart);
        AnchorPane.setTopAnchor(newChart, 0.0);
        AnchorPane.setBottomAnchor(newChart, 0.0);
        AnchorPane.setLeftAnchor(newChart, 0.0);
        AnchorPane.setRightAnchor(newChart, 0.0);
    }

    // ===================== Azioni principali =====================

    @FXML
    void searchTransactionSubmit() {
        updateReports();
    }

}