package com.ttp.scenemanagement;

import javafx.stage.Stage;

public interface SceneManager {
    void switchScene(ScenePath scenePath);

    Object getController(ScenePath scenePath);

    Stage getPrimaryStage();

}
