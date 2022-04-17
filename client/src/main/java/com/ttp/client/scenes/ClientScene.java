package com.ttp.client.scenes;

import com.ttp.scenemanagement.ScenePath;

public final class ClientScene extends ScenePath {

    public static ScenePath TITLE = new ClientScene("title.fxml");
    public static ScenePath CHAT_ROOM = new ClientScene("chatroom.fxml");
    public static ScenePath CONFIG = new ClientScene("config.fxml");

    private ClientScene(String path) {
        super(path);
    }

}
