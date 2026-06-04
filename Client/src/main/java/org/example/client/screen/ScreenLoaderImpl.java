package org.example.client.screen;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.example.Pair;
import org.example.client.view.ApplicationView;
import org.example.client.view.BaseView;
import org.example.client.view.ViewLoader;

public class ScreenLoaderImpl implements ScreenLoader {
    @Getter
    private final ObjectProperty<Screen> currentScreen = new SimpleObjectProperty<>();
    private Stage primaryStage;
    private Scene scene;
    private ApplicationView applicationView;
    private ViewLoader viewLoader;

    public void init(Stage primaryStage, ViewLoader viewLoader) {
        this.primaryStage = primaryStage;
        this.primaryStage.setMaximized(true);
        this.viewLoader = viewLoader;

        var loaded = viewLoader.loadApplicationView();
        this.applicationView = loaded.getSecond();
        Parent parent = loaded.getFirst();

        setupHeader();
        setupNavigation();

        this.scene = new Scene(parent, 1200, 800);
        this.primaryStage.setScene(scene);
        applicationView.show();
    }

    public void load(Screen screen) {
        Pair<Parent, ? extends BaseView<?>> loaded;
        if (screen == Screen.GRAPHICS) {
            loaded = viewLoader.loadBoundGraphicsViewModel();
        } else {
            loaded = viewLoader.loadBoundView(screen.getFxmlPath());
        }

        Parent parent = loaded.getFirst();
        BaseView<?> view = loaded.getSecond();

        // TODO if long loading add icon loading

        view.setApplicationView(applicationView);
        applicationView.setContent(parent);
        view.show();

        primaryStage.setTitle("Budget Accounting - " + screen.name());
        currentScreen.set(screen);
        primaryStage.show();
    }

    private void setupHeader() {
        Pair<Parent, ? extends BaseView<?>> loaded = viewLoader.loadBoundView("/org/example/client/component/HeaderView.fxml");
        loaded.getSecond().setApplicationView(applicationView);
        applicationView.setHeader(loaded.getFirst());
        loaded.getSecond().show();
    }

    private void setupNavigation() {
        Pair<Parent, ? extends BaseView<?>> loaded = viewLoader.loadBoundView("/org/example/client/component/NavigationView.fxml");
        loaded.getSecond().setApplicationView(applicationView);
        applicationView.setNavigation(loaded.getFirst());
        loaded.getSecond().show();
    }
}