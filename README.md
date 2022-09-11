# Ulak
## Simple Messaging Application

Practice on:
* Maven Project
* JavaFX
* Concurrency
* TCP

### How to run

* Create executables for both client side and server side of the project by running below command.
```
mvn package
```
* ulak_server.jar under target is server side executable file.
* ulak_client.jar under target is client side executable file.

Run Server:
`java --module-path %FX_HOME%\lib --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.base -jar server/target/ulak_server.jar`

Run Client:
`java --module-path %FX_HOME%\lib --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.base -jar client/target/ulak_client.jar`

## Screenshots

![SS](images/ss.png)