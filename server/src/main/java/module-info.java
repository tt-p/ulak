module ulak.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires ulak.baseapp;

    opens com.ttp.server.controller to javafx.fxml;

    exports com.ttp.server;
}
