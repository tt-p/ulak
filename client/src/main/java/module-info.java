module ulak.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires ulak.baseapp;

    opens com.ttp.client.controller to javafx.fxml;

    exports com.ttp.client.controller;
    exports com.ttp.client;

}
