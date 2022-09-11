package com.ttp.server.concurrent;

import com.ttp.concurrent.HoldTerminateRunnable;
import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;
import com.ttp.server.net.User;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class RegisterTask extends HoldTerminateRunnable {

    private AtomicInteger totalUserCount;
    private Queue<Connection> connectionQueue;
    private Queue<User> registeredUserQueue;
    private Consumer<String> logger;

    public RegisterTask(
            AtomicBoolean isRunning,
            AtomicBoolean isTerminated,
            AtomicInteger totalUserCount,
            Queue<Connection> connectionQueue,
            Queue<User> registeredUserQueue,
            Consumer<String> logger
    ) {
        super(isRunning, isTerminated);
        this.totalUserCount = totalUserCount;
        this.connectionQueue = connectionQueue;
        this.registeredUserQueue = registeredUserQueue;
        this.logger = logger;
    }

    @Override
    protected void operate() {
        if (!connectionQueue.isEmpty()) {
            Connection connection = connectionQueue.poll();
            Message message = connection.receive();

            if (message != null && message.code() == AppProtocol.REGISTER) {
                String username = message.message();
                int id = totalUserCount.incrementAndGet();
                User user = new User(id, username, connection);
                connection.send(AppProtocol.OK, user.toString());
                logger.accept("User %s is registered".formatted(user.toString()));
                registeredUserQueue.add(user);
            } else {
                connection.close();
            }
        }

    }

    @Override
    protected void terminate() {
        System.out.println("register user stopped");
    }
}