package informatica.unical.spadafora.casanostra.controller.categories;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AddCategoryController {

    // ===================== Costanti per messaggi e percorsi =====================

    private static final String PREDEFINED_CATEGORY = "No Category";
    private static final String INVALID_CATEGORY_NAME = "Maximum 30 alphanumeric characters and spaces";
    private static final String CATEGORY_ALREADY_PREDEFINED = "This already exists as a predefined category";
    private static final String CATEGORY_NAME_EMPTY_ERROR = "The category name field cannot be left empty";
    private static final String CATEGORY_ADDED = "Category added successfully.";
    private static final String CATEGORY_ALREADY_EXISTS = "Please make sure the category you are adding does not already exist in the list";
    private static final String GO_BACK_PATH = "controlPanel/categoryView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";


    // ===================== Componenti UI =====================

    @FXML
    private TextField categoryField;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        Platform.runLater(() -> categoryField.requestFocus());
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            aggiungiCategoriaSubmit();
        }
    }

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    @FXML
    void validateCategoryField() {
        // Validazione del campo categoria
        String content = categoryField.getText();
        if (!content.matches("[a-zA-Z0-9\\s]{0,30}")) {
            sceneHandler.alert("Warning", INVALID_CATEGORY_NAME, "warning");
            content = content.substring(0, content.length() - 1);
        }
        categoryField.setText(content);
        categoryField.positionCaret(content.length());
    }


    // ===================== Azioni Principali =====================

    @FXML
    void aggiungiCategoriaSubmit() {
        String category = categoryField.getText();
        if (PREDEFINED_CATEGORY.equals(category)) {
            sceneHandler.alert("Warning", CATEGORY_ALREADY_PREDEFINED, "warning");
            return;
        }
        if (category.isEmpty()) {
            sceneHandler.alert("Warning", CATEGORY_NAME_EMPTY_ERROR, "warning");
            return;
        }
        if (databaseHandler.aggiungiCategoria(category)) {
            sceneHandler.alert("Success", CATEGORY_ADDED, "success");
        }
        else {
            sceneHandler.alert("Error", CATEGORY_ALREADY_EXISTS, "error");
        }
        categoryField.setText("");
    }
}