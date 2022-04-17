package com.ttp.lanucher;

import com.ttp.scenemanagement.SceneManager;
import com.ttp.scenemanagement.SceneManagerImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URL;

public abstract class Launcher extends Application {

    protected final URL resourcesPath = getClass().getResource("");
    protected SceneManager sceneManager;

    @Override
    public void start(Stage stage) {
        sceneManager = new SceneManagerImpl(resourcesPath, stage);

        startApplication(stage);
    }

    public abstract void startApplication(Stage stage);

}
