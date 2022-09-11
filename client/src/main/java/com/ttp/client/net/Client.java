package com.ttp.client.net;

import com.ttp.client.concurrent.MessageReceiver;
import com.ttp.client.concurrent.MessageSender;
import com.ttp.concurrent.SimpleDaemonThreadFactory;
import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;
import javafx.application.Platform;

import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Client {

    private String hostname;
    private String username;
    private int port;
    private BiConsumer<String, MessageType> logger;

    private Connection connection;
    private ThreadFactory threadFactory = new SimpleDaemonThreadFactory();
    private ScheduledExecutorService scheduledExecutor;
    private AtomicBoolean isClientRunning = new AtomicBoolean();
    private AtomicBoolean isClientTerminated = new AtomicBoolean();
    private AtomicBoolean isReceiving = new AtomicBoolean();
    private ScheduledFuture<?> receiverFuture;
    private ScheduledFuture<?> senderFuture;

    private Queue<String> messageQueue = new LinkedBlockingQueue<>();

    public String getUsername() {
        return username;
    }

    public Client(String hostname,
                  int port,
                  String username,
                  BiConsumer<String, MessageType> logger
    ) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.logger = logger;
    }

    public void sendMessage(String message) {
        messageQueue.add(message);
    }

    public void stop() {
        connection.send(AppProtocol.LEAVE, "");
        connection.close();

        isClientRunning.set(false);
        isClientTerminated.set(true);

        receiverFuture.cancel(true);
        senderFuture.cancel(true);

        Platform.exit();
    }

    public void start() {
        connect();
        register();
        startMessaging();
    }

    private void connect() {
        try {
            TCPClientConnection temp = new TCPClientConnection(hostname, port);
            temp.getSocket().setSoTimeout(50);
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
        } else {
            username = message.message();
        }
    }

    private void startMessaging() {
        scheduledExecutor = Executors.newScheduledThreadPool(2, threadFactory);
        isClientRunning.set(true);
        isClientTerminated.set(false);

        startMessageReceivingTask();
        startMessageSendingTask();
    }

    private void startMessageReceivingTask() {
        MessageReceiver messageReceiver = new MessageReceiver(
                isClientRunning,
                isClientTerminated,
                isReceiving,
                connection,
                logger
        );
        receiverFuture = scheduledExecutor.scheduleAtFixedRate(
                messageReceiver, 50, 50, TimeUnit.MILLISECONDS
        );
    }

    private void startMessageSendingTask() {
        MessageSender messageSender = new MessageSender(
                isClientRunning,
                isClientTerminated,
                isReceiving,
                connection,
                messageQueue
        );
        senderFuture = scheduledExecutor.scheduleAtFixedRate(
                messageSender, 50, 50, TimeUnit.MILLISECONDS
        );
    }

}
