module org.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires org.example.common;
    requires java.xml;
    requires static lombok;

    opens org.example.client to javafx.fxml;
    exports org.example.client;
    exports org.example.client.controller;
    opens org.example.client.controller to javafx.fxml;
    exports org.example.client.viewModel;
    opens org.example.client.viewModel to javafx.fxml;
}