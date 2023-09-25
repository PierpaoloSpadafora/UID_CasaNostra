package informatica.unical.spadafora.casanostra.controller.userAccessPoint;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.util.List;

public class AccessResetPasswordController {

    // ===================== Costanti per messaggi =====================
    private static final String TITLE_VALIDATION = "The title field can only contain alphanumeric characters and spaces and can have a maximum of 20 characters";
    private static final String NO_SECURITY_QUESTIONS = "There are no security questions associated to this username";
    private static final String INCORRECT_ANSWERS = "The answers you entered are incorrect";

    // ===================== Componenti UI =====================
    @FXML
    private TextField answerField1;
    @FXML
    private TextField answerField2;
    @FXML
    private TextField answerField3;
    @FXML
    private ImageView dyslexiaButton;
    @FXML
    private Label question1;
    @FXML
    private Label question2;
    @FXML
    private Label question3;

    // ===================== Istanze Singleton =====================
    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    private boolean isDyslexia;

    // ===================== Inizializzazione =====================

    private void populateSecurityQuestions() {
        List<String> domande = databaseHandler.trovaDomandeDiSicurezza();
        question1.setText(domande.get(0));
        question2.setText(domande.get(1));
        question3.setText(domande.get(2));
    }

    private void initializeDyslexia() {
        dyslexiaButton.setImage(sceneHandler.getDyslexiaImage("dyslexia_white"));
        isDyslexia = sceneHandler.getFont().startsWith("Dyslexia");
    }

    private void loadSecurityQuestions() {
        Platform.runLater(() -> {
            if (databaseHandler.verificaSeDomandeDiSicurezzaPresenti()) {
                populateSecurityQuestions();
                answerField1.requestFocus();
            } else {
                sceneHandler.alert("Error", NO_SECURITY_QUESTIONS, "error");
                sceneHandler.showUsernameRecoveryView();
            }
        });
    }

    @FXML
    public void initialize() {
        initializeDyslexia();
        loadSecurityQuestions();
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto() {
        saveAnswersSubmit();
    }

    @FXML
    void goBack() {
        sceneHandler.showUsernameRecoveryView();
    }

    @FXML
    void toggleFontDislessia() {
        if(isDyslexia) {
            sceneHandler.setFontAndReloadAccessResetPassword("Roboto");
        } else {
            sceneHandler.setFontAndReloadAccessResetPassword("Dyslexia");
        }
    }

    // ===================== Validazioni =====================

    private void validateAnswerField(TextField answerField) {
        String content = answerField.getText();
        if (!content.matches("[a-zA-Z0-9\\s]{0,20}")) {
            sceneHandler.alert("Warning", TITLE_VALIDATION, "warning");
            content = content.substring(0, content.length() - 1);
        }
        answerField.setText(content);
        answerField.positionCaret(content.length());
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

    // ===================== Azioni Principali =====================

    @FXML
    void saveAnswersSubmit() {
        String answer1 = answerField1.getText().toLowerCase();
        String answer2 = answerField2.getText().toLowerCase();
        String answer3 = answerField3.getText().toLowerCase();


        if(databaseHandler.checkSecurityAnswers(answer1, answer2, answer3)){
            sceneHandler.showResetPasswordView();
        }
        else{
            sceneHandler.alert("Error", INCORRECT_ANSWERS, "error");
        }

    }



}
