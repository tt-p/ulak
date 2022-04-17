package com.ttp.server.net;

import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPServerConnection implements Connection {

    private static ServerSocket serverSocket;
    private final int port;
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public TCPServerConnection(int port) {
        this.port = port;
        open();
    }

    @Override
    public void open() {
        try {
            if (serverSocket == null) {
                serverSocket = new ServerSocket(port);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            socket = serverSocket.accept();
            socket.setSoTimeout(100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void send(AppProtocol code, String message) {
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(code.getCode() + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Message receive() {
        AppProtocol code = AppProtocol.ERROR;
        String message = "";
        try {
            DataInputStream din = new DataInputStream(socket.getInputStream());
            String result = din.readUTF();
            code = AppProtocol.convert(result.substring(0, 2));
            message = result.substring(2);
        } catch (SocketTimeoutException e) {
          return null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new Message(code, message);
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    @Override
    public InetAddress getAddress() {
        return socket.getInetAddress();
    }

    @Override
    public int getPort() {
        return socket.getPort();
    }

}
