package informatica.unical.spadafora.casanostra.controller;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SideController {

    // ===================== Costanti per messaggi =====================

    private static final String ASK_LOGOUT = "Are you sure you want to log out?";

    // ===================== Componenti UI =====================

    @FXML
    private VBox sideBar;
    @FXML
    private VBox controlPanelVbox;
    @FXML
    private VBox accountVbox;
    @FXML
    private VBox impostazioniVbox;
    @FXML
    private VBox creditsVbox;
    @FXML
    private AnchorPane workingArea;

    // ===================== Istanze Singleton =====================

    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Inizializzazione =====================

    @FXML
    public void initialize() {
        controlPanelVbox.getStyleClass().add("selectedVbox");

        if(sceneHandler.getFontSize().equals("Big")){
            sideBar.setMinWidth(180);
        }
        else{
            sideBar.setMinWidth(160);
        }

        AnchorPane.setTopAnchor(sideBar, 0.0);
        AnchorPane.setBottomAnchor(sideBar, 0.0);

        AnchorPane.setTopAnchor(workingArea, 0.0);
        AnchorPane.setBottomAnchor(workingArea, 0.0);

    }

    // ===================== Metodi Ausiliari =====================

    private void setVboxSelected(VBox vbox) {
        controlPanelVbox.getStyleClass().remove("selectedVbox");
        accountVbox.getStyleClass().remove("selectedVbox");
        impostazioniVbox.getStyleClass().remove("selectedVbox");
        creditsVbox.getStyleClass().remove("selectedVbox");

        vbox.getStyleClass().add("selectedVbox");
    }

    public AnchorPane getWorkingArea() {
    	return workingArea;
    }

    // ===================== Gestione Eventi =====================

    @FXML
    void controlPanelClicked() {
        setVboxSelected(controlPanelVbox);
        sceneHandler.loadWorkingArea("controlPanelView", "XX");

    }

    @FXML
    public void accountClicked() {
        setVboxSelected(accountVbox);
        sceneHandler.loadWorkingArea("account/accountView", "XX");

    }

    @FXML
    public void settingsClicked() {
        setVboxSelected(impostazioniVbox);
        sceneHandler.loadWorkingArea("settingsView", "XX");
    }

    @FXML
    void creditsClicked() {
        setVboxSelected(creditsVbox);
        sceneHandler.loadWorkingArea("creditsView", "XX");
    }

    @FXML
    private void logoutClicked() {
        if(sceneHandler.askPermission("Log out", ASK_LOGOUT)) {
            databaseHandler.logout();
            sceneHandler.setDefaultStyle();

            sceneHandler.showLoginScreen();
        }
    }

}

