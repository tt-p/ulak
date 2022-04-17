package com.ttp.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapConfig implements Config {

    private Map<String, String> map;

    public MapConfig() {
        map = new HashMap<>();
    }

    public MapConfig(Map<String, String> map) {
        this.map = map;
    }

    public String getConfigValue(String key) {
        return map.get(key);
    }

    @Override
    public String serialize() {
        return map.entrySet().stream()
                .map(e -> "%s=%s".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public Config deserializeFrom(List<String> lines) {
        Map<String, String> map = new HashMap<>();
        lines.forEach(line -> {
            String[] pair = line.split("=");
            map.put(pair[0], pair[1]);
        });
        this.map = map;
        return this;
    }
}
