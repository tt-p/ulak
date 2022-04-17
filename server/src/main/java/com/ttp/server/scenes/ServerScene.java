package com.ttp.server.scenes;

import com.ttp.scenemanagement.ScenePath;

public final class ServerScene extends ScenePath {

    public static ScenePath TITLE = new ServerScene("title.fxml");
    public static ScenePath HOME = new ServerScene("home.fxml");
    public static ScenePath CONFIG = new ServerScene("config.fxml");

    private ServerScene(String path) {
        super(path);
    }

}
