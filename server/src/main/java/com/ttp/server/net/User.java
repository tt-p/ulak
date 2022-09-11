package com.ttp.server.net;

import com.ttp.net.Connection;

public class User {

    private final long id;
    private final Connection con;
    private final String username;
    private boolean isNew;

    public User(long id, String username, Connection connection) {
        this.id = id;
        this.username = username;
        this.con = connection;
        this.isNew = true;
    }

    public void setNewFalse() {
        isNew = false;
    }

    public boolean isNew() {
        return isNew;
    }

    public Connection getConnection() {
        return con;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return username + "#" + id;
    }
}
