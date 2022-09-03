package com.ttp.server.concurrent;

import com.ttp.concurrent.HoldTerminateRunnable;
import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;
import com.ttp.server.net.TCPServerConnection;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class HandshakeTask extends HoldTerminateRunnable {

    private int port;
    private Queue<Connection> connectionQueue;
    private Consumer<String> logger;

    public HandshakeTask(
            AtomicBoolean isRunning,
            AtomicBoolean isTerminated,
            int port,
            Queue<Connection> connectionQueue,
            Consumer<String> logger
    ) {
        super(isRunning, isTerminated);
        this.port = port;
        this.connectionQueue = connectionQueue;
        this.logger = logger;
    }

    @Override
    protected void operate() {
        logger.accept("Waiting connections...");
        Connection connection = new TCPServerConnection(port);
        connection.connect();

        Message message = connection.receive();
        logger.accept("Handshake start");

        if (message != null && message.code() == AppProtocol.HANDSHAKE) {
            connection.send(AppProtocol.HANDSHAKE, "");
            logger.accept("Handshake end");
            connectionQueue.add(connection);
        } else {
            connection.close();
        }
    }

    @Override
    protected void terminate() {
        System.out.println("Handshake stopped");
    }
}