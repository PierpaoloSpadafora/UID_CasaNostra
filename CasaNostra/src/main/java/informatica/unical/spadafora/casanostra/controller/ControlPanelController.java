package informatica.unical.spadafora.casanostra.controller;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControlPanelController {

    // ===================== Costanti per messaggi e percorsi =====================

    private static final String SECURITY_QUESTION_NOT_SET = "You have not set your security questions yet. Do you want to set them now?";

    // ===================== Componenti UI =====================

    @FXML
    private Label labelBenvenuto;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        labelBenvenuto.setText("Welcome  " + databaseHandler.getCurrentUser().username());

        Platform.runLater(() ->{
            if(!databaseHandler.verificaSeDomandeDiSicurezzaPresenti()){
                if(sceneHandler.askPermission("Security questions", SECURITY_QUESTION_NOT_SET)){
                    sceneHandler.loadWorkingArea("account/securityQuestionsView", "NO");
                }
            }
        });
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void aggiungiTransazioni() {
        sceneHandler.loadWorkingArea("transactions/addTransactionView", "DESTRA");
    }

    @FXML
    void cercaTransazioni() {
        sceneHandler.loadWorkingArea("transactions/searchTransactionView", "DESTRA");

    }

    @FXML
    void gestisciCategorie() {
        sceneHandler.loadWorkingArea("controlPanel/categoryView",  "DESTRA");
    }

    @FXML
    void generaReport() {
        sceneHandler.loadWorkingArea("controlPanel/reportView",  "DESTRA");
    }

}
