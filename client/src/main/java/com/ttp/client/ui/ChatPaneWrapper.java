package com.ttp.client.ui;

import com.ttp.client.net.MessageType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import static com.ttp.client.util.Utils.beautifyText;
import static com.ttp.util.JSONUtils.fromJSON;

public class ChatPaneWrapper {

    private Insets insets;

    private Pane chatPane;

    private String sentLabelStyle = """
        -fx-padding: 4 8 4 8;
        -fx-background-color: darkseagreen;
        -fx-background-radius: 12;
        -fx-border-width: 1;
        -fx-border-radius: 12;
    """;
    private String receivedLabelStyle = """
        -fx-padding: 4 8 4 8;
        -fx-background-color: darkslategray;
        -fx-background-radius: 12;
        -fx-border-width: 1;
        -fx-border-radius: 12;
    """;
    private String notificationLabelStyle = """
        -fx-padding: 4 6 4 6;
        -fx-text-fill: white;
        -fx-background-color: black;
        -fx-font-size: 10;
        -fx-background-radius: 6;
        -fx-border-width: 0.1;
        -fx-border-radius: 6;
    """;

    public Pane get() {
        return chatPane;
    }

    public ChatPaneWrapper(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height, Insets insets) {
        this.insets = insets;

        chatPane = new VBox(20);
        chatPane.setPrefWidth(width.doubleValue());
        chatPane.setPrefHeight(height.doubleValue());
        width.addListener((e) -> chatPane.setPrefWidth(width.doubleValue()));
        height.addListener((e) -> chatPane.setPrefHeight(height.doubleValue()));
        chatPane.setPadding(insets);
    }

    public void addMessage(String message, MessageType messageType) {
        chatPane.getChildren().add(createMessageBox(message, messageType));
    }

    private Parent createMessageBox(String message, MessageType messageType) {

        Pos allignment = switch (messageType) {
            case SENT: yield Pos.CENTER_RIGHT;
            case RECEIVED: yield Pos.CENTER_LEFT;
            case NOTIFICATION: yield Pos.CENTER;
        };

        HBox messageBox = new HBox();
        messageBox.setAlignment(allignment);

        if (MessageType.NOTIFICATION.equals(messageType)) {
            messageBox.getChildren().add(createNotificationLabel(message));
        } else {
            Pair<String, String> pair = fromJSON(message);

            if (MessageType.RECEIVED.equals(messageType)) {

                VBox vBox = new VBox(
                        crateReceivedLabel(pair.getKey()),
                        crateReceivedLabel(beautifyText(pair.getValue(), 65))
                );
                vBox.setStyle(receivedLabelStyle);
                vBox.setAlignment(allignment);
                messageBox.getChildren().add(vBox);

            } else {
                VBox vBox = new VBox(
                        createSentLabel(pair.getKey()),
                        createSentLabel(beautifyText(pair.getValue(), 65))
                );
                vBox.setStyle(sentLabelStyle);
                vBox.setAlignment(allignment);
                messageBox.getChildren().addAll(vBox);
            }
        }

        return messageBox;
    }

    private Label createNotificationLabel(String message) {
        Label label = new Label(message);
        label.setStyle(notificationLabelStyle);
        label.setTextAlignment(TextAlignment.CENTER);

        return label;
    }

    private Label createSentLabel(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        label.setTextAlignment(TextAlignment.RIGHT);

        return label;
    }

    private Label crateReceivedLabel(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        label.setTextAlignment(TextAlignment.LEFT);

        return label;
    }

}
