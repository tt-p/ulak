package com.ttp.server.net;

import com.ttp.net.Connection;

public class User {

    private final long id;
    private final Connection con;
    private String username;

    public User(long id, String username, Connection connection) {
        this.id = id;
        this.username = username;
        this.con = connection;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Connection getConnection() {
        return con;
    }

    @Override
    public String toString() {
        return username + "#" + id;
    }
}
