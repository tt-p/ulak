package com.ttp.scenemanagement;

public abstract class ScenePath {

    private final String path;

    public ScenePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
