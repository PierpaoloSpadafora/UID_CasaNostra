package informatica.unical.spadafora.casanostra.model;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartManager {

    // ===================== Istanze Singleton =====================

    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Variabili =====================

    private List<Transaction> transactions;

    // ===================== Metodi ausiliari =====================

    private Map<String, Map<String, Double>> aggregateTransactionData() {
        Map<String, Map<String, Double>> aggregateData = new HashMap<>();
        for (Transaction transaction : transactions) {
            String mainCategory = transaction.getTipo().equals("EXPENSE") ? "Expenses" : "Incomes";
            String subCategory = transaction.getCategoria();
            aggregateData.putIfAbsent(mainCategory, new HashMap<>());
            Map<String, Double> subMap = aggregateData.get(mainCategory);
            subMap.put(subCategory, subMap.getOrDefault(subCategory, 0.0) + transaction.getImporto());
        }
        return aggregateData;
    }

    private void populateBarChart(StackedBarChart<String, Number> stackedBarChart, Map<String, Map<String, Double>> aggregateData) {
        for (Map.Entry<String, Map<String, Double>> mainEntry : aggregateData.entrySet()) {
            for (Map.Entry<String, Double> subEntry : mainEntry.getValue().entrySet()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(mainEntry.getKey() + " - " + subEntry.getKey());
                series.getData().add(new XYChart.Data<>(mainEntry.getKey(), subEntry.getValue()));
                stackedBarChart.getData().add(series);
            }
        }
    }

    private void createAndAddLabel(VBox vbox, Transaction transaction, int somma) {
        double percentuale = (transaction.getImporto() / somma) * 100;
        String percentualeArrotondata = String.format("%.2f", percentuale);
        String labelText = transaction.getCategoria() + " - " + transaction.getNome() + " - " + transaction.getImporto() + "€ - " + percentualeArrotondata + "%";
        Label label = new Label(labelText);
        vbox.getChildren().add(label);
    }

    private void generateReport(VBox vbox, String tipoTransazione, String totalLabel) {
        vbox.getChildren().clear();
        int somma = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getTipo().equals(tipoTransazione)) {
                somma += (int) transaction.getImporto();
            }
        }

        for (Transaction transaction : transactions) {
            if (transaction.getTipo().equals(tipoTransazione)) {
                createAndAddLabel(vbox, transaction, somma);
            }
        }

        Label label = new Label(totalLabel + somma + "€");
        label.setStyle("-fx-font-weight: bold; -fx-font-style: italic;");
        vbox.getChildren().add(label);
    }

    public void reportTestualeSpese(VBox vboxSpese) {
        generateReport(vboxSpese, "EXPENSE", "Total expenses: ");
    }

    // ===================== Metodi principali =====================

    public StackedBarChart<String, Number> generateBarChart(String date1, String date2) {

        transactions = databaseHandler.trovaTransazioniTraDueDate(date1, date2);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);

        Map<String, Map<String, Double>> aggregateData = aggregateTransactionData();

        populateBarChart(stackedBarChart, aggregateData);

        return stackedBarChart;
    }

    public void reportTestualeIncassi(VBox vboxIncassi) {
        generateReport(vboxIncassi, "INCOME", "Total income: ");
    }




}