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

import java.util.List;

public class AddTransactionController {

    // ===================== Costanti per messaggi e percorsi =====================

    private static final String TRANSACTION_VALIDATION = "The title field can only contain alphanumeric characters and spaces and can have a maximum of 30 characters";
    private static final String NOTE_LIMIT_REACHED = "Character or line limit reached \n (Max 300 characters - max 10 lines)";
    private static final String TRANSACTION_SUCCESSFUL = "The transaction was successfully entered";
    private static final String TRANSACTION_ERROR = "Error in entering transaction";
    private static final String TITLE_BLANK = "Title field cannot be blank";
    private static final String AMOUNT_BLANK = "Amount field cannot be blank";
    private static final String TYPE_BLANK = "Type field cannot be empty";
    private static final String CATEGORY_BLANK = "Category field cannot be empty";
    private static final String DATE_BLANK = "Date field cannot be blank";
    private static final String GO_BACK_PATH = "controlPanelView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Componenti UI =====================

    @FXML
    private TextField transactionField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> typeField;
    @FXML
    private ComboBox<String> categoryField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextArea notesField;
    @FXML
    private TextArea lineNumberField;

    // ===================== Istanze Singleton =====================
    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize(){
        popolaCategoryField();
        popolaTypeField();
        dateField.setValue(java.time.LocalDate.now());
        inizializzaNoteField();

        Platform.runLater(() -> {
            ScrollPane sp = (ScrollPane) lineNumberField.lookup(".scroll-pane");
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        });

    }

    private void inizializzaNoteField() {
        Platform.runLater(() -> {

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
    void tastoPremuto(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            salvaTransazioneSubmit();
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
            sceneHandler.alert("Warning", TRANSACTION_VALIDATION, "warning");
            content = content.substring(0, content.length() - 1);
        }

        transactionField.setText(content);
        transactionField.positionCaret(content.length());
    }

    // ===================== Metodi Ausiliari =====================

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
    void salvaTransazioneSubmit(){

        if(convalidaCampi()){
            Transaction t;
            t = new Transaction(
                    0,
                    transactionField.getText(),
                    Double.parseDouble(amountField.getText()),
                    notesField.getText(),
                    typeField.getValue(),
                    dateField.getValue().toString(),
                    categoryField.getValue(),
                    databaseHandler.getCurrentUser().username()
            );

            if(databaseHandler.inserisciTransazione(t)){
                sceneHandler.alert("Transaction entered", TRANSACTION_SUCCESSFUL, "success");
            }
            else{
                sceneHandler.alert("Error", TRANSACTION_ERROR, "error");
            }

            transactionField.setText("");
            amountField.setText("");
            notesField.setText("");
            typeField.getSelectionModel().selectFirst();
            categoryField.getSelectionModel().selectFirst();
            dateField.setValue(java.time.LocalDate.now());
            lineNumberField.setText("");
        }

    }

}
