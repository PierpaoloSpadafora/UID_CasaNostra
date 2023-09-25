package informatica.unical.spadafora.casanostra.controller.userAccessPoint;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class UsernameRecoveryController {

    private final String SECURITY_QUESTION_NOT_SET = "The username you entered does not exist or the security questions have not been set yet";

    @FXML
    private ImageView dyslexiaButton;

    @FXML
    private TextField usernameField;

    private boolean isDyslexia;

    private final SceneHandler sceneHandler= SceneHandler.getInstance();

    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    @FXML
    void toggleFontDislessia() {
        if(isDyslexia) {
            sceneHandler.setFontAndReloadUsernameRecovery("Roboto");
        } else {
            sceneHandler.setFontAndReloadUsernameRecovery("Dyslexia");
        }
    }

    @FXML
    public void initialize(){
        dyslexiaButton.setImage(sceneHandler.getDyslexiaImage("dyslexia_white"));
        isDyslexia = sceneHandler.getFont().startsWith("Dyslexia");
        usernameField.requestFocus();
    }

    @FXML
    void validaUsername(KeyEvent event) {
        String character = event.getCharacter();
        if (!character.matches("[a-zA-Z0-9-_]")  || usernameField.getText().length() >= 20) {

            String currentText = usernameField.getText();
            if (!currentText.isEmpty()) {
                usernameField.setText(currentText.substring(0, currentText.length() - 1));
                usernameField.positionCaret(currentText.length() - 1); // Posiziona il cursore alla fine
            }

            event.consume();
        }
    }

    @FXML
    void tastoPremuto() {
        confermaUsernameSubmit();
    }

    @FXML
    void goBack() {
        sceneHandler.showLoginScreen();
    }


    @FXML
    void confermaUsernameSubmit() {

        if(databaseHandler.areSecurityQuestionsSet(usernameField.getText())){
            sceneHandler.showAccessResetPasswordView();
            User user = new User(usernameField.getText());
            databaseHandler.setCurrentUser(user);
        }
        else{
            sceneHandler.alert("Error", SECURITY_QUESTION_NOT_SET, "error");
        }

    }

}
