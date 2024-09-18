package com.f4.logicielf4;

import com.f4.logicielf4.Views.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * La classe principale de l'application JavaFX.
 * Cette classe étend {@link Application} et sert de point d'entrée pour l'application.
 */
public class App extends Application {

    /**
     * Méthode appelée lors du lancement de l'application.
     * Initialise et affiche la fenêtre de connexion.
     *
     * @param stage Le stage principal de l'application (non utilisé).
     */
    @Override
    public void start(Stage stage) {
        ViewFactory viewFactory = new ViewFactory();
       // viewFactory.showLoginWindow();
        viewFactory.showAdminWindow();
    }
}
