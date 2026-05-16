package org.example.client.screen;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.client.view.ViewLoader;

public class ScreenLoaderImpl implements ScreenLoader {
    private Stage primaryStage;
    private Scene scene;
    private BorderPane rootLayout;
    private ViewLoader viewLoader;
    private Screen currentScreen;

    private boolean isLoggedIn = false;

    public void init(Stage primaryStage, ViewLoader viewLoader) {
        this.primaryStage = primaryStage;
        this.viewLoader = viewLoader;
        this.rootLayout = new BorderPane();

        this.scene = new Scene(rootLayout, 1200, 800);
        this.primaryStage.setScene(scene);
    }

    public void load(Screen screen) {
        Parent view = viewLoader.loadView(screen.getFxmlPath());

        if (requiresAuth(screen)) {
            if (!isLoggedIn) {
                setupHeader();
                setupNavigation();
                isLoggedIn = true;
            }
            rootLayout.setCenter(view);
        } else {
            rootLayout.setTop(null);
            rootLayout.setLeft(null);
            rootLayout.setCenter(view);
        }

        primaryStage.setTitle("Budget Accounting - " + screen.name());
        currentScreen = screen;
        primaryStage.show();
    }

    private boolean requiresAuth(Screen screen) {
        return screen != Screen.AUTH && screen != Screen.REGISTRATION;
    }

    private void setupHeader() {
        Parent headerParent = viewLoader.loadView("/org/example/client/component/HeaderView.fxml");
        rootLayout.setTop(headerParent);
    }

    private void setupNavigation() {
        Parent navigationParent = viewLoader.loadView("/org/example/client/component/NavigationView.fxml");
        rootLayout.setLeft(navigationParent);
    }

    @Override
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    @Override
    public void logout() {
        isLoggedIn = false;
        rootLayout.setTop(null);
        rootLayout.setLeft(null);
        load(Screen.AUTH);
    }
}