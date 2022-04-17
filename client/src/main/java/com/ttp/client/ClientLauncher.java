package com.ttp.client;

import com.ttp.client.controller.ChatRoomController;
import com.ttp.client.controller.ClientConfigController;
import com.ttp.client.scenes.ClientScene;
import com.ttp.config.Config;
import com.ttp.config.ConfigFile;
import com.ttp.config.MapConfig;
import com.ttp.lanucher.Launcher;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashMap;


/**
 * JavaFX App
 */
public class ClientLauncher extends Launcher {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void startApplication(Stage stage) {
        sceneManager.switchScene(ClientScene.TITLE);
        stage.show();

        handleConfig();
    }

    private void handleConfig() {
        Config config;
        ConfigFile configFile = new ConfigFile(Path.of("client.config"), new MapConfig(new HashMap<>()));

        if (configFile.exists()) {
            config = configFile.read();
            ChatRoomController chatRoomController = (ChatRoomController) sceneManager.getController(ClientScene.CHAT_ROOM);
            chatRoomController.setConfig((MapConfig) config);
            sceneManager.switchScene(ClientScene.CHAT_ROOM);
        } else {
            ClientConfigController configController = (ClientConfigController) sceneManager.getController(ClientScene.CONFIG);
            configController.setConfigFile(configFile);
            sceneManager.switchScene(ClientScene.CONFIG);
        }
    }

}