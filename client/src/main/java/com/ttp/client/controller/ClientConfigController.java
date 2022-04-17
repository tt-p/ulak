package com.ttp.client.controller;

import com.ttp.client.scenes.ClientScene;
import com.ttp.config.ConfigFile;
import com.ttp.config.MapConfig;
import com.ttp.scenemanagement.AbstractController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.Map;

public class ClientConfigController extends AbstractController {

    @FXML
    private TextField tfIp;
    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfUsername;

    private ConfigFile configFile;

    public void setConfigFile(ConfigFile configFile) {
        this.configFile = configFile;
    }

    @FXML
    private void onSave() {
        String ip = tfIp.getText();
        String port = tfPort.getText();
        String maxUsers = tfUsername.getText();

        configFile.write(new MapConfig(
                Map.of(
                        "ip", ip,
                        "port", port,
                        "username", maxUsers
                )
        ));

        ChatRoomController chatRoomController = (ChatRoomController) sceneManager.getController(ClientScene.CHAT_ROOM);
        chatRoomController.setConfig((MapConfig) configFile.getConfig());
        sceneManager.switchScene(ClientScene.CHAT_ROOM);
    }

}
