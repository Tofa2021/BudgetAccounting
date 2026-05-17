package org.example.client.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;

@RequiredArgsConstructor
public class ApplicationViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    @Getter
    private final BooleanProperty navigationVisible = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty navigationManaged = new SimpleBooleanProperty(false);

    @Override
    public void onViewShown() {
        screenLoader.getCurrentScreen().addListener((observable, oldValue, newValue) -> {
            boolean isNavigationNeeded = isNavigationNeeded(newValue);
            navigationVisible.set(isNavigationNeeded);
            navigationManaged.set(isNavigationNeeded);
        });
    }

    private boolean isNavigationNeeded(Screen screen) {
        return screen != Screen.AUTH && screen != Screen.REGISTRATION;
    }
}
