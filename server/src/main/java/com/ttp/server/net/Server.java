package com.ttp.server.net;

import com.ttp.concurrent.SimpleDaemonThreadFactory;
import com.ttp.net.Connection;
import com.ttp.server.concurrent.MessagingTask;
import com.ttp.server.concurrent.HandshakeTask;
import com.ttp.server.concurrent.RegisterTask;
import javafx.application.Platform;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Server {

    private final int port;
    private final int maxUsers;
    private Consumer<String> logger;

    private final Queue<Connection> connectionQueue = new LinkedBlockingQueue<>();
    private final Queue<User> registeredUserQueue = new LinkedBlockingQueue<>();
    private AtomicBoolean isServerRunning = new AtomicBoolean();
    private AtomicBoolean isServerTerminated = new AtomicBoolean();
    private AtomicInteger totalUserCount = new AtomicInteger(0);

    private ThreadFactory threadFactory = new SimpleDaemonThreadFactory();
    private ScheduledExecutorService scheduledExecutor;
    private ScheduledFuture<?> handshakeFuture;
    private ScheduledFuture<?> registerFuture;
    private ScheduledFuture<?> messagingFuture;

    public Server(int port, int maxUsers, Consumer<String> logger) {
        this.port = port;
        this.maxUsers = maxUsers;
        this.logger = logger;
    }

    public void stop() {
        isServerRunning.set(false);
        isServerTerminated.set(true);

        for (User user : registeredUserQueue) {
            user.getConnection().close();
        }

        handshakeFuture.cancel(true);
        registerFuture.cancel(true);
        messagingFuture.cancel(true);

        Platform.exit();
    }

    public void start() {
        logger.accept("Server started");
        scheduledExecutor = Executors.newScheduledThreadPool(2, threadFactory);

        isServerRunning.set(true);
        isServerTerminated.set(false);

        startHandshakeTask();
        startRegisterTask();
        startMessagingTask();
    }

    private void startHandshakeTask() {
        HandshakeTask handshakeTask = new HandshakeTask(
                isServerRunning,
                isServerTerminated,
                port,
                connectionQueue,
                logger
        );
        handshakeFuture = scheduledExecutor.scheduleAtFixedRate(
                handshakeTask, 1, 1, TimeUnit.SECONDS
        );
    }

    private void startRegisterTask() {
        RegisterTask registerTask = new RegisterTask(
                isServerRunning,
                isServerTerminated,
                totalUserCount,
                connectionQueue,
                registeredUserQueue,
                logger
        );
        registerFuture = scheduledExecutor.scheduleAtFixedRate(
                registerTask, 0, 25, TimeUnit.MILLISECONDS
        );
    }

    private void startMessagingTask() {
        MessagingTask messagingTask = new MessagingTask(
                isServerRunning,
                isServerTerminated,
                registeredUserQueue,
                logger
        );
        messagingFuture = scheduledExecutor.scheduleAtFixedRate(
                messagingTask, 0,25, TimeUnit.MILLISECONDS
        );
    }

}
