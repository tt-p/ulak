package com.ttp.server.util;

import java.util.EventListener;

public interface ServerEventListener extends EventListener {

    void handleServerEvent(ServerEvent event);

}