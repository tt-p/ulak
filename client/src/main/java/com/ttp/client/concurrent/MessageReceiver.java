package com.ttp.client.concurrent;

import com.ttp.client.net.MessageType;
import com.ttp.concurrent.HoldTerminateRunnable;
import com.ttp.net.Connection;
import com.ttp.net.Message;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageReceiver extends HoldTerminateRunnable {

    private AtomicBoolean isReceiving;
    private Connection connection;
    private BiConsumer<String, MessageType> logger;

    public MessageReceiver(
            AtomicBoolean isRunning,
            AtomicBoolean isTerminated,
            AtomicBoolean isReceiving,
            Connection connection,
            BiConsumer<String, MessageType> logger

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
                        logger.accept(message.message(), MessageType.RECEIVED);
                    }
                    case NOTIFY_JOIN -> {
                        logger.accept(
                                "User %s joined the chat room".formatted(message.message()), MessageType.NOTIFICATION);
                    }
                    case NOTIFY_LEAVE -> {
                        logger.accept(
                                "User %s leaved the chat room".formatted(message.message()), MessageType.NOTIFICATION);
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