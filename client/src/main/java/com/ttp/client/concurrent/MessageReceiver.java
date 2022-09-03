package com.ttp.client.concurrent;

import com.ttp.concurrent.HoldTerminateRunnable;
import com.ttp.net.Connection;
import com.ttp.net.Message;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MessageReceiver extends HoldTerminateRunnable {

    private AtomicBoolean isReceiving;
    private Connection connection;
    private Consumer<String> logger;

    public MessageReceiver(
            AtomicBoolean isRunning,
            AtomicBoolean isTerminated,
            AtomicBoolean isReceiving,
            Connection connection,
            Consumer<String> logger
    ) {
        super(isRunning, isTerminated);
        this.isReceiving = isReceiving;
        this.connection = connection;
        this.logger = logger;
    }

    @Override
    protected void operate() {
        if (isReceiving.get()) {
            Message message = connection.receive();
            if (message != null) {
                switch (message.code()) {
                    case TEXT -> {
                        int indexOfText = message.message().indexOf('-');
                        String username = message.message().substring(0, indexOfText);
                        String text = message.message().substring(indexOfText+1);
                        logger.accept("%s: %s".formatted(username, text));
                    }
                    case NOTIFY_JOIN -> {
                        logger.accept("User %s joined the chat room".formatted(message.message()));
                    }
                    case NOTIFY_LEAVE -> {
                        logger.accept("User %s leaved the chat room".formatted(message.message()));
                    }
                    default -> throw new RuntimeException("Invalid state");
                }
            }
            isReceiving.set(false);
        }
    }

    @Override
    protected void terminate() {
        System.out.println("receiver stopped");
    }

}