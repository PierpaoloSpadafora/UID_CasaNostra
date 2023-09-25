package informatica.unical.spadafora.casanostra.handler;

import informatica.unical.spadafora.casanostra.model.Settings;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;

import informatica.unical.spadafora.casanostra.controller.SideController;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SceneHandler {

    // ===================== Costanti per messaggi e variabili =====================
    private static final String RESOURCE_PATH = "/informatica/unical/spadafora/casanostra/";
    private static final double FORM_WIDTH = 1350, FORM_HEIGHT = 920;
    private static final double opacita = 70;
    private static double screenWidth, screenHeight;
    private final static double resizedWidth = 1350, resizedHeight = 950;

    // ===================== Componenti UI =====================

    private Stage stage;
    private Scene scene;
    private Rectangle overlay;
    private AnchorPane workingArea;

    // ===================== Variabili di istanza =====================

    private String currentWorkingArea = "";
    private Settings settings = new Settings("","","");
    private final List<String> views = List.of("controlPanelView", "account/accountView", "settingsView", "creditsView");

    // ===================== Costruttore di una classe Singleton =====================

    private static final SceneHandler instance = new SceneHandler();
    private SceneHandler() {}
    public static SceneHandler getInstance() {
        return instance;
    }

    // ===================== Metodi riguardanti i settings =====================

    public void setStyle(String theme, String font, String size) {
        settings.setTheme(theme);
        settings.setFont(font);
        settings.setFontSize(size);
    }
    public Settings getSettings(){
        return settings;
    }
    public String getTheme(){
        return settings.getTheme();
    }
    public String getFont(){
        return settings.getFont();
    }
    public String getFontSize(){
        return settings.getFontSize();
    }
    public void setDefaultStyle() {
        settings.setTheme("dark/dark");
        settings.setFont("Roboto");
        settings.setFontSize("Medium");
    }

    // ===================== Metodi riguardanti i CSS =====================

    private void applicaCSS() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"css/"+settings.getTheme()+".css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"css/style.css")).toExternalForm());

        loadFont();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"fonts/"+settings.getFont()+".css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"css/textSize/"+settings.getFontSize()+".css")).toExternalForm());


    }
    private void applicaCSSalert(DialogPane dialogPane) {

        dialogPane.getStylesheets().clear();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"css/"+settings.getTheme()+".css")).toExternalForm());
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"css/style.css")).toExternalForm());

        loadFont();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"fonts/"+settings.getFont()+".css")).toExternalForm());
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(RESOURCE_PATH+"css/textSize/"+settings.getFontSize()+".css")).toExternalForm());
    }

    // ===================== Metodi riguardanti i Font =====================

    public void loadFont() {
        Font.loadFont(getClass().getResourceAsStream(RESOURCE_PATH + "fonts/" + settings.getFont() + ".ttf"), 20);
    }
    public void setFontAndReloadLogin(String newFont){
        settings.setFont(newFont);
        showLoginScreen();
    }
    public void setFontAndReloadRegistration(String newFont){
        settings.setFont(newFont);
        showRegistrationScreen();
    }
    public void setFontAndReloadUsernameRecovery(String newFont){
        settings.setFont(newFont);
        showUsernameRecoveryView();
    }
    public void setFontAndReloadAccessResetPassword(String newFont){
        settings.setFont(newFont);
        showAccessResetPasswordView();
    }
    public void setFontAndReloadResetPassword(String newFont){
        settings.setFont(newFont);
        showResetPasswordView();
    }

    // ===================== Metodi riguardanti gli alert =====================

    private void centraAlertNelloSchermo(Alert alert, Stage alertStage) {
        Platform.runLater(() -> {
            double centerX = stage.getX() + (stage.getWidth() / 2);
            double centerY = stage.getY() + (stage.getHeight() / 2);

            double alertWidth = alert.getDialogPane().getWidth();
            double alertHeight = alert.getDialogPane().getHeight();

            double alertX = centerX - (alertWidth / 2);
            double alertY = centerY - (alertHeight / 2);

            alertStage.setX(alertX);
            alertStage.setY(alertY);
        });
    }

    public void alert(String title, String message, String type) {

        overlay.setVisible(true);

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        applicaCSSalert(alert.getDialogPane());

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.UNDECORATED);

        centraAlertNelloSchermo(alert, alertStage);

        FontIcon icon = new FontIcon();

        alert.getDialogPane().getStyleClass().add("alertBase");
        icon.getStyleClass().add("iconBase");

        switch (type) {
            case "error" -> {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.getDialogPane().getStyleClass().add("errorAlert");

                icon.setIconLiteral("mdral-error_outline");
                icon.getStyleClass().add("iconErrorAlert");
                alert.getDialogPane().setGraphic(icon);
            }
            case "info" -> {

                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStyleClass().add("infoAlert");

                icon.setIconLiteral("mdoal-info");
                icon.getStyleClass().add("iconInfoAlert");
                alert.getDialogPane().setGraphic(icon);
            }
            case "warning" -> {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.getDialogPane().getStyleClass().add("warningAlert");

                icon.setIconLiteral("mdrmz-warning");
                icon.getStyleClass().add("iconWarningAlert");
                alert.getDialogPane().setGraphic(icon);
            }
            case "success" ->{
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStyleClass().add("successAlert");

                icon.setIconLiteral("mdal-check_circle_outline");
                icon.getStyleClass().add("iconSuccessAlert");
                alert.getDialogPane().setGraphic(icon);
            }
        }

        alert.showAndWait();

        overlay.setVisible(false);
    }
    public Boolean askPermissionDanger(String title, String message) {
        overlay.setVisible(true);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStyleClass().add("errorAlert");
        applicaCSSalert(alert.getDialogPane());

        FontIcon icon = new FontIcon("mdrmz-warning");

        alert.getDialogPane().getStyleClass().add("alertBase");
        icon.getStyleClass().add("iconBase");

        icon.getStyleClass().add("iconErrorAlert");
        alert.getDialogPane().setGraphic(icon);

        Stage askPermissionStage = (Stage) alert.getDialogPane().getScene().getWindow();
        askPermissionStage.initStyle(StageStyle.UNDECORATED);

        centraAlertNelloSchermo(alert, askPermissionStage);

        Optional<ButtonType> result = alert.showAndWait();

        overlay.setVisible(false);
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                return true;
            } else if (result.get() == ButtonType.CANCEL) {
                return false;
            }
        }
        return false;
    }
    public Boolean askPermission(String title, String message) {
        overlay.setVisible(true);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStyleClass().add("infoAlert");
        applicaCSSalert(alert.getDialogPane());

        FontIcon icon = new FontIcon("mdi2c-comment-question-outline");

        alert.getDialogPane().getStyleClass().add("alertBase");
        icon.getStyleClass().add("iconBase");

        icon.getStyleClass().add("iconInfoAlert");
        alert.getDialogPane().setGraphic(icon);

        Stage askPermissionStage = (Stage) alert.getDialogPane().getScene().getWindow();
        askPermissionStage.initStyle(StageStyle.UNDECORATED);

        centraAlertNelloSchermo(alert, askPermissionStage);

        Optional<ButtonType> result = alert.showAndWait();

        overlay.setVisible(false);
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                return true;
            } else if (result.get() == ButtonType.CANCEL) {
                return false;
            }
        }
        return false;
    }
    public void showPopupMessage(String message) {

        overlay.setVisible(true);

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().add(closeButton);

        alert.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        alert.getDialogPane().getStyleClass().add("popupMessage");
        applicaCSSalert(alert.getDialogPane());

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.UNDECORATED);

        alert.getDialogPane().setGraphic(null);

        centraAlertNelloSchermo(alert, alertStage);

        alert.showAndWait();

        overlay.setVisible(false);
    }

    // ===================== Utility =====================

    public double getWindowWidth(){
        return stage.getWidth();
    }
    public double getWindowHeight(){
        return stage.getHeight();
    }
    private void logError(String message) {
        System.out.println(message);
        alert("Error", message, "error");
    }
    public Image getDyslexiaImage(String fileName) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCE_PATH+"pictures/"+fileName+".png")));
    }
    public String getDirectoryChooserPath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the folder in which to save the file");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory == null){
            return "?errore?";
        }
        else{
            return selectedDirectory.getAbsolutePath() + "\\";
        }
    }
    public String getFileChooserPath(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the file to import");

        File selectedFile = fileChooser.showOpenDialog(stage);

        if(selectedFile == null){
            return "";
        }
        else{
            return selectedFile.getAbsolutePath();
        }
    }
    public void addHeightListenerToScrollPane(ScrollPane scrollPane, double minHeight) {
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = newValue.doubleValue();
            if (newHeight > resizedHeight) {
                double diff = newHeight - resizedHeight;
                scrollPane.setMinHeight(minHeight + diff);
            } else {
                scrollPane.setMinHeight(minHeight);
            }
        });
    }

    // ===================== Metodi riguardanti il cambio di schermate =====================

    public void showSplashScreenAndThenLogin(){
        // Crea la Splash Screen
        Stage splashStage = new Stage();
        StackPane root = new StackPane();

        // Crea e applica il gradiente come sfondo
        LinearGradient linearGradient = new LinearGradient(
                0, 1, 0, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#030303")),
                new Stop(0.25, Color.web("#912411")),
                new Stop(0.5, Color.web("#a46329")),
                new Stop(0.75, Color.web("#c6a25a")),
                new Stop(1, Color.web("#d3cfc3"))
        );
        BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        root.setBackground(background);

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCE_PATH + "pictures/SplashScreen.png")));
        ImageView imageView = new ImageView(image);

        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());

        imageView.fitHeightProperty().bind(root.heightProperty());
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        root.getChildren().add(imageView);

        Scene scene = new Scene(root, FORM_WIDTH, FORM_HEIGHT);
        splashStage.setScene(scene);
        splashStage.setResizable(false);
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.initModality(Modality.APPLICATION_MODAL);

        splashStage.show();

        // Imposta la durata della Splash Screen (puramente fittizia)
        double duration = 0.3;
        PauseTransition delay = new PauseTransition(Duration.seconds(duration));
        delay.setOnFinished(event -> {
            showLoginScreen();
            splashStage.close();
        });
        delay.play();
    }
    public void showLoginScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/userAccessPoint/loginView.fxml"));
            AnchorPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, FORM_WIDTH, FORM_HEIGHT);
            applicaCSS();

            stage.setScene(scene);
            stage.setTitle("Casa Nostra - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }
        catch (IOException e) {
            logError(e.getMessage());
        }

    }
    public void showRegistrationScreen(){

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/userAccessPoint/registrationView.fxml"));
            scene = new Scene(loader.load(), FORM_WIDTH, FORM_HEIGHT);

            applicaCSS();

            stage.setScene(scene);
            stage.setTitle("Casa Nostra - Registration");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }
        catch (IOException e) {
            logError(e.getMessage());
        }

    }
    public void showUsernameRecoveryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/userAccessPoint/usernameRecoveryView.fxml"));
            AnchorPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, FORM_WIDTH, FORM_HEIGHT);
            applicaCSS();

            stage.setScene(scene);
            stage.setTitle("Casa Nostra - Password Reset");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            logError(e.getMessage());
        }
    }
    public void showAccessResetPasswordView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/userAccessPoint/accessResetPasswordView.fxml"));
            AnchorPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, FORM_WIDTH, FORM_HEIGHT);
            applicaCSS();

            stage.setScene(scene);
            stage.setTitle("Casa Nostra - Password Reset");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            logError(e.getMessage());
        }
    }
    public void showResetPasswordView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/userAccessPoint/resetPasswordView.fxml"));
            AnchorPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, FORM_WIDTH, FORM_HEIGHT);
            applicaCSS();

            stage.setScene(scene);
            stage.setTitle("Casa Nostra - Password Reset");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            logError(e.getMessage());
        }
    }
    public void showMainScreen(double width, double height){

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/sideView.fxml"));

            BorderPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, width, height);

            workingArea = ( (SideController) loader.getController()).getWorkingArea();

            loadWorkingArea("controlPanelView", "XX");

            applicaCSS();

            stage.setResizable(true);
            stage.setScene(scene);
            stage.setTitle("Casa Nostra");
            stage.setMinWidth(resizedWidth);
            stage.setMinHeight(resizedHeight);

            stage.setMaxWidth(screenWidth);

            double taskbarHeight = screenHeight - Screen.getPrimary().getVisualBounds().getHeight();

            stage.setMaxHeight(screenHeight - taskbarHeight);

            stage.centerOnScreen();
            stage.show();
        }
        catch (IOException e) {
            logError(e.getMessage());
        }
    }
    public void showSettingsScreen() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/sideView.fxml"));

            BorderPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, getWindowWidth(), getWindowHeight());

            workingArea = ( (SideController) loader.getController()).getWorkingArea();

            loadWorkingArea("controlPanelView", "XX");

            ((SideController) loader.getController()).settingsClicked();

            applicaCSS();

            stage.setResizable(true);
            stage.setScene(scene);
            stage.setTitle("Casa Nostra");
            stage.setMinWidth(resizedWidth); // setta la larghezza minima - 1350 per permettere di visualizzare adeguatamente gli elementi grafici
            stage.setMinHeight(resizedHeight); // setta l'altezza minima - 920 per mantenere un rapporto più simile possibile al 16:9 e permettere di visualizzare adeguatamente gli elementi grafici

            stage.setWidth(resizedWidth);
            stage.setHeight(resizedHeight);

            stage.setMaxWidth(screenWidth);
            stage.setMaxHeight(screenHeight);

            stage.centerOnScreen();

            stage.setMaximized(false);

            stage.show();
        }
        catch (IOException e) {
            logError(e.getMessage());
        }
    }
    public void showAccountScreen() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/sideView.fxml"));

            BorderPane content = loader.load();

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(content, overlay);

            scene = new Scene(rootPane, getWindowWidth(), getWindowHeight());

            workingArea = ( (SideController) loader.getController()).getWorkingArea();

            loadWorkingArea("account/accountView", "XX");

            ((SideController) loader.getController()).accountClicked();

            applicaCSS();

            stage.setResizable(true);
            stage.setScene(scene);
            stage.setTitle("Casa Nostra");
            stage.setMinWidth(resizedWidth); // setta la larghezza minima - 1350 per permettere di visualizzare adeguatamente gli elementi grafici
            stage.setMinHeight(resizedHeight); // setta l'altezza minima - 950 per mantenere un rapporto più simile possibile al 16:9 e permettere di visualizzare adeguatamente gli elementi grafici

            stage.setMaxWidth(screenWidth);
            stage.setMaxHeight(screenHeight);

            stage.centerOnScreen();

            stage.setMaximized(false);

            stage.show();
        }
        catch (IOException e) {
            logError(e.getMessage());
        }
    }

    public Object loadWorkingArea(String viewName, String direzione){

        try {
            if (viewName.equals(currentWorkingArea)) {
                return null;
            }
            int currentIndex = views.indexOf(currentWorkingArea);
            int targetIndex = views.indexOf(viewName);

            if (currentIndex != -1 && targetIndex != -1) {
                if (targetIndex > currentIndex) {
                    direzione = "GIU";
                } else if (targetIndex < currentIndex) {
                    direzione = "SU";
                }
            }

            currentWorkingArea = viewName;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_PATH + "view/" + viewName + ".fxml"));
            AnchorPane newView = loader.load();

            newView.setPrefHeight(workingArea.getHeight());
            newView.setPrefWidth(workingArea.getWidth());

            TranslateTransition tt = new TranslateTransition(Duration.seconds(0.3), newView);
            ScaleTransition st = new ScaleTransition(Duration.seconds(0.3), newView);

            switch (direzione) {
                case "DESTRA" -> {
                    newView.setTranslateX(screenWidth);
                    tt.setToX(0);
                }
                case "SINISTRA" -> {
                    newView.setTranslateX(-screenWidth);
                    tt.setToX(0);  // farà scivolare la nuova vista da sinistra a destra
                }
                case "SU" -> {
                    newView.setTranslateY(-screenHeight);
                    tt.setToY(0);  // farà scivolare la nuova vista dall'alto verso il basso
                }
                case "GIU" -> {
                    newView.setTranslateY(screenHeight);
                    tt.setToY(0);  // farà scivolare la nuova vista dal basso verso l'alto
                }
                case "XX" -> {
                    newView.setScaleX(0);
                    newView.setScaleY(0);
                    st.setToX(1);
                    st.setToY(1);
                }
                case "NO" ->{
                    // nessuna transizione
                }
            }

            workingArea.getChildren().clear();
            workingArea.getChildren().add(newView);

            tt.setOnFinished(event -> {
                if (workingArea.getChildren().size() > 1) {
                    workingArea.getChildren().remove(0);
                }
                AnchorPane.setTopAnchor(newView, 0.0);
                AnchorPane.setBottomAnchor(newView, 0.0);
                AnchorPane.setLeftAnchor(newView, 0.0);
                AnchorPane.setRightAnchor(newView, 0.0);
            });

            ParallelTransition pt = new ParallelTransition(tt, st);

            pt.play();

            return loader.getController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ===================== Initialize =====================

    public void init(Stage primaryStage) {

        setDefaultStyle();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        screenWidth = screenBounds.getWidth();
        screenHeight = screenBounds.getHeight();

        overlay = new Rectangle(0, 0, screenWidth, screenHeight);
        overlay.setFill(new Color(0, 0, 0, opacita/100));
        overlay.setVisible(false);

        stage = primaryStage;
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCE_PATH+"pictures/Logo.png"))));

        showSplashScreenAndThenLogin();
    }

}