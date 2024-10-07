package com.f4.logicielf4.Models;

import com.f4.logicielf4.Views.ViewFactory;

/**
 * Classe de modèle suivant le patron Singleton.
 * <p>
 * Cette classe garantit qu'il n'y a qu'une seule instance de `ViewFactory`, utilisée pour gérer les vues de l'application.
 * </p>
 */
public class Model {
    private final ViewFactory viewFactory;
    private static Model model;

    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * <p>
     * Initialise une instance de `ViewFactory` pour gérer les vues de l'application.
     * </p>
     */
    private Model() {
        this.viewFactory = new ViewFactory();
    }

    /**
     * Retourne l'instance unique de la classe `Model`.
     * <p>
     * Si l'instance n'existe pas encore, elle est créée. Sinon, l'instance existante est retournée.
     * Ce mécanisme permet de suivre le patron de conception Singleton, garantissant qu'une seule instance
     * de cette classe est utilisée à travers l'application.
     * </p>
     *
     * @return L'instance unique de `Model`.
     */
    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    /**
     * Retourne l'instance de `ViewFactory` associée au modèle.
     * <p>
     * `ViewFactory` est utilisée pour gérer toutes les vues de l'application.
     * </p>
     *
     * @return L'instance de `ViewFactory`.
     */
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
