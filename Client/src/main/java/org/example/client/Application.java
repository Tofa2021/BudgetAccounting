package org.example.client;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.client.connection.ServerInteractionManager;
import org.example.client.connection.api.CachedClientAPIFactory;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoaderImpl;
import org.example.client.view.ViewLoader;
import org.example.client.viewModel.ViewModelFactory;

public class Application extends javafx.application.Application {
    private ServerInteractionManager serverInteractionManager;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        SessionContext sessionContext = new SessionContext();

        serverInteractionManager = new ServerInteractionManager(sessionContext);

        CachedClientAPIFactory clientAPIFactory = new CachedClientAPIFactory(serverInteractionManager);

        ScreenLoaderImpl screenLoader = new ScreenLoaderImpl();
        ViewModelFactory viewModelFactory = new ViewModelFactory(clientAPIFactory, screenLoader, sessionContext);
        ViewLoader viewLoader = new ViewLoader(viewModelFactory);
        screenLoader.init(stage, viewLoader);

        stage.setOnCloseRequest(event -> close());
        screenLoader.load(Screen.AUTH);
    }

    private void close() {
        serverInteractionManager.close();
        Platform.exit();
    }

    @Override
    public void stop() {
        close();
    }
}