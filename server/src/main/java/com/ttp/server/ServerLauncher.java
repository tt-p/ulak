package com.ttp.server;

import com.ttp.config.Config;
import com.ttp.config.ConfigFile;
import com.ttp.config.MapConfig;
import com.ttp.lanucher.Launcher;
import com.ttp.server.controller.HomeController;
import com.ttp.server.controller.ServerConfigController;
import com.ttp.server.scenes.ServerScene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * JavaFX App
 */
public class ServerLauncher extends Launcher {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void startApplication(Stage stage) {
        sceneManager.switchScene(ServerScene.TITLE);
        stage.show();

        handleConfig();
    }

    private void handleConfig() {
        Config config;
        ConfigFile configFile = new ConfigFile(Path.of("server.config"), new MapConfig(new HashMap<>()));

        if (configFile.exists()) {
            config = configFile.read();
            HomeController homeController = (HomeController) sceneManager.getController(ServerScene.HOME);
            homeController.setConfig((MapConfig) config);
            sceneManager.switchScene(ServerScene.HOME);
        } else {
            ServerConfigController configController = (ServerConfigController) sceneManager.getController(ServerScene.CONFIG);
            configController.setConfigFile(configFile);
            sceneManager.switchScene(ServerScene.CONFIG);
        }
    }

}