package com.ttp.scenemanagement;

public abstract class AbstractController {

    protected SceneManager sceneManager;

    protected void init() {
    }

    void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

}
