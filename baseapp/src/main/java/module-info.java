module ulak.baseapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    exports com.ttp.lanucher;
    exports com.ttp.scenemanagement;
    exports com.ttp.config;
    exports com.ttp.net;
    exports com.ttp.concurrent;
    exports com.ttp.util;
}