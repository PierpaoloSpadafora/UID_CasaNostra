package informatica.unical.spadafora.casanostra.controller.controlPanel;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.ElencoCategorie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CategoriesController {

    // ===================== Costanti per messaggi e percorsi =====================

    private static final String CATEGORY_NAME_ERROR = "Maximum 30 alphanumeric characters and spaces";
    private static final String CATEGORY_REMOVAL_ERROR = "Please make sure to delete all transactions associated with this category before attempting to remove the category itself.";
    private static final String CATEGORY_SELECTION_ERROR = "Please select a category to remove.";
    private static final String CATEGORY_REMOVAL_SUCCESS = "Category has been successfully removed.";
    private static final String CATEGORY_REMOVAL_CONFIRMATION = "Are you sure you want to remove the selected category?";
    private static final String GO_BACK_PATH = "controlPanelView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Componenti UI =====================

    @FXML
    private AnchorPane anchorPaneCategories;
    @FXML
    private TextField categoryField;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private final ElencoCategorie elencoCategorie = new ElencoCategorie();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        searchTransactionSubmit();
        Platform.runLater(() -> categoryField.requestFocus());
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    @FXML
    void addCategorySubmit() {
        sceneHandler.loadWorkingArea("categories/addCategoryView", "DESTRA");
    }

    // ===================== Azioni Principali =====================

    @FXML
    void clearFiltersSubmit() {
        categoryField.setText("");
        searchTransactionSubmit();
    }

    @FXML
    void searchTransactionSubmit() {
        elencoCategorie.creaElencoCategorie(anchorPaneCategories, categoryField.getText());
    }

    @FXML
    void validateCategoryField() {

        String content = categoryField.getText();

        if (!content.matches("[a-zA-Z0-9\\s]{0,30}")) {
            sceneHandler.alert("Warning", CATEGORY_NAME_ERROR, "warning");
            content = content.substring(0, content.length() - 1);
        }

        categoryField.setText(content);
        categoryField.positionCaret(content.length());
    }

    @FXML
    void rimuoviCategorieSubmit() {
        if(!(elencoCategorie.getSelectedCategory() == null)){
            if(sceneHandler.askPermissionDanger("Category removal", CATEGORY_REMOVAL_CONFIRMATION)){
                if(databaseHandler.rimuoviCategoria(elencoCategorie.getSelectedCategory().getId(), elencoCategorie.getSelectedCategory().getNome())){
                    sceneHandler.alert("Category removal", CATEGORY_REMOVAL_SUCCESS, "success");

                    anchorPaneCategories.getChildren().clear();
                    searchTransactionSubmit();
                }
                else{
                    sceneHandler.alert("Category removal", CATEGORY_REMOVAL_ERROR, "error");
                }
            }
        }
        else{
            sceneHandler.alert("Warning", CATEGORY_SELECTION_ERROR, "warning");
        }
    }



}
