package com.ttp.client.controller;

import com.ttp.client.net.Client;
import com.ttp.config.MapConfig;
import com.ttp.scenemanagement.AbstractController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatRoomController extends AbstractController {

    @FXML
    private Button btSend;
    @FXML
    private TextField tfInput;
    @FXML
    private TextArea taChat;

    private MapConfig config;
    private Client client;

    public void setConfig(MapConfig config) {
        this.config = config;
    }

    @Override
    public void init() {
        client = createClient();
        client.start();

        sceneManager.getPrimaryStage().setOnCloseRequest(event -> client.stop());
    }

    private Client createClient() {
        String username = config.getConfigValue("username");
        String hostname = config.getConfigValue("ip");
        int port = Integer.parseInt(config.getConfigValue("port"));

        return new Client(hostname, port, username, this::log);
    }

    private void log(String message) {
        Platform.runLater(() -> taChat.appendText("%s%n".formatted(message)));
    }

    @FXML
    public void btSendOnAction(ActionEvent event) {
        sendMessage();
    }

    @FXML
    public void tfInputOnAction(ActionEvent event) {
        sendMessage();
    }

    private void sendMessage() {
        String input = tfInput.getText();
        tfInput.clear();
        tfInput.requestFocus();

        client.sendMessage(input);
    }

}
