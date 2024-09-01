package com.f4.logicielf4.Models;

import com.f4.logicielf4.Views.ViewFactory;

/**
 * Singleton! Nous permet d'avoir une seule instance de ViewFactory (Classe qui gère les vues).
 */
public class Model {
    private final ViewFactory viewFactory;
    private static Model model;
    private Model(){
        this.viewFactory = new ViewFactory();
    };

    public static synchronized Model getInstance() {
        if(model == null){
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
