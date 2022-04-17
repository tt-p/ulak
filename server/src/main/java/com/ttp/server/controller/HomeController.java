package com.ttp.server.controller;

import com.ttp.config.MapConfig;
import com.ttp.scenemanagement.AbstractController;
import com.ttp.server.net.Server;
import com.ttp.server.util.ServerEvent;
import com.ttp.server.util.ServerEventListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;

import java.time.LocalDateTime;

public class HomeController extends AbstractController implements ServerEventListener {

    @FXML
    private Button btLog;
    @FXML
    private TextField tfInput;
    @FXML
    private TextArea taLog;
    @FXML
    private TreeView<String> tvInfo;

    private MapConfig config;
    private Server server;

    public void setConfig(MapConfig config) {
        this.config = config;
    }

    @Override
    public void init() {
        server = createServer();
        server.addListener(this);
        server.start();

        sceneManager.getPrimaryStage().setOnCloseRequest(event -> {
            server.stop();
        });
    }

    private Server createServer() {
        int port = Integer.parseInt(config.getConfigValue("port"));
        int maxUsers = Integer.parseInt(config.getConfigValue("port"));
        return new Server(port, maxUsers);
    }

    @Override
    public void handleServerEvent(ServerEvent event) {
        log(event.getMessage());
    }

    private void log(String message) {
        Platform.runLater(() -> taLog.appendText("%s - %s".formatted(LocalDateTime.now().toString(), message)));
    }

    @FXML
    public void btLogOnAction(ActionEvent event) {
        logMessage();
    }

    @FXML
    public void tfInputOnAction(ActionEvent event) {
        logMessage();
    }

    private void logMessage() {
        String input = tfInput.getText();
        tfInput.clear();
        taLog.appendText("root : %s\n".formatted(input));
        tfInput.requestFocus();
    }

}
