package org.example.client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;

@RequiredArgsConstructor
public class NavigationViewModel extends BaseViewModel {
    @Getter
    private final ScreenLoader screenLoader;
    @Getter
    private final ObjectProperty<Screen> currentScreen = new SimpleObjectProperty<>();

    @Override
    public void onViewShown() {
        if (screenLoader != null && screenLoader.getCurrentScreen() != null) {
            screenLoader.getCurrentScreen().addListener((obs, old, newScreen) -> {
                if (newScreen != null) {
                    currentScreen.set(newScreen);
                }
            });

            Screen initialScreen = screenLoader.getCurrentScreen().get();
            if (initialScreen != null) {
                currentScreen.set(initialScreen);
            }
        }
    }

    public void navigateTo(Screen screen) {
        if (screenLoader != null) {
            screenLoader.load(screen);
        }
    }
}