package com.f4.logicielf4.Models;

import com.f4.logicielf4.Views.ViewFactory;

/**
 * Classe de modèle suivant le patron Singleton.
 * <p>
 * Cette classe assure qu'il n'y a qu'une seule instance de `ViewFactory`, qui est utilisée pour gérer les vues de l'application.
 * </p>
 */
public class Model {
    private final ViewFactory viewFactory;
    private static Model model;

    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * <p>
     * Initialise la `ViewFactory` pour gérer les vues.
     * </p>
     */
    private Model() {
        this.viewFactory = new ViewFactory();
    }

    /**
     * Obtient l'instance unique de la classe `Model`.
     *
     * @return l'instance unique de `Model`
     */
    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    /**
     * Obtient l'instance de `ViewFactory` associée au modèle.
     *
     * @return l'instance de `ViewFactory`
     */
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
