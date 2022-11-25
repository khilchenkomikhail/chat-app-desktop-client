module ru.edu.spbstu.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens ru.edu.spbstu.client to javafx.fxml;
    opens ru.edu.spbstu.client.controllers to javafx.fxml;
    exports ru.edu.spbstu.client;
}