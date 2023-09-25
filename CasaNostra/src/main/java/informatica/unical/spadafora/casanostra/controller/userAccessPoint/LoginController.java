package informatica.unical.spadafora.casanostra.controller.userAccessPoint;

import informatica.unical.spadafora.casanostra.model.Settings;
import informatica.unical.spadafora.casanostra.model.User;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.kordamp.ikonli.javafx.FontIcon;

public class LoginController {

    // ===================== Componenti UI =====================
    @FXML
    private FontIcon eyePasswordField;
    @FXML
    private ImageView dyslexiaButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordFieldHidden;
    @FXML
    private PasswordField passwordFieldVisible;

    // ===================== Istanze Singleton =====================
    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Variabili di Stato =====================
    private boolean isDyslexia;

    // ===================== Inizializzazione =====================

    private void initializeDyslexia() {
        dyslexiaButton.setImage(sceneHandler.getDyslexiaImage("dyslexia_white"));
        isDyslexia = sceneHandler.getFont().startsWith("Dyslexia");
    }

    @FXML
    public void initialize() {
        databaseHandler.setCurrentUser(null);
        initializeDyslexia();
        passwordFieldVisible.textProperty().bindBidirectional(passwordFieldHidden.textProperty());
        Platform.runLater(() -> usernameField.requestFocus());
    }

    // ===================== Gestione Eventi =====================
    @FXML
    void tastoPremuto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                loginSubmit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void toggleFontDislessia() {
        if(isDyslexia) {
            sceneHandler.setFontAndReloadLogin("Roboto");
        } else {
            sceneHandler.setFontAndReloadLogin("Dyslexia");
        }
    }

    @FXML
    public void togglePasswordVisibility() {

        // variabile booleana lalla che vede se una password è visibile o no senza usare checkbox
        boolean showPassword = passwordFieldHidden.isVisible();

        if(showPassword) {
            eyePasswordField.setIconLiteral("mdi2e-eye");
        } else {
            eyePasswordField.setIconLiteral("mdi2e-eye-off");
        }

        passwordFieldHidden.setManaged(!showPassword);
        passwordFieldHidden.setVisible(!showPassword);
        passwordFieldVisible.setManaged(showPassword);
        passwordFieldVisible.setVisible(showPassword);
    }

    // ===================== Validazioni =====================

    @FXML
    void validaUsername(KeyEvent event) {
        String character = event.getCharacter();
        if (!character.matches("[a-zA-Z0-9-_]")  || usernameField.getText().length() >= 20) {

            // Elimina l'ultimo carattere inserito
            String currentText = usernameField.getText();
            if (!currentText.isEmpty()) {
                usernameField.setText(currentText.substring(0, currentText.length() - 1));
                usernameField.positionCaret(currentText.length() - 1); // Posiziona il cursore alla fine
            }

            event.consume(); // Ignora il carattere non valido
        }
    }

    // ===================== Azioni Principali =====================

    @FXML
    void loginSubmit() {

        String Username = usernameField.getText();
        String Password = passwordFieldVisible.getText();

        if(Username.isEmpty()){
            sceneHandler.alert("Warning", "Please enter your username \n to log in.", "warning");
            return;
        }
        if(Password.isEmpty()){
            sceneHandler.alert("Warning", "Please enter your password \n to log in.", "warning");
            return;
        }

        if(databaseHandler.utentePresenteInDB(Username)){
            if(databaseHandler.checkPassword(Username, Password)){
                User user = new User(Username);
                databaseHandler.setCurrentUser(user);
                Settings settings = databaseHandler.trovaSettings();
                sceneHandler.setStyle(settings.getTheme(), settings.getFont(), settings.getFontSize());
                sceneHandler.showMainScreen(sceneHandler.getWindowWidth(), sceneHandler.getWindowHeight());
                return;
            }
        }
        sceneHandler.alert("Error", "Username or password is incorrect.", "error");
    }

    @FXML
    void registrationSubmit() {
        sceneHandler.showRegistrationScreen();
    }

    @FXML
    void passwordDimenticata() {
        sceneHandler.showUsernameRecoveryView();
    }

}
