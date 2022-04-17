package com.ttp.server.controller;

import com.ttp.config.ConfigFile;
import com.ttp.config.MapConfig;
import com.ttp.scenemanagement.AbstractController;
import com.ttp.server.scenes.ServerScene;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.Map;

public class ServerConfigController extends AbstractController {

    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfMaxUsers;

    private ConfigFile configFile;

    public void setConfigFile(ConfigFile configFile) {
        this.configFile = configFile;
    }

    @FXML
    private void onSave() {
        String port = tfPort.getText();
        String maxUsers = tfMaxUsers.getText();

        configFile.write(new MapConfig(
                Map.of(
                        "port", port,
                        "maxUsers", maxUsers
                )
        ));

        HomeController homeController = (HomeController) sceneManager.getController(ServerScene.HOME);
        homeController.setConfig((MapConfig) configFile.getConfig());
        sceneManager.switchScene(ServerScene.HOME);
    }

}
