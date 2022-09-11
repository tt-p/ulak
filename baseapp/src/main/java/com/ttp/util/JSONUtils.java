package com.ttp.util;

import javafx.util.Pair;
import org.json.JSONObject;

public final class JSONUtils {

    private JSONUtils() {}

    public static String toJSON(String username, String message) {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("message", message);
        return object.toString();
    }

    public static Pair<String, String> fromJSON(String text) {
        JSONObject object = new JSONObject(text);
        String username = (String) object.get("username");
        String message = (String) object.get("message");

        return new Pair<>(username, message);
    }

}
