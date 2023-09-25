package informatica.unical.spadafora.casanostra.controller.userAccessPoint;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.Settings;
import informatica.unical.spadafora.casanostra.model.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.kordamp.ikonli.javafx.FontIcon;

public class RegistrationController {

    // ===================== Costanti per messaggi =====================

    private static final String REQUIREMENTS_REQUIREMENTS = "Password requirements:\n";
    private static final String AT_LEAST_8_CHARACTERS = "At least 8 characters";
    private static final String AT_LEAST_ONE_UPPERCASE = "At least one uppercase letter";
    private static final String AT_LEAST_ONE_LOWERCASE = "At least one lowercase letter";
    private static final String AT_LEAST_ONE_NUMBER = "At least one number";
    private static final String AT_LEAST_ONE_SPECIAL_CHAR = "At least one of these special character";
    private static final String SPECIAL_CHARACTERS = "\t @ $ ! % * ? & # ^ + - \n";
    private static final String PASSWORD_MUST_MATCH = "Passwords must match";
    private static final String USERNAME_REQUIREMENTS = "Username Requirements:\n";
    private static final String USERNAME_LENGTH = "- Length between 4 and 20 characters\n";
    private static final String USERNAME_CONSTRAINT = "- Can contain only letters, numbers, hyphens and underscores\n";

    // ===================== Componenti UI =====================

    @FXML
    private FontIcon eyePasswordField1;

    @FXML
    private FontIcon eyePasswordField2;

    @FXML
    private ImageView dyslexiaButton;

    @FXML
    private Label labelPassword;

    @FXML
    private Label labelUsername;

    @FXML
    private PasswordField passwordFieldVisible;

    @FXML
    private TextField passwordFieldHidden;

    @FXML
    private PasswordField passwordFieldVisibleConfirmation;

    @FXML
    private TextField passwordFieldHiddenConfirmation;

    @FXML
    private TextField usernameField;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Variabili d'istanza =====================

    private boolean isDyslexia;

    // ===================== Inizializzazione =====================

    private void initializeDyslexia() {
        dyslexiaButton.setImage(sceneHandler.getDyslexiaImage("dyslexia_white"));
        isDyslexia = sceneHandler.getFont().startsWith("Dyslexia");
    }

    @FXML
    public void initialize() {
        initializeDyslexia();

        passwordFieldVisible.textProperty().bindBidirectional(passwordFieldHidden.textProperty());
        passwordFieldHiddenConfirmation.textProperty().bindBidirectional(passwordFieldVisibleConfirmation.textProperty());

        attivaConfermaPassword();
        validaUsername();
        Platform.runLater(() -> usernameField.requestFocus());
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void tastoPremuto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            registrationSubmit();
        }
    }

    @FXML
    void goBack(){
        sceneHandler.showLoginScreen();
    }

    @FXML
    void toggleFontDislessia() {
        if(isDyslexia) {
            sceneHandler.setFontAndReloadRegistration("Roboto");
        } else {
            sceneHandler.setFontAndReloadRegistration("Dyslexia");
        }
    }

    @FXML
    void validaUsername() {
        String username = usernameField.getText();
        StringBuilder errorMsg = new StringBuilder();

        if (Validator.lunghezzaNonValida(username) || Validator.caratteriNonAmmessi(username)) {
            errorMsg.append(USERNAME_REQUIREMENTS);

            if (Validator.lunghezzaNonValida(username)) {
                errorMsg.append(USERNAME_LENGTH);
            }

            if (Validator.caratteriNonAmmessi(username)) {
                errorMsg.append(USERNAME_CONSTRAINT);
            }

            labelUsername.setText(errorMsg.toString());
        } else {
            labelUsername.setText("");
        }
    }

    @FXML
    void attivaConfermaPassword() {
        String password = passwordFieldVisible.getText();
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        // Valida ciascun requisito e costruisci un messaggio di errore se necessario
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

    private void togglePasswordVisibility(TextField hiddenField, PasswordField visibleField, FontIcon eyeIcon) {
        boolean showPassword = hiddenField.isVisible();
        if (showPassword) {
            eyeIcon.setIconLiteral("mdi2e-eye");
        } else {
            eyeIcon.setIconLiteral("mdi2e-eye-off");
        }
        hiddenField.setManaged(!showPassword);
        hiddenField.setVisible(!showPassword);
        visibleField.setManaged(showPassword);
        visibleField.setVisible(showPassword);
    }

    public void togglePasswordVisibility1() {
        togglePasswordVisibility(passwordFieldHidden, passwordFieldVisible, eyePasswordField1);
    }

    public void togglePasswordVisibility2() {
        togglePasswordVisibility(passwordFieldHiddenConfirmation, passwordFieldVisibleConfirmation, eyePasswordField2);
    }

    // ===================== Validazioni =====================

    private boolean validateUsername() {
        String username = usernameField.getText();

        return Validator.validateUsername(username);
    }

    private boolean validatePassword() {
        String password = passwordFieldVisible.getText();
        String repeatPassword = passwordFieldVisibleConfirmation.getText();

        return password.equals(repeatPassword) && Validator.validatePassword(password);
    }

    // ===================== Metodi Ausiliari =====================

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


    // ===================== Azioni Principali =====================

    @FXML
    void registrationSubmit() {

        Settings settings = sceneHandler.getSettings();

        if (validateUsername() && validatePassword()) {
            if (databaseHandler.utentePresenteInDB(usernameField.getText())) {
                labelUsername.setText("The username is already taken");
            } else {
                if (databaseHandler.inserisciUser(usernameField.getText(), passwordFieldVisible.getText())) {
                    if (databaseHandler.aggiungiSettings(settings)) {

                        usernameField.setText("");
                        passwordFieldVisible.setText("");
                        passwordFieldVisibleConfirmation.setText("");

                        sceneHandler.alert("Registration completed", "Registration has been successfully completed!", "success");
                        sceneHandler.showLoginScreen();
                    }
                }
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



