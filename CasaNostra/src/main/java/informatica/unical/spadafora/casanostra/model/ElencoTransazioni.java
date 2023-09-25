package informatica.unical.spadafora.casanostra.model;

import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import javafx.scene.layout.AnchorPane;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.List;

public class ElencoTransazioni {

    // ===================== Istanze Singleton =====================
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private final SceneHandler sceneHandler = SceneHandler.getInstance();

    // ===================== Variabili =====================

    TableView<Transaction> table = new TableView<>();

    int currentTransaction = -1;

    // ===================== Costruttore =====================

    public ElencoTransazioni() {
        setupTable();
    }

    // ===================== Metodi ausiliari =====================

    private <T> TableCell<Transaction, T> customCellFactory() {
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                String text = item.toString();
                setText(text);

                setOnMouseClicked(event -> {
                    TableColumn<Transaction, ?> column = getTableColumn();
                    if ("Note".equals(column.getText()) || "Name".equals(column.getText()) || "Category".equals(column.getText())) {
                        sceneHandler.showPopupMessage(text);
                    }
                });
            }
        };
    }

    private <T> TableColumn<Transaction, T> creaColonna(String title, String property) {
        TableColumn<Transaction, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        if ("Note".equals(title) || "Name".equals(title) || "Category".equals(title)) {
            column.setCellFactory(tc -> customCellFactory());
        }
        return column;
    }

    private void setTableSize(List<Transaction> transactions) {
        table.setItems(FXCollections.observableArrayList(transactions));
        Platform.runLater(() -> {
            final double rowHeight = 60.0;
            int numberOfRows = Math.min(transactions.size(), 50);
            table.setPrefHeight((numberOfRows * rowHeight) + 8 + 36);
        });
    }

    private void setupTable() {
        table.getColumns().clear();
        String[] columnNames = {"Date", "Name", "Amount", "Note", "Type", "Category"};
        String[] columnProps = {"data", "nome", "importo", "note", "tipo", "categoria"};

        for (int i = 0; i < columnNames.length; i++) {
            TableColumn<Transaction, ?> column = creaColonna(columnNames[i], columnProps[i]);
            column.prefWidthProperty().bind(table.widthProperty().divide(columnNames.length));
            table.getColumns().add(column);
        }
        table.getStyleClass().add("tableTransaction");

        table.setOnMouseClicked(event -> {
            Transaction clickedTransaction = table.getSelectionModel().getSelectedItem();
            if (clickedTransaction != null) {
                int clickedTransactionId = clickedTransaction.getTransazione_ID();
                if (clickedTransactionId == currentTransaction) {
                    table.getSelectionModel().clearSelection();
                    currentTransaction = -1;
                } else {
                    currentTransaction = clickedTransactionId;
                }
            } else {
                currentTransaction = -1;
            }
        });
    }

    private void initializeTable(AnchorPane anchorPaneTransaction) {
        if (!anchorPaneTransaction.getChildren().contains(table)) {
            anchorPaneTransaction.getChildren().add(table);
            AnchorPane.setTopAnchor(table, 0.0);
            AnchorPane.setBottomAnchor(table, 0.0);
            AnchorPane.setLeftAnchor(table, 0.0);
            AnchorPane.setRightAnchor(table, 0.0);
        }
    }

    private void playFadeTransition() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), table);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    // ===================== Metodi principali =====================

    public void creaElencoTransazioni(AnchorPane anchorPaneTransaction, String name, String type, String category, String date) {
        List<Transaction> transazioni = databaseHandler.trovaTransazioniAvanzato(name, type, category, date);
        initializeTable(anchorPaneTransaction);
        setTableSize(transazioni);
        playFadeTransition();
    }

    public Transaction getSelectedTransaction() {
        return table.getSelectionModel().getSelectedItem();
    }

}