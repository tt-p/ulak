package com.ttp.client.controller;

import com.ttp.client.net.TCPClientConnection;
import com.ttp.config.MapConfig;
import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;
import com.ttp.scenemanagement.AbstractController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatRoomController extends AbstractController {

    @FXML
    private Button btSend;
    @FXML
    private TextField tfInput;
    @FXML
    private TextArea taChat;

    private MapConfig config;
    private String hostname;
    private String username;
    private int port;
    private Connection connection;
    private Queue<String> messageQueue = new LinkedBlockingQueue<>();
    private ScheduledExecutorService scheduledExecutor;

    private AtomicBoolean isClientRunning = new AtomicBoolean();
    private AtomicBoolean isReceiving = new AtomicBoolean();

    public void setConfig(MapConfig config) {
        this.config = config;
    }

    @Override
    public void init() {
        username = config.getConfigValue("username");
        hostname = config.getConfigValue("ip");
        port = Integer.parseInt(config.getConfigValue("port"));

        sceneManager.getPrimaryStage().setOnCloseRequest(event -> {
            connection.send(AppProtocol.LEAVE, "");
            isClientRunning.set(false);
            connection.close();
            Platform.exit();
        });

        connect();
        register();
        start();
    }

    private void connect() {
        try {
            TCPClientConnection temp = new TCPClientConnection(hostname, port);
            temp.getSocket().setSoTimeout(100);
            connection = temp;
            connection.connect();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void register() {
        connection.send(AppProtocol.REGISTER, username);
        Message message = connection.receive();
        if (message.code() != AppProtocol.OK) {
            connection.close();
        }
    }

    private void start() {
        scheduledExecutor = Executors.newScheduledThreadPool(2);
        isClientRunning.set(true);

        scheduledExecutor.scheduleAtFixedRate(new MessageReceiver(), 100, 100, TimeUnit.MILLISECONDS);
        scheduledExecutor.scheduleAtFixedRate(new MessageSender(), 100, 100, TimeUnit.MILLISECONDS);
    }

    class MessageReceiver implements Runnable {
        @Override
        public void run() {
            if (isClientRunning.get() && isReceiving.get()) {
                Message message = connection.receive();
                if (message != null) {
                    switch (message.code()) {
                        case TEXT -> {
                            int indexOfText = message.message().indexOf('-');
                            String username = message.message().substring(0, indexOfText);
                            String text = message.message().substring(indexOfText+1);
                            logMessage("%s: %s".formatted(username, text));
                        }
                        case NOTIFY_JOIN -> {
                            logMessage("User %s joined the chat room".formatted(message.message()));
                        }
                        case NOTIFY_LEAVE -> {
                            logMessage("User %s leaved the chat room".formatted(message.message()));
                        }
                        default -> throw new RuntimeException("Invalid state");
                    }
                }
                isReceiving.set(false);
            }
        }
    }

    class MessageSender implements Runnable {
        @Override
        public void run() {
            if (isClientRunning.get() && !isReceiving.get() && !messageQueue.isEmpty()) {
                String message = messageQueue.poll();
                connection.send(AppProtocol.TEXT, message);
            }
            isReceiving.set(true);
        }
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
        messageQueue.add(input);
    }

    private void logMessage(String message) {
        Platform.runLater(() -> taChat.appendText("%s\n".formatted(message)));
    }

}
