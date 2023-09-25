package informatica.unical.spadafora.casanostra.model;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.List;

public class ElencoCategorie {

    // ===================== Istanze Singleton =====================

    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Variabili =====================

    TableView<Category> table = new TableView<>();

    int currentCategory = -1;

    // ===================== Costruttore =====================

    public ElencoCategorie() {
        setupTable();
    }

    // ===================== Metodi ausiliari =====================

    private TableColumn<Category, String> creaColonna() {
        TableColumn<Category, String> column = new TableColumn<>("Name");
        column.setCellValueFactory(new PropertyValueFactory<>("nome"));
        return column;
    }

    private void playFadeTransition() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), table);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void setTableSize(List<Category> categorie) {

        table.setItems(FXCollections.observableArrayList(categorie));

        Platform.runLater(() -> {
            double rowHeight = 60.0;
            int numberOfRowsToShow = Math.min(categorie.size(), 50);
            table.setPrefHeight((numberOfRowsToShow * rowHeight) + 8 + 36); // 8 = padding, 36 = altezza header
            table.setFixedCellSize(rowHeight);
        });
    }

    private void setupTable() {
        table.getColumns().clear();

        TableColumn<Category, String> column = creaColonna();

        column.setReorderable(false);
        column.setResizable(false);
        column.prefWidthProperty().bind(table.widthProperty().subtract(28)); // 20 = scrollbar, 8 = padding
        table.getColumns().add(column);
        table.getStyleClass().add("tableTransaction");

        table.setOnMouseClicked(event -> {
            Category clickedCategory = table.getSelectionModel().getSelectedItem();
            if (clickedCategory != null) {
                int clickedCategoryId = clickedCategory.getId();

                if (clickedCategoryId == currentCategory) {
                    table.getSelectionModel().clearSelection();
                    currentCategory = -1;
                }
                else {
                    currentCategory = clickedCategoryId;
                }
            }
        });

    }

    // ===================== Metodi principali =====================

    public void creaElencoCategorie(AnchorPane anchorPaneTransaction, String nomeCategoria) {
        List<Category> categorie = databaseHandler.trovaCategorie(nomeCategoria);

        if (!anchorPaneTransaction.getChildren().contains(table)) {

            anchorPaneTransaction.getChildren().add(table);

            AnchorPane.setTopAnchor(table, 0.0);
            AnchorPane.setBottomAnchor(table, 0.0);
            AnchorPane.setLeftAnchor(table, 0.0);
            AnchorPane.setRightAnchor(table, 0.0);
        }
        table.setOpacity(0.0);
        setTableSize(categorie);
        playFadeTransition();
    }

    public Category getSelectedCategory() {
        return table.getSelectionModel().getSelectedItem();
    }

}
