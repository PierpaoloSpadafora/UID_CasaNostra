package informatica.unical.spadafora.casanostra.controller.account;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.fxml.FXML;

public class DeleteAccountController {

    // ===================== Costanti per messaggi e percorsi =====================
    private static final String DELETE_CONFIRM_MESSAGE = "Are you sure you want to permanently delete your account?";
    private static final String GO_BACK_PATH = "account/accountView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Gestione Eventi =====================

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    // ===================== Azioni principali =====================

    @FXML
    void deleteAccountSubmit() {
        if (sceneHandler.askPermissionDanger("Account deletion", DELETE_CONFIRM_MESSAGE)) {
            databaseHandler.eliminaAccount();
            databaseHandler.logout();
            sceneHandler.setDefaultStyle();
            sceneHandler.showLoginScreen();
        }
    }
}