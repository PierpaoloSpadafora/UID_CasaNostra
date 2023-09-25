package informatica.unical.spadafora.casanostra.controller.account;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.fxml.FXML;

import java.util.Objects;

public class JsonDataController {

    // ===================== Costanti per messaggi di validazione =====================

    private static final String IMPORT_SUCCESS = "The importing has been completed successfully";
    private static final String IMPORT_FAIL = "Something went wrong while importing";
    private static final String EXPORT_SUCCESS = "The exporting has been completed successfully";
    private static final String EXPORT_FAIL = "Something went wrong while exporting";
    private static final String GO_BACK_PATH = "account/accountView";
    private static final String GO_BACK_ANIMATION_DIRECTION = "SINISTRA";

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler= SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Gestione Eventi =====================

    @FXML
    void goBack() {
        sceneHandler.loadWorkingArea(GO_BACK_PATH, GO_BACK_ANIMATION_DIRECTION);
    }

    // ===================== Azioni principali =====================

    @FXML
    void importJsonSubmit() {
        String path = sceneHandler.getFileChooserPath();

        if(!databaseHandler.importaUtenteDaJson(path)) {
            sceneHandler.alert("Import failed", IMPORT_FAIL , "error");
        }
        else{
            sceneHandler.alert("Import successful", IMPORT_SUCCESS, "success");
        }
    }

    @FXML
    void exportJsonSubmit() {
        String path = sceneHandler.getDirectoryChooserPath();

        if(Objects.equals(path, "?errore?")){
            sceneHandler.alert("Export failed", EXPORT_FAIL,  "error");
        }
        else{
            if(!databaseHandler.esportaUtenteInJson(path)){
                sceneHandler.alert("Export failed", EXPORT_FAIL,  "error");
            }
            else{
                sceneHandler.alert("Export successful", EXPORT_SUCCESS, "success");
            }
        }

    }



}
