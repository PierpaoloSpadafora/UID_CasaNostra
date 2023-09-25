package informatica.unical.spadafora.casanostra;

import informatica.unical.spadafora.casanostra.handler.DatabaseHandler;
import javafx.application.Application;
import informatica.unical.spadafora.casanostra.handler.SceneHandler;
import javafx.application.Platform;
import javafx.stage.Stage;



public class MainApplication extends Application {


    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    @Override
    public void start(Stage stage){
        SceneHandler.getInstance().init(stage);
    }

    @Override
    public void init() {
        databaseHandler.openConnection();
    }

    @Override
    public void stop() {
        databaseHandler.closeConnection();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}