package com.ttp.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigFile {
    private final Path path;
    private Config config;

    public ConfigFile(Path path, Config config) {
        this.path = path;
        this.config = config;
    }

    public Path getPath() {
        return path;
    }

    public Config getConfig() {
        return config;
    }

    public boolean exists() {
        return Files.exists(path);
    }

    public void write(Config config) {
        try {
            Files.writeString(path, config.serialize());
            this.config = config;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config read() {
        try {
            this.config = config.deserializeFrom(Files.readAllLines(path));
            return this.config;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
