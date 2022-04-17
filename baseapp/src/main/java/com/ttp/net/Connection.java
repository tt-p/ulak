package com.ttp.net;

import java.net.InetAddress;

public interface Connection {
    void open();

    void close();

    void connect();

    void send(AppProtocol code, String message);

    Message receive();

    boolean isConnected();

    InetAddress getAddress();

    int getPort();
}