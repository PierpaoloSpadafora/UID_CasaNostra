module informatica.unical.spadafora.casanostra {
    // Required modules
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires spring.security.crypto;
    requires com.google.gson;

    // Export and open main packages
    exports informatica.unical.spadafora.casanostra;
    opens informatica.unical.spadafora.casanostra to javafx.fxml;

    // Export and open controller packages
    exports informatica.unical.spadafora.casanostra.controller;
    opens informatica.unical.spadafora.casanostra.controller to javafx.fxml;

    exports informatica.unical.spadafora.casanostra.controller.transactions;
    opens informatica.unical.spadafora.casanostra.controller.transactions to javafx.fxml;

    exports informatica.unical.spadafora.casanostra.controller.categories;
    opens informatica.unical.spadafora.casanostra.controller.categories to javafx.fxml;

    // Model package
    opens informatica.unical.spadafora.casanostra.model to javafx.base, com.google.gson;
    exports informatica.unical.spadafora.casanostra.controller.account;
    opens informatica.unical.spadafora.casanostra.controller.account to javafx.fxml;
    exports informatica.unical.spadafora.casanostra.controller.userAccessPoint;
    opens informatica.unical.spadafora.casanostra.controller.userAccessPoint to javafx.fxml;
    exports informatica.unical.spadafora.casanostra.controller.controlPanel;
    opens informatica.unical.spadafora.casanostra.controller.controlPanel to javafx.fxml;

}