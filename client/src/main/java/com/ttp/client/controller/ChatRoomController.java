package com.ttp.client.controller;

import com.ttp.client.net.Client;
import com.ttp.client.net.MessageType;
import com.ttp.client.ui.ChatPaneWrapper;
import com.ttp.config.MapConfig;
import com.ttp.scenemanagement.AbstractController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import static com.ttp.util.JSONUtils.toJSON;

public class ChatRoomController extends AbstractController {

    @FXML
    private Button btSend;
    @FXML
    private TextField tfInput;
    @FXML
    private HBox hBox;

    private ChatPaneWrapper chatPaneWrapper;

    private MapConfig config;
    private Client client;

    public void setConfig(MapConfig config) {
        this.config = config;
    }

    @Override
    public void init() {
        client = createClient();
        client.start();

        tfInput.setStyle("-fx-border-radius: 24 0 0 24; -fx-background-radius: 24 0 0 24;");
        btSend.setStyle("-fx-border-radius: 0 24 24 0; -fx-background-radius: 0 24 24 0;");

        chatPaneWrapper = new ChatPaneWrapper(hBox.widthProperty(), hBox.heightProperty(), new Insets(10));

        hBox.getChildren().add(chatPaneWrapper.get());

        sceneManager.getPrimaryStage().setOnCloseRequest(event -> client.stop());
    }

    private Client createClient() {
        String username = config.getConfigValue("username");
        String hostname = config.getConfigValue("ip");
        int port = Integer.parseInt(config.getConfigValue("port"));

        return new Client(hostname, port, username, this::log);
    }

    private void log(String message, MessageType messageType) {
        Platform.runLater(() -> chatPaneWrapper.addMessage(message, messageType));
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

        chatPaneWrapper.addMessage(toJSON(client.getUsername(), input), MessageType.SENT);
        client.sendMessage(input);
    }

}
