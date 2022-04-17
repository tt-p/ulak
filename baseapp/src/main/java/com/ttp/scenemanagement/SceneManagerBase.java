package com.ttp.scenemanagement;

import javafx.stage.Stage;

import java.net.URL;

public abstract class SceneManagerBase implements SceneManager {

    protected final Stage primaryStage;

    protected final URL resourceUrl;

    public SceneManagerBase(URL resourceUrl, Stage primaryStage) {
        if (primaryStage == null || resourceUrl == null) {
            throw new IllegalArgumentException();
        }
        this.primaryStage = primaryStage;
        this.resourceUrl = resourceUrl;
    }

}
