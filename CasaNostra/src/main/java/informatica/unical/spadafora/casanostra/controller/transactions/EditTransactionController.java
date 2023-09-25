package informatica.unical.spadafora.casanostra.controller.transactions;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.Category;
import informatica.unical.spadafora.casanostra.model.Transaction;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.util.List;

public class EditTransactionController {

    // ===================== Costanti per messaggi e percorsi =====================


    private static final String TRANSACTION_VALIDATION = "The title field is restricted to alphanumeric characters and spaces, with a maximum limit of 30 characters";
    private static final String NOTE_LIMIT_REACHED = "Character or line limit reached \n (Max 300 characters - max 10 lines)";
    private static final String TRANSACTION_SUCCESSFUL = "The transaction was successfully entered";
    private static final String TRANSACTION_ERROR = "Error in entering transaction";
    private static final String NO_TRANSACTION_SELECTED_FOR_EDIT = "No transactions have been selected";
    private static final String TITLE_BLANK = "Title field cannot be blank";
    private static final String AMOUNT_BLANK = "Amount field cannot be blank";
    private static final String TYPE_BLANK = "Type field cannot be empty";
    private static final String CATEGORY_BLANK = "Category field cannot be empty";
    private static final String DATE_BLANK = "Date field cannot be blank";
    private static final String GO_BACK_PATH = "transactions/searchTransactionView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Componenti UI =====================
    @FXML
    private FontIcon notesIcon;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> categoryField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextArea notesField;
    @FXML
    private TextArea lineNumberField;
    @FXML
    private TextField transactionField;
    @FXML
    private ComboBox<String> typeField;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private Transaction transaction = null;

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        inizializzaCampiEControlli();
        Platform.runLater(() -> {
            ScrollPane sp = (ScrollPane) lineNumberField.lookup(".scroll-pane");
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        });
    }

    private void inizializzaCampiEControlli() {
        popolaCategoryField();
        popolaTypeField();
        limiteCaratteriNoteField();
        updateLineNumbers();
        Platform.runLater(() -> {
            settaCampiEditTransaction(transaction);

            lineNumberField.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    event.consume();
                }
            });

            transactionField.requestFocus();
            limiteCaratteriNoteField();
            notesField.textProperty().addListener((observable, oldValue, newValue) -> updateLineNumbers());
            notesField.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
                lineNumberField.setScrollTop(newValue.doubleValue());
                updateLineNumbers();
            });
        });
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            salvaModificheSubmit();
        }
    }

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    // ===================== Validazioni =====================

    @FXML
    void validateAmountField() {
        String content = amountField.getText();

        // Sostituisci eventuali virgole con un punto
        content = content.replace(',', '.');

        // Verifica che il contenuto inserito sia un numero valido con al massimo due cifre dopo il punto
        if (!content.matches("\\d*([.,]\\d{0,2})?")) {
            content = content.substring(0, content.length() - 1);
        }

        amountField.setText(content);
        amountField.positionCaret(content.length());
    }

    @FXML
    void validateTransactionField() {

        String content = transactionField.getText();

        if (!content.matches("[a-zA-Z0-9\\s]{0,30}")) {
            // Se non corrisponde, rimuovi l'ultimo carattere inserito
            sceneHandler.alert("Warning", TRANSACTION_VALIDATION, "warning");
            content = content.substring(0, content.length() - 1);
        }

        transactionField.setText(content);
        transactionField.positionCaret(content.length());
    }

    public boolean convalidaCampi(){
        if(transactionField.getText().isEmpty()){
            sceneHandler.alert("Warning", TITLE_BLANK, "warning");
            return false;
        }
        if(amountField.getText().isEmpty()){
            sceneHandler.alert("Warning", AMOUNT_BLANK, "warning");
            return false;
        }
        if(typeField.getValue().isEmpty()){
            sceneHandler.alert("Warning", TYPE_BLANK, "warning");
            return false;
        }
        if(categoryField.getValue().isEmpty()){
            sceneHandler.alert("Warning", CATEGORY_BLANK, "warning");
            return false;
        }
        if(dateField.getValue() == null){
            sceneHandler.alert("Warning", DATE_BLANK, "warning");
            return false;
        }

        return true;
    }

    // ===================== Metodi Ausiliari =====================

    void popolaCategoryField(){
        List<Category> categorie = databaseHandler.trovaCategorie("");

        categoryField.getItems().clear();
        categoryField.getItems().add("No Category");

        for (Category categoria : categorie) {
            categoryField.getItems().add(categoria.getNome());
        }

        categoryField.getSelectionModel().selectFirst();
    }

    void popolaTypeField() {
        typeField.getItems().add("EXPENSE");
        typeField.getItems().add("INCOME");

        typeField.getSelectionModel().selectFirst();
    }

    public void settaCampiEditTransaction(Transaction t) {
        if (t == null) {
            sceneHandler.alert("Missing transaction", NO_TRANSACTION_SELECTED_FOR_EDIT, "warning");
        }
        else {
            transaction = t;
            transactionField.setText(transaction.getNome());
            transactionField.end();

            amountField.setText(String.valueOf(transaction.getImporto()));


            notesField.setText(transaction.getNote());

            if(transaction.getNote().isEmpty()){
                notesIcon.setIconLiteral("mdi2n-note-plus-outline");
            }
            else {
                notesIcon.setIconLiteral("mdi2n-note-outline");
            }

            typeField.setValue(transaction.getTipo());
            dateField.setValue(LocalDate.parse(transaction.getData()));
        }
    }

    private String limitaLineeECaratteri(String text) {
        int lastNewLineIndex = -1;
        for (int i = 0, count = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                if (++count == 9 + 1) {
                    lastNewLineIndex = i;
                    break;
                }
            }
        }
        String truncatedText = (lastNewLineIndex != -1) ? text.substring(0, lastNewLineIndex) : text;
        if (truncatedText.length() > 300) {
            truncatedText = truncatedText.substring(0, 300);
        }
        return truncatedText;
    }

    private void limiteCaratteriNoteField() {
        notesField.textProperty().addListener((observable, oldValue, newValue) -> {
            int lineCount = newValue.length() - newValue.replace("\n", "").length();
            if (lineCount > 9 || newValue.length() > 300) {
                sceneHandler.alert("Warning", NOTE_LIMIT_REACHED, "warning");
                Platform.runLater(() -> {
                    String truncatedText = limitaLineeECaratteri(newValue);
                    notesField.setText(truncatedText);
                    notesField.positionCaret(truncatedText.length());
                });
            }
        });
    }

    private void updateLineNumbers() {
        Platform.runLater(() -> {
            String text = notesField.getText();
            String[] lines = text.split("\n", -1); // il parametro -1 serve per mantenere le righe vuote
            StringBuilder lineNumbers = new StringBuilder();
            for (int i = 1; i <= lines.length; i++) {
                lineNumbers.append(i).append(")\n");
            }
            lineNumberField.setText(lineNumbers.toString());
            lineNumberField.setScrollTop(notesField.getScrollTop());
        });
    }

    // ===================== Azioni Principali =====================

    @FXML
    void salvaModificheSubmit(){

        // se transaction è null allora fai partire uno scenehandler.alert

        if(convalidaCampi()){

            transaction.setNome(transactionField.getText());
            transaction.setImporto(Double.parseDouble(amountField.getText()));
            transaction.setNote(notesField.getText());
            transaction.setTipo(typeField.getValue());
            transaction.setData(dateField.getValue().toString());
            transaction.setCategoria(categoryField.getValue());
            transaction.setUtente_ID(databaseHandler.getCurrentUser().username());

            if(databaseHandler.modificaTransazione(transaction)){
                sceneHandler.alert("Transaction entered", TRANSACTION_SUCCESSFUL, "success");
                goBack();
            }
            else{
                sceneHandler.alert("Error", TRANSACTION_ERROR, "error");
            }
        }
    }
}
