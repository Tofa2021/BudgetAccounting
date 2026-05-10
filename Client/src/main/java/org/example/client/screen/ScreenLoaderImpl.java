package org.example.client.screen;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.client.view.ViewLoader;

public class ScreenLoaderImpl implements ScreenLoader { // TODO check fields
    private Stage primaryStage;
    private Scene scene;
    private BorderPane rootLayout;
    private ViewLoader viewLoader;

    public void init(Stage primaryStage, ViewLoader viewLoader) {
        this.primaryStage = primaryStage;
        this.viewLoader = viewLoader;
        this.rootLayout = new BorderPane();
        this.scene = new Scene(rootLayout, 1200, 800);
        this.primaryStage.setScene(scene);
    }

    public void load(Screen screen) {
        Parent view = viewLoader.loadView(screen.getFxmlPath());
        rootLayout.setCenter(view);
        primaryStage.setTitle("Budget Accounting - " + screen.name());
        primaryStage.show();
    }
}
