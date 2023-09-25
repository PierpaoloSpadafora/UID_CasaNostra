package informatica.unical.spadafora.casanostra.controller.account;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class SecurityQuestionsController {

    // ===================== Costanti per messaggi di validazione =====================
    private static final String VALIDATION_ERROR = "The title field can only contain alphanumeric characters and spaces and can have a maximum of 20 characters";
    private static final String SELECT_ALL_QUESTIONS = "You must select all the security questions";
    private static final String ASK_TO_SAVE_QUESTIONS = "Are you sure you want to save your security questions?";
    private static final String ERROR_SAVING_QUESTIONS = "An error occurred while saving your security questions";
    private static final String SECURITY_QUESTIONS_SAVED = "Security questions saved successfully";

    // ===================== Componenti UI =====================

    @FXML
    private Label labelSecurityQuestions;

    @FXML
    private Label labelSecurityQuestionsDescription;

    @FXML
    private TextField answerField1;

    @FXML
    private TextField answerField2;

    @FXML
    private TextField answerField3;

    @FXML
    private ComboBox<String> questionField1;

    @FXML
    private ComboBox<String> questionField2;

    @FXML
    private ComboBox<String> questionField3;


    // ===================== Variabile di classe =====================

    private final List<String> securityQuestions = Arrays.asList(
            "The name of your first pet?",
            "Your childhood nickname?",
            "Your mother's maiden name?",
            "Your favorite teacher?",
            "Your favorite movie?",
            "The make and model of your first car?",
            "Your favorite color?",
            "Your father's middle name?",
            "Your first school?",
            "Your favorite sport?",
            "The name of your first roommate?",
            "Your favorite book?",
            "The name of your childhood best friend?",
            "The name of your favorite musician?",
            "Your first job?",
            "The name of your favorite aunt?",
            "Your favorite food?",
            "The name of your first crush?"
    );

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler= SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        questionField1.setItems(FXCollections.observableArrayList(securityQuestions));
        questionField2.setItems(FXCollections.observableArrayList(securityQuestions));
        questionField3.setItems(FXCollections.observableArrayList(securityQuestions));

        // Aggiungi i listener
        sceltaEsclusivaListener(questionField1, questionField2, questionField3);
        sceltaEsclusivaListener(questionField2, questionField1, questionField3);
        sceltaEsclusivaListener(questionField3, questionField1, questionField2);

        Platform.runLater(() -> {
            questionField1.requestFocus();

            if(databaseHandler.verificaSeDomandeDiSicurezzaPresenti()){

                labelSecurityQuestions.setText("Change your security questions");
                labelSecurityQuestionsDescription.setText("");

                List<String> domande = databaseHandler.trovaDomandeDiSicurezza();

                questionField1.getSelectionModel().select(domande.get(0));
                questionField2.getSelectionModel().select(domande.get(1));
                questionField3.getSelectionModel().select(domande.get(2));

            }
            else {
                labelSecurityQuestions.setText("Set your security questions");
                labelSecurityQuestionsDescription.setText("to enable your password recovery");
            }

        });
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto() {
        saveAnswersSubmit();
    }

    @FXML
    void goBack() {
        sceneHandler.showAccountScreen();
    }

    // ===================== Logica di validazione =====================

    private void validateAnswerField(TextField answerField) {
        String content = answerField.getText();

        if (!content.matches("[a-zA-Z0-9\\s]{0,20}")) {
            sceneHandler.alert("Warning", VALIDATION_ERROR, "warning" );
            content = content.substring(0, content.length() - 1);
            answerField.setText(content);
            answerField.positionCaret(content.length());
        }
    }

    @FXML
    void validateAnswerField1() {
        validateAnswerField(answerField1);
    }

    @FXML
    void validateAnswerField2() {
        validateAnswerField(answerField2);
    }

    @FXML
    void validateAnswerField3() {
        validateAnswerField(answerField3);
    }

    // ===================== Metodi ausiliari =====================

    private void sceltaEsclusivaListener(ComboBox<String> source, ComboBox<String>... others) {
        source.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for (ComboBox<String> other : others) {
                    other.getItems().remove(newValue);
                }
            }
            if (oldValue != null) {
                for (ComboBox<String> other : others) {
                    if (!other.getItems().contains(oldValue)) {
                        other.getItems().add(oldValue);
                    }
                }
            }
        });
    }

    // ===================== Azioni principali =====================

    @FXML
    void saveAnswersSubmit() {

        if(questionField1.getValue() == null || questionField2.getValue() == null || questionField3.getValue() == null){
            sceneHandler.alert("Warning", SELECT_ALL_QUESTIONS, "warning");
            return;
        }

        if(sceneHandler.askPermission("Confirmation", ASK_TO_SAVE_QUESTIONS)){

            String answer1 = answerField1.getText().toLowerCase();
            String answer2 = answerField2.getText().toLowerCase();
            String answer3 = answerField3.getText().toLowerCase();

            if(databaseHandler.verificaSeDomandeDiSicurezzaPresenti()){
                if(databaseHandler.modificaDomandeDiSicurezza(questionField1.getValue(), questionField2.getValue(), questionField3.getValue(), answer1, answer2, answer3)){
                    sceneHandler.alert("Success", SECURITY_QUESTIONS_SAVED, "success");
                     sceneHandler.showAccountScreen();
                }
                else {
                    sceneHandler.alert("Error", ERROR_SAVING_QUESTIONS, "error");
                }
            }
            else{
                if (databaseHandler.inserisciDomandeDiSicurezza(questionField1.getValue(), questionField2.getValue(), questionField3.getValue(), answer1, answer2, answer3)) {
                    sceneHandler.alert("Success", SECURITY_QUESTIONS_SAVED, "success");
                     sceneHandler.showAccountScreen();
                }
                else {
                    sceneHandler.alert("Error", ERROR_SAVING_QUESTIONS, "error");
                }
            }
        }
    }



}
