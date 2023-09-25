package informatica.unical.spadafora.casanostra.controller.account;

import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.fxml.FXML;

public class AccountController {

    // ===================== Costanti per i cambi di scena =====================
    private static final String CHANGE_PASSWORD_VIEW = "account/changePasswordView";
    private static final String DELETE_ACCOUNT_VIEW = "account/deleteAccountView";
    private static final String JSON_DATA_VIEW = "account/jsonDataView";
    private static final String SECURITY_QUESTIONS_VIEW = "account/securityQuestionsView";

    // ===================== Istanze Singleton =====================
    private final SceneHandler sceneHandler = SceneHandler.getInstance();

    // ===================== Gestione Eventi =====================
    @FXML
    void changePassword() {
        loadWorkingArea(CHANGE_PASSWORD_VIEW);
    }

    @FXML
    void eliminaAccount() {
        loadWorkingArea(DELETE_ACCOUNT_VIEW);
    }

    @FXML
    void importaEsportaJson() {
        loadWorkingArea(JSON_DATA_VIEW);
    }

    @FXML
    void domandeSicurezza() {
        loadWorkingArea(SECURITY_QUESTIONS_VIEW);
    }

    // ===================== Metodi =====================
    private void loadWorkingArea(String viewName) {
        sceneHandler.loadWorkingArea(viewName, "DESTRA");
    }
}