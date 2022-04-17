package com.ttp.net;

public enum AppProtocol {

    HANDSHAKE("10"),
    REGISTER("11"),
    JOIN("12"),
    LEAVE("13"),
    TEXT("14"),

    OK("20"),
    CONNECTED("21"),
    NOTIFY_JOIN("22"),
    NOTIFY_LEAVE("23"),
    ERROR("51");

    private final String code;

    AppProtocol(String s) {
        this.code = s;
    }

    public static AppProtocol convert(String str) {
        return switch (str) {
            case "10" -> HANDSHAKE;
            case "11" -> REGISTER;
            case "12" -> JOIN;
            case "13" -> LEAVE;
            case "14" -> TEXT;
            case "20" -> OK;
            case "21" -> CONNECTED;
            case "22" -> NOTIFY_JOIN;
            case "23" -> NOTIFY_LEAVE;
            default -> ERROR;
        };
    }

    public String getCode() {
        return code;
    }
}
