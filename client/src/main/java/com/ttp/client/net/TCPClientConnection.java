package com.ttp.client.net;

import com.ttp.net.AppProtocol;
import com.ttp.net.Connection;
import com.ttp.net.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPClientConnection implements Connection {

    private String host;
    private int port;
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;

    public Socket getSocket() {
        return socket;
    }

    public TCPClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
        open();
    }

    @Override
    public void open() {
        try {
            socket = new Socket(InetAddress.getByName(host), port);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void connect() {
        send(AppProtocol.HANDSHAKE, "");
        Message message = receive();
        if (message.code() != AppProtocol.HANDSHAKE) {
            close();
        }
    }

    @Override
    public void send(AppProtocol code, String message) {
        try {
            dout.writeUTF(code.getCode() + message);
            dout.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Message receive() {
        AppProtocol code = AppProtocol.ERROR;
        String message = "";
        try {
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
