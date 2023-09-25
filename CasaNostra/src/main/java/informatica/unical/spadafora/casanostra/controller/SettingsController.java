package informatica.unical.spadafora.casanostra.controller;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import informatica.unical.spadafora.casanostra.model.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SettingsController {

    // ===================== Costanti per messaggi e percorsi =====================

    private static final String ASK_CHANGES = "Are you sure you want to apply changes?";
    private static final String SETTINGS_ERROR = "An error occurred while applying settings";
    private static final String SETTINGS_SUCCESS = "Settings updated successfully";

    // ===================== Istanze Singleton =====================
    private final SceneHandler sceneHandler = SceneHandler.getInstance();
    private final DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

    // ===================== Componenti UI =====================
    @FXML
    private ComboBox<String> themeField;
    @FXML
    private ComboBox<String> fontField;
    @FXML
    private ComboBox<String> textSizeField;
    @FXML
    private RadioButton deuteranopiaRadioButton;
    @FXML
    private RadioButton protanopiaRadioButton;
    @FXML
    private RadioButton trianopiaRadioButton;
    @FXML
    private CheckBox boldCheckBox;
    @FXML
    private CheckBox italicCheckBox;

    private final ToggleGroup themeGroup = new ToggleGroup();
    private String currentThemeRadioButton = "";

    // ===================== Inizializzazione =====================
    @FXML
    private void initialize() {
        deuteranopiaRadioButton.setToggleGroup(themeGroup);
        trianopiaRadioButton.setToggleGroup(themeGroup);
        protanopiaRadioButton.setToggleGroup(themeGroup);

        updateInputFieldsToCurrentSettings();

    }

    // ===================== Metodi Ausiliari =====================

    private void popolaTemi() {
        themeField.getItems().add("Dark Theme");
        themeField.getItems().add("Light Theme");
        // themeField.getItems().add("Godfather Theme");
        /*
        tema del padrino pronto e funzionante, ma non inserito nel programma finale perché
        non necessario e non c'è abbastanza "spazio di manovra" nel modificare i colori
        senza intaccare l'accessibilità
         */

    }

    private void popolaFont(){
        fontField.getItems().add("Roboto");
        fontField.getItems().add("Dyslexia");
        fontField.getItems().add("Corleone");
        fontField.getItems().add("Sabon");
    }

    private void popolaTextSize(){
        textSizeField.getItems().add("Small");
        textSizeField.getItems().add("Medium");
        textSizeField.getItems().add("Big");
    }

    private void updateInputFieldsToCurrentSettings(){

        popolaTemi();
        popolaFont();
        popolaTextSize();

        String theme = sceneHandler.getTheme();

        if(theme.startsWith("dark")){
            themeField.getSelectionModel().select("Dark Theme");
        }
        else if(theme.startsWith("light")){
            themeField.getSelectionModel().select("Light Theme");
        }
        else if (theme.startsWith("godfather")){
            themeField.getSelectionModel().select("Godfather Theme");
        }

        if(theme.contains("Deuteranopia")){
            deuteranopiaRadioButton.setSelected(true);
        }
        else if(theme.contains("Protanopia")){
            protanopiaRadioButton.setSelected(true);
        }
        else if(theme.contains("Trianopia")){
            trianopiaRadioButton.setSelected(true);
        }

        String font = sceneHandler.getFont();
        if(font.startsWith("Roboto")){
            fontField.getSelectionModel().select("Roboto");
        }
        else if(font.startsWith("Dyslexia")){
            fontField.getSelectionModel().select("Dyslexia");
        }
        else if (font.startsWith("Corleone")) {
            fontField.getSelectionModel().select("Corleone");
        }
        else if (font.startsWith("Sabon")) {
            fontField.getSelectionModel().select("Sabon");
        }


        if(font.contains("Bold") || font.contains("Italic")){
            if(font.contains("Italic")){
                italicCheckBox.setSelected(true);
            }
            if(font.contains("Bold")){
                boldCheckBox.setSelected(true);
            }
        }
        else{
            boldCheckBox.setSelected(false);
            italicCheckBox.setSelected(false);
        }

        String textSize = sceneHandler.getFontSize();
        if(textSize.contains("Small")){
            textSizeField.getSelectionModel().select("Small");
        }
        else if(textSize.contains("Medium")){
            textSizeField.getSelectionModel().select("Medium");
        }
        else{
            textSizeField.getSelectionModel().select("Big");
        }

        disableStyles();

    }

    private void disableStyles() {

        if ( ! (fontField.getValue().equals("Roboto")) ) {
            boldCheckBox.setVisible(false);
            boldCheckBox.setSelected(false);

            italicCheckBox.setVisible(false);
            italicCheckBox.setSelected(false);
        } else {
            boldCheckBox.setVisible(true);
            italicCheckBox.setVisible(true);
        }

        if (fontField.getValue().equals("Dyslexia")) {
            textSizeField.getItems().remove("Big");
        } else {
            if (!textSizeField.getItems().contains("Big")) {
                textSizeField.getItems().add("Big");
            }
        }
    }

    // ===================== Gestione Eventi =====================

    @FXML
    private void tastoPremuto(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            applyChangesSubmit();
        }
    }

    @FXML
    private void disabilitaStili() {
        disableStyles();
    }

    @FXML
    private void themeRadioButtonClicked() {
        RadioButton selectedRadioButton = (RadioButton) themeGroup.getSelectedToggle();
        if (currentThemeRadioButton.equals(selectedRadioButton.getText())) {
            themeGroup.getSelectedToggle().setSelected(false);
            currentThemeRadioButton = "";
        }
        else{
            currentThemeRadioButton = selectedRadioButton.getText();
        }
    }

    @FXML
    private void applyChangesSubmit() {
        if(sceneHandler.askPermission("Apply changes", ASK_CHANGES)){

            /* Gestisci i temi */
            String theme = "godfather/godfather";
            if(themeField.getValue().equals("Light Theme")){
                theme = "light/light";
            }
            else if(themeField.getValue().equals("Dark Theme")){
                theme = "dark/dark";
            }

            RadioButton themeSelectedRadioButton = (RadioButton) themeGroup.getSelectedToggle();
            if (themeSelectedRadioButton != null) {
                theme += themeSelectedRadioButton.getText();
            }


            /* Gestisci i FONT*/
            String font = fontField.getValue();
            if(boldCheckBox.isSelected()){
                font += "-Bold";
            }
            if(italicCheckBox.isSelected()){
                font += "-Italic";
            }


            /*Gestisci la textSize*/
            String textSize = textSizeField.getValue();

            Settings settings = new Settings(theme, font, textSize);

            if(settings.equals(sceneHandler.getSettings())){
                sceneHandler.showSettingsScreen();
                return;
            }

            if(databaseHandler.modificaSettings(settings)){
                sceneHandler.alert("Success", SETTINGS_SUCCESS, "success");
            }
            else{
                sceneHandler.alert("Error", SETTINGS_ERROR, "error");
            }

            sceneHandler.setStyle(theme, font, textSize);
            sceneHandler.showSettingsScreen();
        }
    }



}
