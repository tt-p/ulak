package com.ttp.server.concurrent;

import com.ttp.concurrent.HoldTerminateRunnable;
import com.ttp.net.AppProtocol;
import com.ttp.net.Message;
import com.ttp.server.net.User;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.ttp.util.JSONUtils.toJSON;

public class MessagingTask extends HoldTerminateRunnable {

    private final Queue<User> registeredUserQueue;
    private final Consumer<String> logger;

    public MessagingTask(
            AtomicBoolean isRunning,
            AtomicBoolean isTerminated,
            Queue<User> registeredUserQueue,
            Consumer<String> logger
    ) {
        super(isRunning, isTerminated);
        this.registeredUserQueue = registeredUserQueue;
        this.logger = logger;
    }

    @Override
    protected void operate() {
        if (!registeredUserQueue.isEmpty()) {
            registeredUserQueue.forEach(this::welcomeNewUser);
            registeredUserQueue.forEach(this::handleUser);
        }
    }

    private void welcomeNewUser(User user) {
        if (user.isNew()) {
            logger.accept("User %s joined the chat room".formatted(user.toString()));
            sendAllExceptSender(AppProtocol.NOTIFY_JOIN, user.toString(), user);
            user.setNewFalse();
        }
    }

    private void handleUser(User user) {
        Message message = user.getConnection().receive();

        if (message != null) {
            switch (message.code()) {
                case TEXT -> {
                    sendAllExceptSender(AppProtocol.TEXT, toJSON(user.toString(), message.message()), user);
                    logger.accept("User %s sent message".formatted(user.toString()));
                }
                case LEAVE -> {
                    registeredUserQueue.remove(user);
                    sendAllExceptSender(AppProtocol.NOTIFY_LEAVE, user.toString(), user);
                    logger.accept("User %s leaved the chat room".formatted(user.toString()));
                }
                default -> throw new RuntimeException("Invalid state");
            }
        }
    }

    private void sendAllExceptSender(AppProtocol code, String message, User sender) {
        registeredUserQueue.stream()
                .filter(user -> !user.equals(sender))
                .map(User::getConnection)
                .forEach(con -> con.send(code, message));
    }

    @Override
    protected void terminate() {
        System.out.println("messaging stopped");
    }

}