package informatica.unical.spadafora.casanostra.controller.account;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.kordamp.ikonli.javafx.FontIcon;

public class ChangePasswordController {

    // ===================== Costanti per messaggi di validazione =====================

    private static final String REQUIREMENTS_REQUIREMENTS = "Password requirements:\n";
    private static final String AT_LEAST_8_CHARACTERS = "At least 8 characters";
    private static final String AT_LEAST_ONE_UPPERCASE = "At least one uppercase letter";
    private static final String AT_LEAST_ONE_LOWERCASE = "At least one lowercase letter";
    private static final String AT_LEAST_ONE_NUMBER = "At least one number";
    private static final String AT_LEAST_ONE_SPECIAL_CHAR = "At least one special character";
    private static final String SPECIAL_CHARACTERS = "\t @ $ ! % * ? & # ^ + - \n";
    private static final String PASSWORD_MUST_MATCH = "Passwords must match";
    private static final String PASSWORD_EDITING_ERROR = "An error occurred while changing the password";
    private static final String PASSWORD_CHANGED = "Password changed successfully";
    private static final String GO_BACK_PATH = "account/accountView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Componenti UI =====================

    @FXML
    private Label labelPassword;

    @FXML
    private FontIcon eyePasswordField1;

    @FXML
    private FontIcon eyePasswordField2;

    @FXML
    private TextField passwordFieldHidden;

    @FXML
    private TextField passwordFieldHiddenConfirmation;

    @FXML
    private PasswordField passwordFieldVisible;

    @FXML
    private PasswordField passwordFieldVisibleConfirmation;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        // Sincronizza i due field per
        passwordFieldVisible.textProperty().bindBidirectional(passwordFieldHidden.textProperty());
        passwordFieldHiddenConfirmation.textProperty().bindBidirectional(passwordFieldVisibleConfirmation.textProperty());

        // Far comparire i requisiti delle password all'avvio
        attivaConfermaPassword();

        Platform.runLater(() -> {
            passwordFieldVisible.requestFocus();
        });
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            saveNewPasswordSubmit();
        }
    }

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    @FXML
    void attivaConfermaPassword() {
        String password = passwordFieldVisible.getText();
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true; // Flag per tracciare se la password è valida

        // Valida ciascun requisito ed eventualmente costruisce il messaggio di errore
        if (password.length() < 8) {
            appendErrorMessage(errorMsg, AT_LEAST_8_CHARACTERS);
            isValid = false;
        }
        if (Validator.mancanoMaiuscole(password)) {
            appendErrorMessage(errorMsg, AT_LEAST_ONE_UPPERCASE);
            isValid = false;
        }
        if (Validator.mancanoMinuscole(password)) {
            appendErrorMessage(errorMsg, AT_LEAST_ONE_LOWERCASE);
            isValid = false;
        }
        if (Validator.mancanoNumeri(password)) {
            appendErrorMessage(errorMsg, AT_LEAST_ONE_NUMBER);
            isValid = false;
        }
        if (Validator.mancanoCaratteriSpeciali(password)) {
            appendErrorMessage(errorMsg, AT_LEAST_ONE_SPECIAL_CHAR);
            errorMsg.append(SPECIAL_CHARACTERS);
            isValid = false;
        }

        updateUIBasedOnValidation(isValid, errorMsg);
    }

    // ===================== Logica di validazione =====================

    private boolean validatePassword() {
        String password = passwordFieldVisible.getText();
        String repeatPassword = passwordFieldVisibleConfirmation.getText();

        return password.equals(repeatPassword) && Validator.validatePassword(password);
    }

    // ===================== Metodi ausiliari =====================

    private void appendErrorMessage(StringBuilder errorMsg, String message) {
        if (errorMsg.isEmpty()) {
            errorMsg.append(REQUIREMENTS_REQUIREMENTS);
        }
        errorMsg.append("- ").append(message).append("\n");
    }

    private void updateUIBasedOnValidation(boolean isValid, StringBuilder errorMsg) {
        if (isValid) {
            // Abilita i campi di conferma password se la password è valida
            passwordFieldHiddenConfirmation.setDisable(false);
            passwordFieldVisibleConfirmation.setDisable(false);
            labelPassword.setText("");
        } else {
            // Disabilita i campi di conferma password e mostra il messaggio di errore
            passwordFieldHiddenConfirmation.setDisable(true);
            passwordFieldVisibleConfirmation.setDisable(true);
            labelPassword.setText(errorMsg.toString());
        }
    }

    public void togglePasswordVisibility1() {
        togglePasswordVisibility(passwordFieldHidden, passwordFieldVisible, eyePasswordField1);
    }

    public void togglePasswordVisibility2() {
        togglePasswordVisibility(passwordFieldHiddenConfirmation, passwordFieldVisibleConfirmation, eyePasswordField2);
    }

    private void togglePasswordVisibility(TextField hiddenField, PasswordField visibleField, FontIcon eyeIcon) {
        boolean showPassword = hiddenField.isVisible();

        if (showPassword) {
            eyeIcon.setIconLiteral("mdi2e-eye");
        } else {
            eyeIcon.setIconLiteral("mdi2e-eye-off");
        }

        // toggle dei campi
        hiddenField.setManaged(!showPassword);
        hiddenField.setVisible(!showPassword);
        visibleField.setManaged(showPassword);
        visibleField.setVisible(showPassword);
    }

    // ===================== Azioni principali =====================

    @FXML
    void saveNewPasswordSubmit() {
        if (validatePassword()) {
            if (databaseHandler.modificaPassword(passwordFieldVisible.getText())) {
                sceneHandler.alert("Success", PASSWORD_CHANGED, "success");
                sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
            } else {
                sceneHandler.alert("Error", PASSWORD_EDITING_ERROR, "error");
            }
        }
        if(!validatePassword()){
            String password = passwordFieldVisible.getText();
            if(Validator.validatePassword(password)){
                labelPassword.setText(PASSWORD_MUST_MATCH);
            }
            else{
                attivaConfermaPassword();
            }
        }
    }
}