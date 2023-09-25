package informatica.unical.spadafora.casanostra.controller.transactions;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.Category;
import informatica.unical.spadafora.casanostra.model.ElencoTransazioni;
import informatica.unical.spadafora.casanostra.model.Transaction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class SearchTransactionController {


    // ===================== Costanti per messaggi e percorsi =====================

    private static final String TRANSACTION_VALIDATION = "The title field can only contain alphanumeric characters and spaces and can have a maximum of 30 characters";
    private static final String ASK_TRANSACTION_REMOVAL = "Are you sure you want to remove the selected transaction?";
    private static final String TRANSACTION_REMOVAL_ERROR = "Error in removing transaction";
    private static final String TRANSACTION_REMOVED = "The transaction has been successfully removed.";
    private static final String SELECT_TRANSACTION_TO_EDIT = "Please select a transaction to edit";
    private static final String SELECT_TRANSACTION_TO_REMOVE = "Please select a transaction to remove";
    private static final String GO_BACK_PATH = "controlPanelView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Componenti UI =====================

    @FXML
    private AnchorPane anchorPaneTransaction;
    @FXML
    private ComboBox<String> categoryField;
    @FXML
    private ComboBox<String> typeField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField transactionField;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private final ElencoTransazioni elencoTransazioni = new ElencoTransazioni();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize(){
        popolaCategoryField();
        popolaTypeField();

        Platform.runLater(() -> {
            transactionField.requestFocus();
            searchTransactionSubmit();
        });
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchTransactionSubmit();
        }
    }

    @FXML
    void goBack( ) {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    // ===================== Validazioni =====================


    @FXML
    void validateTransactionField() {
        String content = transactionField.getText();

        if (!content.matches("[a-zA-Z0-9\\s]{0,30}")) {
            sceneHandler.alert("Warning", TRANSACTION_VALIDATION, "warning");
            content = content.substring(0, content.length() - 1);
        }

        transactionField.setText(content);
        transactionField.positionCaret(content.length());
    }

    // ===================== Metodi Ausiliari =====================

    void popolaCategoryField(){
        List<Category> categorie = databaseHandler.trovaCategorie("");

        categoryField.getItems().clear();
        categoryField.getItems().add("All Categories");

        for (Category categoria : categorie) {
            categoryField.getItems().add(categoria.getNome());
        }

        categoryField.getSelectionModel().selectFirst();
    }

    void popolaTypeField() {
        typeField.getItems().add("Both");
        typeField.getItems().add("Expense");
        typeField.getItems().add("Income");

        typeField.getSelectionModel().selectFirst();
    }

    // ===================== Azioni Principali =====================

    @FXML
    void clearFiltersSubmit() {
        transactionField.setText("");
        categoryField.getSelectionModel().selectFirst();
        typeField.getSelectionModel().selectFirst();
        dateField.setValue(null);
        searchTransactionSubmit();
    }

    @FXML
    void searchTransactionSubmit() {
        String category = "No Category".equals(categoryField.getValue()) ? null : categoryField.getValue();
        String type = typeField.getValue();
        String date = (dateField.getValue() != null) ? dateField.getValue().toString() : null;
        String transaction = transactionField.getText().isEmpty() ? null : transactionField.getText();

        elencoTransazioni.creaElencoTransazioni(anchorPaneTransaction, transaction, type, category, date);
    }

    @FXML
    void selezionataPerModificaSubmit() {
        if (!(elencoTransazioni.getSelectedTransaction() == null)) {
            Transaction transaction = elencoTransazioni.getSelectedTransaction();

            EditTransactionController editTransactionController = (EditTransactionController) sceneHandler.loadWorkingArea("transactions/editTransactionView", "DESTRA");
            editTransactionController.settaCampiEditTransaction(transaction);
        }
        else{
            sceneHandler.alert("Warning", SELECT_TRANSACTION_TO_EDIT, "warning");
        }
    }


    @FXML
    void rimuoviTransazioneSubmit() {
        if (!(elencoTransazioni.getSelectedTransaction() == null)) {
            if(sceneHandler.askPermissionDanger("Transaction removal", ASK_TRANSACTION_REMOVAL)){
                if(databaseHandler.rimuoviTransazione(elencoTransazioni.getSelectedTransaction().getTransazione_ID())){
                    sceneHandler.alert("Transaction removed", TRANSACTION_REMOVED, "success");
                    anchorPaneTransaction.getChildren().clear();
                    searchTransactionSubmit();
                }
                else{
                    sceneHandler.alert("Error", TRANSACTION_REMOVAL_ERROR, "error");
                }
            }
        }
        else{
            sceneHandler.alert("Warning", SELECT_TRANSACTION_TO_REMOVE, "warning");
        }
    }


}
