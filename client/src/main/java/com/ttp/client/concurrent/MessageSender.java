package com.ttp.client.concurrent;

import com.ttp.concurrent.HoldTerminateRunnable;
import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageSender extends HoldTerminateRunnable {

    private AtomicBoolean isReceiving;
    private Connection connection;
    private Queue<String> messageQueue;

    public MessageSender(
            AtomicBoolean isRunning,
            AtomicBoolean isTerminated,
            AtomicBoolean isReceiving,
            Connection connection,
            Queue<String> messageQueue
    ) {
        super(isRunning, isTerminated);
        this.isReceiving = isReceiving;
        this.connection = connection;
        this.messageQueue = messageQueue;
    }

    @Override
    protected void operate() {
        if (!isReceiving.get() && !messageQueue.isEmpty()) {
            String message = messageQueue.poll();
            connection.send(AppProtocol.TEXT, message);
        }
        isReceiving.set(true);
    }

    @Override
    protected void terminate() {
        System.out.println("sender stopped");
    }
}