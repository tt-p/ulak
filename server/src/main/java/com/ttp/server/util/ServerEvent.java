package com.ttp.server.util;

import java.util.EventObject;

public class ServerEvent extends EventObject {

    private final String message;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ServerEvent(Object source, String message) {
        super(source);
        this.message = message + "\n";
    }

    public ServerEvent(Object source, String... messages) {
        super(source);
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append("\n");
        }
        this.message = sb.toString();
    }

    public String getMessage() {
        return message;
    }
}
