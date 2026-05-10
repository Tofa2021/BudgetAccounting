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
    requires jdk.compiler;
    requires java.desktop;

    exports org.example.client;
    opens org.example.client to javafx.fxml;

    exports org.example.client.view;
    opens org.example.client.view to javafx.fxml;

    exports org.example.client.viewModel;
    opens org.example.client.viewModel to javafx.fxml;

    exports org.example.client.screen;
    opens org.example.client.screen to javafx.fxml;

    exports org.example.client.connection;
    opens org.example.client.connection to javafx.fxml;

    exports org.example.client.connection.api;
    opens org.example.client.connection.api to javafx.fxml;
}