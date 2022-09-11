package com.ttp.scenemanagement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SceneManagerImpl extends SceneManagerBase {

    private final Map<String, Pair<Scene, AbstractController>> map = new HashMap<>();

    public SceneManagerImpl(URL resourceUrl, Stage primaryStage) {
        super(resourceUrl, primaryStage);
    }

    @Override
    public void switchScene(ScenePath scenePath) {
        Pair<Scene, AbstractController> pair = loadValue(scenePath);
        primaryStage.setScene(pair.getKey());
        if (pair.getValue() != null) {
            pair.getValue().init();
        }
    }

    @Override
    public AbstractController getController(ScenePath scenePath) {
        return loadValue(scenePath).getValue();
    }

    private Pair<Scene, AbstractController> loadValue(ScenePath scenePath) {
        return map.computeIfAbsent(scenePath.getPath(), path -> {
            try {
                URL url = new URL("%s%s".formatted(
                        resourceUrl,
                        path
                ));
                FXMLLoader loader = new FXMLLoader(url);
                Parent parent = loader.load();
                AbstractController controller = loader.getController();
                if (controller != null) {
                    controller.setSceneManager(this);
                }
                return new Pair<>(new Scene(parent), controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

}
