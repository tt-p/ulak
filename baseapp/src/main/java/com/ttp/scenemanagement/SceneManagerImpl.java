package com.ttp.scenemanagement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SceneManagerImpl extends SceneManagerBase {

    private final Map<String, Value> valueMap = new HashMap<>();

    public SceneManagerImpl(URL resourceUrl, Stage primaryStage) {
        super(resourceUrl, primaryStage);
    }

    @Override
    public void switchScene(ScenePath scenePath) {
        Value value = loadValue(scenePath);
        primaryStage.setScene(value.scene);
        if (value.controller != null) {
            value.controller.init();
        }
    }

    @Override
    public AbstractController getController(ScenePath scenePath) {
        return loadValue(scenePath).controller;
    }

    private Value loadValue(ScenePath scenePath) {
        return valueMap.computeIfAbsent(scenePath.getPath(), path -> {
            try {
                FXMLLoader loader = new FXMLLoader(new URL("%s/%s".formatted(resourceUrl, path)));
                Parent parent = loader.load();
                AbstractController controller = loader.getController();
                if (controller != null) {
                    controller.setSceneManager(this);
                }
                return new Value(new Scene(parent), controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private static class Value {

        Scene scene;
        AbstractController controller;

        public Value() {
        }

        public Value(Scene scene, AbstractController controller) {
            this.scene = scene;
            this.controller = controller;
        }
    }
}
