package com.ttp.server.net;

import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;
import com.ttp.server.util.ServerEvent;
import com.ttp.server.util.ServerEventListener;

import java.net.SocketException;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private final int port;
    private final int maxUsers;
    private final List<EventListener> eventListeners = new LinkedList<>();
    private final Queue<User> userList = new LinkedBlockingQueue<>();
    private AtomicBoolean isServerRunning = new AtomicBoolean();
    private AtomicInteger totalUserCount = new AtomicInteger(0);

    private ScheduledExecutorService scheduledExecutor;
    private ExecutorService cachedExecutor;

    public Server(int port, int maxUsers) {
        this.port = port;
        this.maxUsers = maxUsers;
    }

    public void start() {
        fireEvent(new ServerEvent(this, "Server started"));
        scheduledExecutor = Executors.newScheduledThreadPool(2);
        cachedExecutor = Executors.newCachedThreadPool();
        isServerRunning.set(true);

        scheduledExecutor.scheduleAtFixedRate(new HandshakeUser(), 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        isServerRunning.set(false);
        for (User user : userList) {
            user.getConnection().close();
        }
    }

    public void addListener(ServerEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeListener(ServerEventListener listener) {
        eventListeners.remove(listener);
    }

    protected void fireEvent(String message) {
        fireEvent(new ServerEvent(this, message));
    }

    protected void fireEvent(ServerEvent event) {
        for (EventListener listener : eventListeners) {
            ((ServerEventListener) listener).handleServerEvent(event);
        }
    }

    class DebugSchedule implements Runnable {
        @Override
        public void run() {
            fireEvent("Test");
        }
    }

    class HandshakeUser implements Runnable {
        @Override
        public void run() {
            if (isServerRunning.get()) {
                fireEvent("Waiting connections...");
                Connection connection = new TCPServerConnection(port);
                connection.connect();

                Message message = connection.receive();
                fireEvent("Handshake start");

                if (message != null && message.code() == AppProtocol.HANDSHAKE) {
                    connection.send(AppProtocol.HANDSHAKE, "");
                    fireEvent("Handshake end");
                    cachedExecutor.submit(new RegisterUser(connection));
                } else {
                    connection.close();
                }
            }
        }
    }

    class RegisterUser implements Runnable {

        Connection connection;

        public RegisterUser(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            if (isServerRunning.get()) {
                Message message = connection.receive();

                if (message != null && message.code() == AppProtocol.REGISTER) {
                    String username = message.message();
                    int id = totalUserCount.incrementAndGet();
                    User user = new User(id, username, connection);
                    userList.add(user);
                    connection.send(AppProtocol.OK, "");
                    fireEvent("User %s is registered".formatted(user.toString()));
                    scheduledExecutor.scheduleAtFixedRate(new ChatRoomHandler(user), 100, 100, TimeUnit.MILLISECONDS);
                } else {
                    connection.close();
                }
            }
        }
    }

    class ChatRoomHandler implements Runnable {

        User user;

        public ChatRoomHandler(User user) {
            this.user = user;
            sendAll(AppProtocol.NOTIFY_JOIN, user.toString());
            fireEvent("User %s joined the chat room".formatted(user.toString()));
        }

        @Override
        public void run() {
            if (isServerRunning.get() && !userList.isEmpty()) {
                Message message = user.getConnection().receive();

                if (message != null) {
                    switch (message.code()) {
                        case TEXT -> {
                            sendAll(AppProtocol.TEXT, "%s-%s".formatted(user.toString(), message.message()));
                            fireEvent("User %s sent message".formatted(user.toString()));
                        }
                        case LEAVE -> {
                            userList.remove(user);
                            sendAll(AppProtocol.NOTIFY_LEAVE, user.toString());
                            fireEvent("User %s leaved the chat room".formatted(user.toString()));
                        }
                        default -> throw new RuntimeException("Invalid state");
                    }
                }
            }
        }

        void sendAll(AppProtocol code, String message) {
            userList.stream()
                    .map(User::getConnection)
                    .forEach(con -> con.send(code, message));
        }
    }

}
